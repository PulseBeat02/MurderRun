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
package me.brandonli.murderrun.utils;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

import com.google.common.io.BaseEncoding;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.brandonli.murderrun.locale.LocaleTools;
import me.brandonli.murderrun.locale.Sender;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ComponentUtils {

  private static final LegacyComponentSerializer LEGACY_SERIALIZER = BukkitComponentSerializer.legacy();
  private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.plainText();
  private static final Pattern PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
  private static final Map<ChatColor, NamedTextColor> COLOR_MAP = new HashMap<>();

  static {
    final ChatColor[] chatColors = ChatColor.values();
    for (final ChatColor chatColor : chatColors) {
      if (!chatColor.isColor()) {
        continue;
      }
      final String name = chatColor.name();
      final String lower = name.toLowerCase(Locale.ROOT);
      final NamedTextColor namedTextColor = requireNonNull(NamedTextColor.NAMES.value(lower));
      COLOR_MAP.put(chatColor, namedTextColor);
    }
  }

  private ComponentUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static NamedTextColor getNamedTextColor(final ChatColor chatColor) {
    return COLOR_MAP.getOrDefault(chatColor, NamedTextColor.WHITE);
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
    return LEGACY_SERIALIZER.serialize(component);
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
    return LEGACY_SERIALIZER.deserialize(legacy);
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

  public static List<Component> wrapLoreLines(final Component component, final int maxWidth) {
    final String legacyString = LEGACY_SERIALIZER.serialize(component);
    final List<String> wrappedStrings = wrapLegacyString(legacyString, maxWidth);
    final List<Component> wrappedComponents = new ArrayList<>();
    for (final String line : wrappedStrings) {
      final Component loreComponent = LEGACY_SERIALIZER.deserialize(line);
      wrappedComponents.add(loreComponent);
    }
    return wrappedComponents;
  }

  private static List<String> wrapLegacyString(final String legacyString, final int maxWidth) {
    final List<String> lines = new ArrayList<>();
    StringBuilder currentLine = new StringBuilder();
    String currentFormat = "";
    final String[] words = legacyString.split(" ");
    for (final String word : words) {
      final String candidate = currentLine.isEmpty() ? word : currentLine + " " + word;
      if (getStrippedLength(candidate) > maxWidth) {
        if (!currentLine.isEmpty()) {
          lines.add(currentLine.toString());
          currentFormat = getLastColors(currentLine.toString());
        }
        currentLine = new StringBuilder(currentFormat);
        currentLine.append(word);
      } else {
        if (!currentLine.isEmpty()) {
          currentLine.append(" ");
        }
        currentLine.append(word);
      }
    }
    if (!currentLine.isEmpty()) {
      lines.add(currentLine.toString());
    }
    return lines;
  }

  private static int getStrippedLength(final String text) {
    return text.replaceAll("(?i)§[0-9A-FK-OR]", "").length();
  }

  private static String getLastColors(final String text) {
    final StringBuilder lastColors = new StringBuilder();
    final Matcher matcher = PATTERN.matcher(text);
    while (matcher.find()) {
      final String code = matcher.group();
      if (code.equalsIgnoreCase("§r")) {
        lastColors.setLength(0);
      } else {
        lastColors.append(code);
      }
    }
    return lastColors.toString();
  }
}
