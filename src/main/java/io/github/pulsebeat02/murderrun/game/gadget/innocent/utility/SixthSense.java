package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Innocent;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Murderer;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import java.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class SixthSense extends MurderGadget {

  private final Multimap<GamePlayer, GamePlayer> glowPlayerStates;

  public SixthSense() {
    super(
        "sixth_sense",
        Material.ENDER_PEARL,
        Locale.SIXTH_SENSE_TRAP_NAME.build(),
        Locale.SIXTH_SENSE_TRAP_LORE.build());
    this.glowPlayerStates = ArrayListMultimap.create();
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {
    super.onGadgetDrop(game, event, true);

    final MurderPlayerManager manager = game.getPlayerManager();
    final Collection<Innocent> players = manager.getInnocentPlayers();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final Component message = Locale.SIXTH_SENSE_TRAP_ACTIVATE.build();
    gamePlayer.sendMessage(message);

    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllMurderers(
            murderer -> this.handleGlowMurderer(murderer, gamePlayer, players)),
        0,
        2 * 20);
  }

  private void handleGlowMurderer(
      final Murderer murderer, final GamePlayer innocent, final Collection<Innocent> innocents) {

    final Collection<GamePlayer> higher =
        innocents.stream().map(player -> (GamePlayer) player).toList();
    final Location location = innocent.getLocation();
    final Location other = murderer.getLocation();
    final Collection<GamePlayer> visible = this.glowPlayerStates.get(innocent);
    if (visible == null) {
      throw new AssertionError("Couldn't get player's glow states!");
    }

    final double distance = location.distanceSquared(other);
    if (distance <= 64) {
      visible.add(murderer);
      PlayerUtils.setGlowColor(murderer, ChatColor.RED, higher);
    } else if (visible.contains(murderer)) {
      visible.remove(murderer);
      PlayerUtils.removeGlow(murderer, higher);
    }
  }
}
