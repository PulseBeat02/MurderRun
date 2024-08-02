package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.InnocentPlayer;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import java.util.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class SixthSense extends MurderGadget {

  private final Map<GamePlayer, Set<GamePlayer>> glowPlayerStates;

  public SixthSense() {
    super(
        "sixth_sense",
        Material.ENDER_PEARL,
        Locale.SIXTH_SENSE_TRAP_NAME.build(),
        Locale.SIXTH_SENSE_TRAP_LORE.build());
    this.glowPlayerStates = new WeakHashMap<>();
  }

  @Override
  public void onDropEvent(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {
    super.onDropEvent(game, event, true);

    final MurderPlayerManager manager = game.getPlayerManager();
    final Collection<InnocentPlayer> players = manager.getInnocentPlayers();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    gamePlayer.sendMessage(Locale.SIXTH_SENSE_TRAP_ACTIVATE.build());
    this.glowPlayerStates.computeIfAbsent(gamePlayer, fun -> new HashSet<>());

    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllMurderers(
            murderer -> this.handleGlowMurderer(murderer, gamePlayer, players)),
        0,
        2 * 20);
  }

  private void handleGlowMurderer(
      final Murderer murderer,
      final GamePlayer innocent,
      final Collection<InnocentPlayer> innocents) {
    final Collection<GamePlayer> higher =
        innocents.stream().map(player -> (GamePlayer) player).toList();
    final Location location = innocent.getLocation();
    final Location other = murderer.getLocation();
    final Set<GamePlayer> visible = this.glowPlayerStates.get(innocent);
    if (visible == null) {
      throw new AssertionError("Couldn't get player's glow states!");
    }
    if (location.distanceSquared(other) <= 64) {
      visible.add(murderer);
      PlayerUtils.setGlowColor(murderer, ChatColor.RED, higher);
    } else if (visible.contains(murderer)) {
      this.glowPlayerStates.remove(murderer);
      PlayerUtils.removeGlow(murderer, higher);
    }
  }
}
