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
package io.github.pulsebeat02.murderrun.gson.adapters;

import com.google.gson.*;
import io.github.pulsebeat02.murderrun.gson.GsonProvider;
import io.github.pulsebeat02.murderrun.reflect.PacketToolAPI;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import java.lang.reflect.Type;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public final class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

  @Override
  public ItemStack deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
    throws JsonParseException {
    final PacketToolAPI api = PacketToolsProvider.PACKET_API;
    final String data = json.getAsString();
    final byte[] bytes = Base64Coder.decode(data);
    return api.fromByteArray(bytes);
  }

  @Override
  public JsonElement serialize(final ItemStack src, final Type typeOfSrc, final JsonSerializationContext context) {
    final Gson gson = GsonProvider.getGson();
    final PacketToolAPI api = PacketToolsProvider.PACKET_API;
    final byte[] bytes = api.toByteArray(src);
    final char[] base64 = Base64Coder.encode(bytes);
    final String data = new String(base64);
    return gson.toJsonTree(data);
  }
}
