package io.github.pulsebeat02.murderrun.reflect.v1_21;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import io.github.pulsebeat02.murderrun.reflect.NMSUtils;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.fixes.DataConverterTypes;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NMSUtilsImpl implements NMSUtils {

  @Override
  public byte[] toByteArray(final ItemStack item) {
    try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      final int version = Bukkit.getUnsafe().getDataVersion();
      final IRegistryCustom.Dimension dimension = MinecraftServer.getServer().bc();
      final net.minecraft.world.item.ItemStack craftItemStack = CraftItemStack.asNMSCopy(item);
      final NBTTagCompound compound = (NBTTagCompound) craftItemStack.a(dimension);
      compound.a("DataVersion", version);
      NBTCompressedStreamTools.a(compound, outputStream);
      return outputStream.toByteArray();
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public ItemStack fromByteArray(final byte[] bytes) {
    try (final ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
      final NBTReadLimiter unlimited = NBTReadLimiter.a();
      final NBTTagCompound old = NBTCompressedStreamTools.a(stream, unlimited);
      final int dataVersion = old.h("DataVersion");
      final int ver = Bukkit.getUnsafe().getDataVersion();
      final DSL.TypeReference reference = DataConverterTypes.t;
      final MinecraftServer server = MinecraftServer.getServer();
      final DataFixer fixer = server.L;
      final Dynamic<NBTBase> dynamic = new Dynamic<>(DynamicOpsNBT.a, old);
      fixer.update(reference, dynamic, dataVersion, ver);
      final NBTTagCompound newCompound = (NBTTagCompound) dynamic.getValue();
      return CraftItemStack.asCraftMirror(
          net.minecraft.world.item.ItemStack.a(MinecraftServer.getServer().bc(), newCompound));
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }
}
