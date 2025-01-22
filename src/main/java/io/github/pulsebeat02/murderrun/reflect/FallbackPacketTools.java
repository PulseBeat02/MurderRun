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

import io.netty.channel.ChannelFuture;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public final class FallbackPacketTools implements PacketToolAPI {

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
    throw new UnsupportedOperationException("Can't set block glowing! Use a different pack provider solution");
  }

  @Override
  public Class<?> getMappedConnectionClass() {
    throw new UnsupportedOperationException("Unable to get mapped connection class!");
  }

  @Override
  public List<ChannelFuture> getServerChannels() {
    throw new UnsupportedOperationException("Unable to get server channels!");
  }
}
