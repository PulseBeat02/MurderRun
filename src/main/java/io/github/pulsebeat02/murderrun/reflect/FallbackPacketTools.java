package io.github.pulsebeat02.murderrun.reflect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

@Deprecated
public final class FallbackPacketTools implements PacketToolAPI {

  @Override
  public byte[] toByteArray(final ItemStack item) {
    try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
      dataOutput.writeObject(item);
      return outputStream.toByteArray();
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public ItemStack fromByteArray(final byte[] bytes) {
    try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
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
    throw new UnsupportedOperationException(
        "Can't set block glowing! Use a different pack provider solution");
  }

  @Override
  public void injectNettyHandler(final String key, final Object handler) {
    throw new UnsupportedOperationException(
        "Can't inject into Netty handler! Use a different pack provider solution");
  }
}
