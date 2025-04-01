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
package io.github.pulsebeat02.murderrun.utils;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_RED;

import com.google.common.io.BaseEncoding;
import io.github.pulsebeat02.murderrun.locale.LocaleTools;
import io.github.pulsebeat02.murderrun.locale.Sender;
import java.net.URI;
import java.util.*;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.KeyFor;

public final class ComponentUtils {

  private static final LegacyComponentSerializer SERIALIZER = BukkitComponentSerializer.legacy();
  private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.plainText();
  private static final TextComponent UNSUPPORTED = text("ERROR WRAPPING").color(DARK_RED);
  private static final String COMPONENT_REGEX = "(?<=\\s)|(?=\\n)";

  private ComponentUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Component createLocationComponent(
    final LocaleTools.TriComponent<Sender, Integer, Integer, Integer> function,
    final Location location
  ) {
    final int x = location.getBlockX();
    final int y = location.getBlockY();
    final int z = location.getBlockZ();
    return function.build(x, y, z);
  }

  public static String serializeComponentToPlain(final Component component) {
    return PLAIN_SERIALIZER.serialize(component);
  }

  public static String serializeComponentToLegacyString(final Component component) {
    return SERIALIZER.serialize(component);
  }

  public static boolean sendPacksLegacy(final Player player, final ResourcePackRequest request) {
    if (request == null) {
      return false;
    }

    if (request.replace()) {
      player.removeResourcePacks();
    }

    final List<ResourcePackInfo> packs = request.packs();
    final boolean required = request.required();
    final Component prompt = getResourcePackPrompt(request);
    final String legacy = serializeComponentToLegacyString(prompt);
    final BaseEncoding encoding = BaseEncoding.base16();
    for (final ResourcePackInfo info : packs) {
      final URI uri = info.uri();
      final String url = uri.toASCIIString();
      final String hexHash = info.hash();
      final String upperHexHash = hexHash.toUpperCase();
      final byte[] hash = encoding.decode(upperHexHash);
      final UUID id = info.id();
      player.addResourcePack(id, url, hash, legacy, required);
    }

    /*
    Alright, I know this isn't really a utility function because it has side effects, but let's say there is a
    one in the billionth chance that a photon causes a bit-switch to happen, causing this to return false
    instead. I mean that means it could return false, and it is a utility function and is pure, right? Right?!
     */
    return true;
  }

  private static Component getResourcePackPrompt(final ResourcePackRequest request) {
    Component prompt = request.prompt();
    if (prompt == null) {
      prompt = empty();
    }
    return prompt;
  }

  public static Component deserializeLegacyStringToComponent(final String legacy) {
    return SERIALIZER.deserialize(legacy);
  }

  public static List<String> serializeLoreToLegacyLore(final List<Component> lore) {
    final List<String> rawLore = new ArrayList<>();
    for (final Component component : lore) {
      rawLore.add(serializeComponentToLegacyString(component));
    }
    return rawLore;
  }

  public static List<String> serializeLoreToLegacyLore(final Component lore) {
    final List<Component> wrapped = ComponentUtils.wrapLoreLines(lore, 40);
    return serializeLoreToLegacyLore(wrapped);
  }

  public static List<Component> wrapLoreLines(final Component component, final int length) {
    if (!(component instanceof final TextComponent text)) {
      return Collections.singletonList(component);
    }

    final List<Component> wrapped = new ArrayList<>();
    final List<TextComponent> parts = flattenTextComponents(text);

    Component currentLine = empty();
    final int[] lineLength = new int[] { 0 };

    for (final TextComponent part : parts) {
      final Style style = part.style();
      final String content = part.content();
      final String[] words = content.split(COMPONENT_REGEX);

      for (final String word : words) {
        if (word.isEmpty()) {
          continue;
        }

        final int wordLength = word.length();
        final int totalLength = lineLength[0] + wordLength;
        if (totalLength > length || word.contains("\n")) {
          wrapped.add(currentLine);
          currentLine = empty().style(style);
          lineLength[0] = 0;
        }

        if (!word.equals("\n")) {
          currentLine = currentLine.append(text(word).style(style));
          lineLength[0] += wordLength;
        }
      }
    }

    if (lineLength[0] > 0) {
      wrapped.add(currentLine);
    }

    return wrapped;
  }

  private static List<TextComponent> flattenTextComponents(final TextComponent component) {
    final List<TextComponent> flattened = new ArrayList<>();
    final Style style = component.style();
    final Style enforcedState = enforceStyleStates(style);
    final TextComponent first = component.style(enforcedState);

    final Stack<TextComponent> toCheck = new Stack<>();
    toCheck.add(first);

    while (!toCheck.empty()) {
      final TextComponent parent = toCheck.pop();
      final String content = parent.content();
      if (!content.isEmpty()) {
        flattened.add(parent);
      }
      addChildComponents(parent, toCheck);
    }

    return flattened;
  }

  private static void addChildComponents(final TextComponent parent, final Stack<TextComponent> toCheck) {
    final List<Component> children = parent.children();
    final List<Component> reversed = children.reversed();
    for (final Component child : reversed) {
      if (child instanceof final TextComponent text) {
        final Style parentStyle = parent.style();
        final Style textStyle = text.style();
        final Style merge = parentStyle.merge(textStyle);
        final TextComponent childComponent = text.style(merge);
        toCheck.add(childComponent);
      } else {
        toCheck.add(UNSUPPORTED);
      }
    }
  }

  private static Style enforceStyleStates(final Style style) {
    final Style.Builder builder = style.toBuilder();
    final Map<TextDecoration, State> map = style.decorations();
    final Set<Map.Entry<@KeyFor("map") TextDecoration, State>> entries = map.entrySet();
    for (final Map.Entry<TextDecoration, State> entry : entries) {
      final TextDecoration decoration = entry.getKey();
      final State state = entry.getValue();
      if (state == TextDecoration.State.NOT_SET) {
        builder.decoration(decoration, false);
      }
    }
    return builder.build();
  }
}
