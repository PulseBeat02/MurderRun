package io.github.pulsebeat02.murderrun.utils;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.resourcepack.sound.FXSound;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import static net.kyori.adventure.title.Title.title;

public final class AdventureUtils {

  private static final LegacyComponentSerializer SERIALIZER = BukkitComponentSerializer.legacy();

  private AdventureUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static String serializeComponentToLegacy(final Component component) {
    return SERIALIZER.serialize(component);
  }

  public static void playSoundForAllParticipants(final MurderGame game, final FXSound... keys) {
    final String key = getRandomKey(keys);
    final PlayerManager manager = game.getPlayerManager();
    for (final GamePlayer gamePlayer : manager.getParticipants()) {
      final Player player = gamePlayer.getPlayer();
      final Location location = player.getLocation();
      player.playSound(location, key, SoundCategory.MASTER, 1, 1);
    }
  }

  public static void playSoundForAllParticipants(final MurderGame game, final Sound... keys) {
    final Sound key = getRandomKey(keys);
    final PlayerManager manager = game.getPlayerManager();
    for (final GamePlayer gamePlayer : manager.getParticipants()) {
      final Player player = gamePlayer.getPlayer();
      final Location location = player.getLocation();
      player.playSound(location, key, SoundCategory.MASTER, 1, 1);
    }
  }

  public static void playSoundForAllParticipantsAtLocation(
      final MurderGame game, final Location origin, final FXSound... keys) {
    final String key = getRandomKey(keys);
    final PlayerManager manager = game.getPlayerManager();
    for (final GamePlayer gamePlayer : manager.getParticipants()) {
      final Player player = gamePlayer.getPlayer();
      player.playSound(origin, key, SoundCategory.MASTER, 1, 1);
    }
  }

  private static String getRandomKey(final FXSound... keys) {
    final int bound = keys.length;
    final int random = RandomUtils.generateInt(bound);
    final FXSound chosen = keys[random];
    return String.format("murder_run:%s", chosen.getId());
  }

  private static Sound getRandomKey(final Sound... keys) {
    final int bound = keys.length;
    final int random = RandomUtils.generateInt(bound);
    return keys[random];
  }

  public static void showTitleForAllParticipants(
      final MurderGame game, final Component title, final Component subtitle) {
    final MurderRun plugin = game.getPlugin();
    final AudienceHandler handler = plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    final PlayerManager manager = game.getPlayerManager();
    for (final GamePlayer gamePlayer : manager.getParticipants()) {
      final Player player = gamePlayer.getPlayer();
      final Audience audience = audiences.player(player);
      audience.showTitle(title(title, subtitle));
    }
  }

  public static void sendMessageToAllParticipants(final MurderGame game, final Component message) {
    final MurderRun plugin = game.getPlugin();
    final AudienceHandler handler = plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    final PlayerManager manager = game.getPlayerManager();
    for (final GamePlayer gamePlayer : manager.getParticipants()) {
      final Player player = gamePlayer.getPlayer();
      final Audience audience = audiences.player(player);
      audience.sendMessage(message);
    }
  }
}
