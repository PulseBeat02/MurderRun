/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.reflect;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.reflect.versioning.ServerEnvironment;
import io.netty.channel.ChannelFuture;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public final class FallbackPacketTools implements PacketToolAPI {

  private static final Class<?> MAPPED_CONNECTION_CLASS;
  private static final List<ChannelFuture> CONNECTIONS;

  static {
    try {
      MAPPED_CONNECTION_CLASS = Class.forName("net.minecraft.network.NetworkManager");
      CONNECTIONS = getConnections();
    } catch (final Throwable e) {
      throw new AssertionError(e);
    }
  }

  @SuppressWarnings("unchecked")
  private static List<ChannelFuture> getConnections() throws Throwable {
    final Object connection = getConnectionHandle();
    final VarHandle handle = getConnectionsVarHandle();
    return (List<ChannelFuture>) handle.get(connection);
  }

  private static VarHandle getConnectionsVarHandle() throws ClassNotFoundException, IllegalAccessException {
    final Class<?> target = Class.forName("net.minecraft.server.network.ServerConnection");
    final MethodHandles.Lookup lookup = MethodHandles.lookup();
    final MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(target, lookup);
    try {
      return privateLookup.findVarHandle(target, "channels", List.class);
    } catch (final NoSuchFieldException | IllegalAccessException e) {
      try {
        return privateLookup.findVarHandle(target, "f", List.class);
      } catch (final NoSuchFieldException | IllegalAccessException ex) {
        throw new AssertionError(ex);
      }
    }
  }

  private static Object getConnectionHandle() throws Throwable {
    final Server craftServer = Bukkit.getServer();
    final MethodHandle getServerHandle = getServerHandle();
    final MethodHandles.Lookup lookup = MethodHandles.lookup();
    final Object dedicatedServer = getServerHandle.invoke(craftServer);
    final Class<?> dedicatedServerClass = dedicatedServer.getClass();
    final String connectionName = "net.minecraft.server.network.ServerConnection";
    final Class<?> connectionClass = Class.forName(connectionName);
    final MethodType getConnectionType = MethodType.methodType(connectionClass);
    final MethodHandle getConnectionHandle = lookup.findVirtual(dedicatedServerClass, "ah", getConnectionType);
    return getConnectionHandle.invoke(dedicatedServer);
  }

  private static MethodHandle getServerHandle() throws NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
    final String rev = ServerEnvironment.getNMSRevision();
    final String craftServerClass = "org.bukkit.craftbukkit.%s.CraftServer".formatted(rev);
    final Class<?> craftServerType = Class.forName(craftServerClass);
    final MethodHandles.Lookup lookup = MethodHandles.lookup();
    final String dedicatedServerClass = "net.minecraft.server.dedicated.DedicatedServer";
    final Class<?> dedicatedServerClassType = Class.forName(dedicatedServerClass);
    final MethodType methodType = MethodType.methodType(dedicatedServerClassType);
    return lookup.findVirtual(craftServerType, "getServer", methodType);
  }

  private final Table<Player, Location, Slime> slimes;

  public FallbackPacketTools() {
    this.slimes = HashBasedTable.create();
  }

  @Override
  public byte[] toByteArray(final ItemStack item) {
    try (
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)
    ) {
      dataOutput.writeObject(item);
      return outputStream.toByteArray();
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public ItemStack fromByteArray(final byte[] bytes) {
    try (
      final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
      final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)
    ) {
      return (ItemStack) dataInput.readObject();
    } catch (final IOException | ClassNotFoundException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public void setEntityGlowing(final Entity entity, final Player watcher, final boolean glowing) {
    entity.setGlowing(glowing); // limited functionality
  }

  @Override
  public void setBlockGlowing(final Player watcher, final Location target, final boolean glowing) {
    final Block block = target.getBlock(); // limited functionality
    final World world = block.getWorld();
    final Location blockLocation = block.getLocation();
    final double centerX = blockLocation.getBlockX() + 0.5;
    final double centerY = blockLocation.getBlockY();
    final double centerZ = blockLocation.getBlockZ() + 0.5;
    final Location spawnLocation = new Location(world, centerX, centerY, centerZ);
    if (glowing) {
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
      if (this.slimes.contains(watcher, target)) {
        return;
      }
      final Slime spawned = world.spawn(spawnLocation, Slime.class, slime -> {
        slime.setSize(2);
        slime.setGlowing(true);
        slime.setInvisible(true);
        slime.setAI(false);
        slime.setCollidable(false);
        slime.setInvulnerable(true);
        slime.setVisibleByDefault(false);
        watcher.showEntity(plugin, slime);
      });
      this.slimes.put(watcher, target, spawned);
    } else {
      final Slime slime = this.slimes.remove(watcher, target);
      if (slime != null) {
        slime.remove();
      }
    }
  }

  @Override
  public Class<?> getMappedConnectionClass() {
    return MAPPED_CONNECTION_CLASS;
  }

  @Override
  public List<ChannelFuture> getServerChannels() {
    return CONNECTIONS;
  }
}
