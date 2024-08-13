package io.github.pulsebeat02.murderrun.utils;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.LocaleTools;
import io.github.pulsebeat02.murderrun.locale.Sender;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundKeys;
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

  private ComponentUtils() {
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

  public static void showBossBarForAllParticipants(
      final Game game,
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(player -> player.showBossBar(name, progress, color, overlay));
  }
}
