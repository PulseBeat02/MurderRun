package io.github.pulsebeat02.murderrun.utils;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_RED;

import com.google.common.io.BaseEncoding;
import io.github.pulsebeat02.murderrun.locale.LocaleTools;
import io.github.pulsebeat02.murderrun.locale.Sender;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class AdventureUtils {

  private static final LegacyComponentSerializer SERIALIZER = BukkitComponentSerializer.legacy();
  private static final TextComponent UNSUPPORTED = text("ERROR WRAPPING").color(DARK_RED);
  private static final String COMPONENT_REGEX = "(?<=\\s)|(?=\\n)";

  private AdventureUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Component createLocationComponent(
      final LocaleTools.TriComponent<Sender, Integer, Integer, Integer> function,
      final Location location) {
    final int x = location.getBlockX();
    final int y = location.getBlockY();
    final int z = location.getBlockZ();
    return function.build(x, y, z);
  }

  public static String serializeComponentToLegacyString(final Component component) {
    return SERIALIZER.serialize(component);
  }

  @Deprecated // when Adventure platform implements new resourcepack logic use that
  public static boolean sendPacksLegacy(final Player player, final ResourcePackRequest request) {

    final List<ResourcePackInfo> packs = request.packs();
    if (request.replace()) {
      player.removeResourcePacks();
    }

    final boolean required = request.required();
    Component prompt = request.prompt();
    if (prompt == null) {
      prompt = empty();
    }

    final String legacy = serializeComponentToLegacyString(prompt);
    for (final ResourcePackInfo info : packs) {
      final URI uri = info.uri();
      final String url = uri.toASCIIString();
      final String hexHash = info.hash();
      final byte[] hash = BaseEncoding.base16().decode(hexHash.toUpperCase());
      final UUID id = info.id();
      player.addResourcePack(id, url, hash, legacy, required);
    }

    return true;
  }

  public static List<String> serializeLoreToLegacyLore(final List<Component> lore) {
    final List<String> rawLore = new ArrayList<>();
    for (final Component component : lore) {
      rawLore.add(serializeComponentToLegacyString(component));
    }
    return rawLore;
  }

  public static List<String> serializeLoreToLegacyLore(final Component lore) {
    final List<Component> wrapped = AdventureUtils.wrapLoreLines(lore, 40);
    final List<String> rawLore = new ArrayList<>();
    for (final Component component : wrapped) {
      rawLore.add(serializeComponentToLegacyString(component));
    }
    return rawLore;
  }

  public static List<Component> wrapLoreLines(final Component component, final int length) {

    if (!(component instanceof final TextComponent text)) {
      return Collections.singletonList(component);
    }

    final List<Component> wrapped = new ArrayList<>();
    final List<TextComponent> parts = flattenTextComponents(text);

    Component currentLine = empty();
    int lineLength = 0;

    for (final TextComponent part : parts) {

      final Style style = part.style();
      final String content = part.content();
      final String[] words = content.split(COMPONENT_REGEX);

      for (final String word : words) {

        if (word.isEmpty()) {
          continue;
        }

        final int wordLength = word.length();
        final int totalLength = lineLength + wordLength;
        if (totalLength > length || word.contains("\n")) {
          wrapped.add(currentLine);
          currentLine = empty().style(style);
          lineLength = 0;
        }

        if (!word.equals("\n")) {
          currentLine = currentLine.append(text(word).style(style));
          lineLength += wordLength;
        }
      }
    }

    if (lineLength > 0) {
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
    return flattened;
  }

  private static Style enforceStyleStates(final Style style) {
    final Style.Builder builder = style.toBuilder();
    final Map<TextDecoration, State> map = style.decorations();
    map.forEach((decoration, state) -> {
      if (state == TextDecoration.State.NOT_SET) {
        builder.decoration(decoration, false);
      }
    });
    return builder.build();
  }
}
