package io.github.pulsebeat02.murderrun.reflect.v1_21_1;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import io.github.pulsebeat02.murderrun.reflect.PacketToolAPI;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.RelativeMovement;
import org.bukkit.Bukkit;
import org.bukkit.UnsafeValues;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PacketTools implements PacketToolAPI {

  private static final String ITEMS_VERSION_ATTRIBUTE = "DataVersion";
  private static final SplittableRandom RANDOM = new SplittableRandom();

  @Override
  public byte[] toByteArray(final ItemStack item) {
    try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      final MinecraftServer server = MinecraftServer.getServer();
      final UnsafeValues values = Bukkit.getUnsafe();
      final int version = values.getDataVersion();
      final RegistryAccess.Frozen dimension = server.registryAccess();
      final net.minecraft.world.item.ItemStack craftItemStack = CraftItemStack.asNMSCopy(item);
      final CompoundTag compound = (CompoundTag) craftItemStack.save(dimension);
      compound.putInt(ITEMS_VERSION_ATTRIBUTE, version);
      NbtIo.writeCompressed(compound, outputStream);
      return outputStream.toByteArray();
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public ItemStack fromByteArray(final byte[] bytes) {
    try (final ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
      final MinecraftServer server = MinecraftServer.getServer();
      final RegistryAccess.Frozen dimension = server.registryAccess();
      final NbtAccounter unlimited = NbtAccounter.unlimitedHeap();
      final CompoundTag old = NbtIo.readCompressed(stream, unlimited);
      final int dataVersion = old.getInt(ITEMS_VERSION_ATTRIBUTE);
      final UnsafeValues values = Bukkit.getUnsafe();
      final int ver = values.getDataVersion();
      final DSL.TypeReference reference = References.ITEM_STACK;
      final DataFixer fixer = server.fixerUpper;
      final NbtOps operation = NbtOps.INSTANCE;
      final Dynamic<Tag> dynamic = new Dynamic<>(operation, old);
      fixer.update(reference, dynamic, dataVersion, ver);
      final CompoundTag newCompound = (CompoundTag) dynamic.getValue();
      final net.minecraft.world.item.ItemStack stack = net.minecraft.world.item.ItemStack.parseOptional(
          dimension, newCompound);
      return CraftItemStack.asCraftMirror(stack);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public void setEntityGlowing(final Entity entity, final Player watcher, final boolean glowing) {

    final CraftEntity glow = (CraftEntity) entity;
    final net.minecraft.world.entity.Entity nmsEntity = glow.getHandle();
    final SynchedEntityData data = nmsEntity.getEntityData();
    final EntityDataAccessor<Byte> glowingAccessor = new EntityDataAccessor<>(0,
        EntityDataSerializers.BYTE);
    final CraftPlayer player = (CraftPlayer) watcher;
    final ServerPlayer handle = player.getHandle();
    final ServerGamePacketListenerImpl connection = handle.connection;
    final int id = entity.getEntityId();
    final List<DataValue<?>> packed = data.getNonDefaultValues();
    if (packed == null) {
      return;
    }

    final List<DataValue<?>> copy = new ArrayList<>(packed);

    boolean found = false;
    for (int i = 0; i < packed.size(); i++) {
      final DataValue<?> value = packed.get(i);
      final int valueID = value.id();
      if (valueID == 0) {
        final byte valueMask = (byte) value.value();
        final byte newMask = (byte) (glowing ? (valueMask | 0x40) : (valueMask & ~0x40));
        final DataValue<?> newValue = DataValue.create(glowingAccessor, newMask);
        copy.set(i, newValue);
        found = true;
        break;
      }
    }

    if (!found) {
      final byte newMask = glowing ? 0x40 : (byte) 0;
      final DataValue<?> newValue = DataValue.create(glowingAccessor, newMask);
      copy.addFirst(newValue);
    }

    final ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(id, copy);
    connection.send(packet);
  }

  @Override
  public void injectNettyHandler(final Object handler) {
    final MinecraftServer server = MinecraftServer.getServer();
    final ServerConnectionListener serverConnection = server.getConnection();
    final List<Connection> connections = serverConnection.getConnections();
    for (final Connection connection : connections) {
      final Channel channel = connection.channel;
      final ChannelPipeline pipeline = channel.pipeline();
      pipeline.addFirst((ChannelHandler) handler);
    }
  }

  // wtf??!?!??!!? troll?!?!?
  public void crashPlayerClient(final Player player) {
    final ClientboundPlayerPositionPacket packet = new ClientboundPlayerPositionPacket(this.d(),
        this.d(), this.d(), this.f(), this.f(), RelativeMovement.unpack(this.b()), this.i());
    final CraftPlayer craftPlayer = (CraftPlayer) player;
    final ServerPlayer handle = craftPlayer.getHandle();
    final ServerGamePacketListenerImpl connection = handle.connection;
    connection.send(packet);
  }

  private double d() {
    final double qs = Double.MAX_VALUE;
    final double mj43 = RANDOM.nextDouble();
    final double p6 = .75;
    final double tp9 = .5;
    return qs * ((mj43 * (((Math.sqrt(mj43) * 564 % 1) * p6) - (Math.pow(mj43, 2) % 1) * tp9) + tp9));
  }

  private float f() {
    final float y8xafa = Float.MAX_VALUE;
    final double zs39asa = RANDOM.nextDouble();
    final double r3s1 = .75;
    final double d9fs2 = .5;
    return y8xafa * ((float) (zs39asa * (((Math.sqrt(zs39asa) * 564 % 1) * r3s1) - (Math.pow(zs39asa, 2) % 1) * d9fs2) + d9fs2));
  }

  private byte b() {
    final byte q4Retv = Byte.MAX_VALUE;
    final double er99 = RANDOM.nextDouble();
    final double lr625 = .75;
    final double wf7125 = .5;
    return (byte) (q4Retv * ((er99 * (((Math.sqrt(er99) * 564 % 1) * lr625) - (Math.pow(er99, 2) % 1) * wf7125)) + wf7125));
  }

  private int i() {
    final int rq4s = Integer.MAX_VALUE;
    final double b45jhh = RANDOM.nextDouble();
    final double cr75 = .75;
    final double ds852 = .5;
    return rq4s * (int) ((b45jhh * (((Math.sqrt(b45jhh) * 564 % 1) * cr75) - (Math.pow(b45jhh, 2) % 1) * ds852)) + ds852);
  }
}
