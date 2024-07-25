package io.github.pulsebeat02.murderrun.reflect.v1_21;

import com.mojang.serialization.Dynamic;
import io.github.pulsebeat02.murderrun.reflect.NMSUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.fixes.DataConverterTypes;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTReadLimiter;
import net.minecraft.nbt.DynamicOpsNBT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NMSImpl implements NMSUtils {

  @Override
  public byte[] toByteArray(final ItemStack item) {
    try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      final NBTTagCompound compoundTag = CraftItemStack.asNMSCopy(item).save(new NBTTagCompound());
      compoundTag.a("DataVersion", CraftMagicNumbers.INSTANCE.getDataVersion());
      NBTCompressedStreamTools.a(compoundTag, outputStream);
      return outputStream.toByteArray();
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public ItemStack fromByteArray(final byte[] bytes) {
    try (final ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
      final NBTTagCompound compound = NBTCompressedStreamTools.a(stream, NBTReadLimiter.a());
      final int dataVersion = compound.h("DataVersion");
      final NBTTagCompound converted =
          (NBTTagCompound)
              MinecraftServer.getServer()
                  .L
                  .update(
                      DataConverterTypes.t,
                      new Dynamic<>(DynamicOpsNBT.a, compound),
                      dataVersion,
                      CraftMagicNumbers.INSTANCE.getDataVersion())
                  .getValue();
      return CraftItemStack.asCraftMirror(net.minecraft.world.item.ItemStack.of(converted));
    } catch (final Exception exception) {
      throw new RuntimeException(exception);
    }
  }
}
