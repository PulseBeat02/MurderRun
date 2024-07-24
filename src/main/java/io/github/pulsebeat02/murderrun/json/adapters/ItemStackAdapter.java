package io.github.pulsebeat02.murderrun.json.adapters;

import io.github.pulsebeat02.murderrun.json.GsonProvider;
import org.bukkit.inventory.ItemStack;
import com.google.gson.*;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

@Deprecated
public final class ItemStackAdapter
    implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

  @Override
  public ItemStack deserialize(
      final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
      throws JsonParseException {
    try {
      final String data = json.getAsString();
      final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decode(data));
      final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
      final ItemStack item;
      item = (ItemStack) dataInput.readObject();
      dataInput.close();
      return item;
    } catch (final IOException | ClassNotFoundException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public JsonElement serialize(
      final ItemStack src, final Type typeOfSrc, final JsonSerializationContext context) {
    final Gson gson = GsonProvider.getGson();
    try {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
      dataOutput.writeObject(src);
      dataOutput.close();
      final String data = new String(Base64Coder.encode(outputStream.toByteArray()));
      return gson.toJsonTree(data);
    } catch (final Exception e) {
      throw new AssertionError(e);
    }
  }
}
