package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.InnocentPlayer;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import io.github.pulsebeat02.murderrun.utils.SchedulingUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class SixthSense extends MurderGadget {

  private final Set<GamePlayer> glowPlayers;

  public SixthSense() {
    super(
        "sixth_sense",
        Material.ENDER_PEARL,
        Locale.SIXTH_SENSE_TRAP_NAME.build(),
        Locale.SIXTH_SENSE_TRAP_LORE.build());
    this.glowPlayers = Collections.newSetFromMap(new WeakHashMap<>());
  }

  @Override
  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
    super.onDropEvent(game, event);

    final PlayerManager manager = game.getPlayerManager();
    final Collection<InnocentPlayer> players = manager.getInnocentPlayers();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    gamePlayer.sendMessage(Locale.SIXTH_SENSE_TRAP_ACTIVATE.build());

    SchedulingUtils.scheduleTaskUntilCondition(
        () -> manager.applyToAllMurderers(
            murderer -> this.handleGlowMurderer(murderer, player, players)),
        0,
        2 * 20,
        game::isFinished);
  }

  public void handleGlowMurderer(
      final Murderer murderer, final Player innocent, final Collection<InnocentPlayer> innocents) {
    final Collection<GamePlayer> higher =
        innocents.stream().map(player -> (GamePlayer) player).toList();
    final Location location = innocent.getLocation();
    final Location other = murderer.getLocation();
    if (location.distanceSquared(other) <= 64) {
      this.glowPlayers.add(murderer);
      PlayerUtils.setGlowColor(murderer, ChatColor.RED, higher);
    } else if (this.glowPlayers.contains(murderer)) {
      this.glowPlayers.remove(murderer);
      PlayerUtils.removeGlow(murderer, higher);
    }
  }
}
