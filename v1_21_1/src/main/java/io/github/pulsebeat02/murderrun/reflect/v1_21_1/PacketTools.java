package io.github.pulsebeat02.murderrun.reflect.v1_21_1;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import io.github.pulsebeat02.murderrun.reflect.PacketToolAPI;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
    final byte newMask = glowing ? 0x40 : (byte) 0;
    final DataValue<?> newValue = DataValue.create(glowingAccessor, newMask);
    copy.set(0, newValue);

    final ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(id, copy);
    connection.send(packet);
  }
}
