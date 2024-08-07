package io.github.pulsebeat02.murderrun.utils;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.LocaleTooling;
import io.github.pulsebeat02.murderrun.locale.Sender;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundKeys;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;

public final class ComponentUtils {

  private static final LegacyComponentSerializer SERIALIZER = BukkitComponentSerializer.legacy();
  private static final Map<Character, Character> SMALL_CAPS_FONT = new HashMap<>();

  static {
    SMALL_CAPS_FONT.put('a', 'ᴀ');
    SMALL_CAPS_FONT.put('b', 'ʙ');
    SMALL_CAPS_FONT.put('c', 'ᴄ');
    SMALL_CAPS_FONT.put('d', 'ᴅ');
    SMALL_CAPS_FONT.put('e', 'ᴇ');
    SMALL_CAPS_FONT.put('f', 'ꜰ');
    SMALL_CAPS_FONT.put('g', 'ɢ');
    SMALL_CAPS_FONT.put('h', 'ʜ');
    SMALL_CAPS_FONT.put('i', 'ɪ');
    SMALL_CAPS_FONT.put('j', 'ᴊ');
    SMALL_CAPS_FONT.put('k', 'ᴋ');
    SMALL_CAPS_FONT.put('l', 'ʟ');
    SMALL_CAPS_FONT.put('m', 'ᴍ');
    SMALL_CAPS_FONT.put('n', 'ɴ');
    SMALL_CAPS_FONT.put('o', 'ᴏ');
    SMALL_CAPS_FONT.put('p', 'ᴘ');
    SMALL_CAPS_FONT.put('q', 'q');
    SMALL_CAPS_FONT.put('r', 'ʀ');
    SMALL_CAPS_FONT.put('s', 'ꜱ');
    SMALL_CAPS_FONT.put('t', 'ᴛ');
    SMALL_CAPS_FONT.put('u', 'ᴜ');
    SMALL_CAPS_FONT.put('v', 'ᴠ');
    SMALL_CAPS_FONT.put('w', 'ᴡ');
    SMALL_CAPS_FONT.put('x', 'x');
    SMALL_CAPS_FONT.put('y', 'ʏ');
    SMALL_CAPS_FONT.put('z', 'ᴢ');
    SMALL_CAPS_FONT.put('{', '{');
    SMALL_CAPS_FONT.put('|', '|');
    SMALL_CAPS_FONT.put('}', '}');
    SMALL_CAPS_FONT.put('~', '˜');
  }

  private ComponentUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Component createLocationComponent(
      final LocaleTooling.TriComponent<Sender, Integer, Integer, Integer> function,
      final Location location) {
    final int x = location.getBlockX();
    final int y = location.getBlockY();
    final int z = location.getBlockZ();
    return function.build(x, y, z);
  }

  public static String serializeComponentToLegacy(final Component component) {
    return SERIALIZER.serialize(component);
  }

  public static void playSoundForAllParticipants(final Game game, final SoundKeys... keys) {
    final String key = getRandomKey(keys);
    final Key id = key(key);
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(player -> player.playSound(id, Source.MASTER, 1f, 1f));
  }

  private static String getRandomKey(final SoundKeys... keys) {
    final int bound = keys.length;
    final int random = RandomUtils.generateInt(bound);
    final SoundKeys chosen = keys[random];
    return chosen.getSoundName();
  }

  public static void playSoundForAllParticipants(final Game game, final String... keys) {
    final String id = getRandomKey(keys);
    final Key key = key(id);
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(player -> player.playSound(key, Source.MASTER, 1f, 1f));
  }

  private static String getRandomKey(final String... keys) {
    final int bound = keys.length;
    final int random = RandomUtils.generateInt(bound);
    return keys[random];
  }

  public static void playSoundForAllMurderers(final Game game, final SoundKeys... keys) {
    final String key = getRandomKey(keys);
    final Key id = key(key);
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllDead(player -> {
      final Location location = player.getLocation();
      player.playSound(id, Source.MASTER, 1f, 1f);
    });
  }

  public static void playSoundForAllInnocents(final Game game, final SoundKeys... keys) {
    final String key = getRandomKey(keys);
    final Key id = key(key);
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllInnocents(innocent -> innocent.playSound(id, Source.MASTER, 1f, 1f));
  }

  public static void playSoundForAllParticipantsAtLocation(
      final Location origin, final SoundKeys... keys) {
    final String key = getRandomKey(keys);
    final World world = requireNonNull(origin.getWorld());
    world.playSound(origin, key, SoundCategory.MASTER, 1f, 1f);
  }

  public static void showTitleForAllParticipants(
      final Game game, final Component title, final Component subtitle) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(player -> player.showTitle(title, subtitle));
  }

  public static void showTitleForAllMurderers(
      final Game game, final Component title, final Component subtitle) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllMurderers(murderer -> murderer.showTitle(title, subtitle));
  }

  public static void showTitleForAllInnocents(
      final Game game, final Component title, final Component subtitle) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllInnocents(innocent -> innocent.showTitle(title, subtitle));
  }

  public static void sendMessageToAllParticipants(final Game game, final Component message) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(player -> player.sendMessage(message));
  }

  public static void showBossBarForAllParticipants(
      final Game game,
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(player -> player.showBossBar(name, progress, color, overlay));
  }

  public static String convertToMini(final String text) {
    final String lower = text.toLowerCase();
    final StringBuilder result = new StringBuilder();
    for (final char character : lower.toCharArray()) {
      if (SMALL_CAPS_FONT.containsKey(character)) {
        result.append(SMALL_CAPS_FONT.get(character));
      } else {
        result.append(character);
      }
    }
    return result.toString();
  }
}
