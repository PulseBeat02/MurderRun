package io.github.pulsebeat02.murderrun.reflect.v1_21_R4;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.Ints;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import io.github.pulsebeat02.murderrun.reflect.PacketToolAPI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import io.netty.channel.ChannelFuture;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import org.bukkit.*;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public class PacketTools implements PacketToolAPI {

  private static final String ITEMS_VERSION_ATTRIBUTE = "DataVersion";
  private static final VarHandle SERVER_CONNECTION_HANDLE = getServerConnectionHandle();

  private static VarHandle getServerConnectionHandle() {
    final Class<?> target = ServerConnectionListener.class;
    final MethodHandles.Lookup lookup = MethodHandles.lookup();
    try {
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
    } catch (final IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  private final Table<Player, Location, Slime> glowBlocks;
  private final Team noCollisions;

  public PacketTools() {
    this.glowBlocks = HashBasedTable.create();
    this.noCollisions = this.registerTeam();
  }

  private Team registerTeam(@UnderInitialization PacketTools this) {
    final ScoreboardManager manager = Bukkit.getScoreboardManager();
    final Scoreboard scoreboard = manager.getMainScoreboard();
    final Team team = scoreboard.getTeam("NoCollisions");
    if (team != null) {
      return team;
    }

    final Team newTeam = scoreboard.registerNewTeam("NoCollisions");
    newTeam.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);

    return newTeam;
  }

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
              dimension,
              newCompound
      );
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
    final EntityDataAccessor<Byte> glowingAccessor = new EntityDataAccessor<>(0, EntityDataSerializers.BYTE);
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
  public void setBlockGlowing(final Player watcher, final Location target, final boolean glowing) {
    if (!this.noCollisions.hasEntity(watcher)) {
      final String name = watcher.getName();
      this.noCollisions.addEntry(name);
    }

    final CraftPlayer player = (CraftPlayer) watcher;
    final ServerPlayer handle = player.getHandle();
    final ServerGamePacketListenerImpl connection = handle.connection;

    if (glowing) {
      this.createGlowingSlime0(target, player, connection);
    } else {
      this.removeGlowingSlime0(target, player, connection);
    }
  }

  private void removeGlowingSlime0(
          final Location target,
          final CraftPlayer player,
          final ServerGamePacketListenerImpl connection
  ) {
    final Slime value = this.glowBlocks.get(player, target);
    if (value == null) {
      return;
    }

    final int id = value.getId();
    this.removeEntity(connection, Set.of(id));
    this.glowBlocks.remove(player, target);
  }

  private void createGlowingSlime0(
          final Location target,
          final CraftPlayer player,
          final ServerGamePacketListenerImpl connection
  ) {
    final Slime existing = this.glowBlocks.get(player, target);
    if (existing != null) {
      return;
    }

    final World world = target.getWorld();
    final CraftWorld craftWorld = (CraftWorld) world;
    final ServerLevel nmsWorld = craftWorld.getHandle();
    final Slime slime = new Slime(EntityType.SLIME, nmsWorld);
    slime.setInvisible(true);
    slime.setGlowingTag(true);
    slime.setSize(2, false);
    slime.setInvulnerable(true);
    slime.setNoAi(true);
    slime.setRot(0, 0);
    slime.setPos(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
    slime.setYBodyRot(0);
    slime.setYHeadRot(0);

    final ServerEntity entity = new ServerEntity(nmsWorld, slime, 0, false, ignored -> {
    }, Set.of());
    final ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(slime, entity);
    connection.send(packet);

    final int id = slime.getId();
    final SynchedEntityData data = slime.getEntityData();
    List<DataValue<?>> values = data.getNonDefaultValues();
    if (values == null) {
      values = new ArrayList<>();
    }

    final List<DataValue<?>> copy = new ArrayList<>(values);
    copy.removeIf(value -> value.id() == 0);

    final byte newMask = 0x20 | 0x40;
    final EntityDataAccessor<Byte> accessor = new EntityDataAccessor<>(0, EntityDataSerializers.BYTE);
    final DataValue<?> newValue = DataValue.create(accessor, newMask);
    copy.addFirst(newValue);

    final ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(id, copy);
    connection.send(dataPacket);

    final String uuid = slime.getStringUUID();
    this.noCollisions.addEntry(uuid);
    this.glowBlocks.put(player, target, slime);
  }

  private void removeEntity(final ServerGamePacketListenerImpl connection, final Collection<Integer> ids) {
    final int[] remove = Ints.toArray(ids);
    final ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(remove);
    connection.send(packet);
  }

  @Override
  public Class<?> getMappedConnectionClass() {
    return Connection.class;
  }

  @Override
  public List<ChannelFuture> getServerChannels() {
    final Server server = Bukkit.getServer();
    final CraftServer craftServer = (CraftServer) server;
    final DedicatedServer dedicated = craftServer.getServer();
    final ServerConnectionListener connection = dedicated.getConnection();
    return (List<ChannelFuture>) SERVER_CONNECTION_HANDLE.get(connection);
  }
}
