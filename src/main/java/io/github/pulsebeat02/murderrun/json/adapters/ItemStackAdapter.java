package io.github.pulsebeat02.murderrun.json.adapters;

import io.github.pulsebeat02.murderrun.json.GsonProvider;
import io.github.pulsebeat02.murderrun.reflect.NMSHandler;
import org.bukkit.inventory.ItemStack;
import com.google.gson.*;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

public final class ItemStackAdapter
    implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

  @Override
  public ItemStack deserialize(
      final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
      throws JsonParseException {
    final String data = json.getAsString();
    final byte[] bytes = Base64Coder.decode(data);
    return NMSHandler.NMS_UTILS.fromByteArray(bytes);
  }

  @Override
  public JsonElement serialize(
      final ItemStack src, final Type typeOfSrc, final JsonSerializationContext context) {
    final Gson gson = GsonProvider.getGson();
    final byte[] bytes = NMSHandler.NMS_UTILS.toByteArray(src);
    final char[] base64 = Base64Coder.encode(bytes);
    final String data = new String(base64);
    return gson.toJsonTree(data);
  }
}
