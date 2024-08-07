package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import java.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class SixthSense extends SurvivorGadget {

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
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Collection<Survivor> players = manager.getInnocentPlayers();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final Component message = Locale.SIXTH_SENSE_TRAP_ACTIVATE.build();
    gamePlayer.sendMessage(message);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllMurderers(
            murderer -> this.handleGlowMurderer(murderer, gamePlayer, players)),
        0,
        2 * 20);
  }

  private void handleGlowMurderer(
      final Killer killer, final GamePlayer innocent, final Collection<Survivor> survivors) {

    final Collection<GamePlayer> higher =
        survivors.stream().map(player -> (GamePlayer) player).toList();
    final Location location = innocent.getLocation();
    final Location other = killer.getLocation();
    final Collection<GamePlayer> visible = this.glowPlayerStates.get(innocent);
    final double distance = location.distanceSquared(other);
    if (distance <= 64) {
      visible.add(killer);
      PlayerUtils.setGlowColor(killer, ChatColor.RED, higher);
    } else if (visible.contains(killer)) {
      visible.remove(killer);
      PlayerUtils.removeGlow(killer, higher);
    }
  }
}
