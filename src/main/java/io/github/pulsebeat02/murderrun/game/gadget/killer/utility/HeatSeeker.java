package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class HeatSeeker extends KillerGadget {

  private final Multimap<GamePlayer, GamePlayer> glowPlayerStates;

  public HeatSeeker() {
    super(
        "heat_seeker",
        Material.REPEATER,
        Locale.HEAT_SEEKER_TRAP_NAME.build(),
        Locale.HEAT_SEEKER_TRAP_LORE.build(),
        48);
    this.glowPlayerStates = ArrayListMultimap.create();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Collection<Survivor> players = manager.getInnocentPlayers();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final Component message = Locale.HEAT_SEEKER_ACTIVATE.build();
    gamePlayer.sendMessage(message);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllInnocents(
            innocent -> this.handleGlowInnocent(innocent, player, gamePlayer)),
        0,
        2 * 20);
  }

  private void handleGlowInnocent(
      final Survivor innocent, final Player killer, final GamePlayer state) {
    final Location location = innocent.getLocation();
    final Location other = killer.getLocation();
    final Collection<GamePlayer> visible = this.glowPlayerStates.get(state);
    final double distance = location.distanceSquared(other);
    innocent.apply(player -> {
      if (distance <= 64) {
        visible.add(innocent);
        PacketToolsProvider.INSTANCE.sendGlowPacket(killer, player);
      } else if (visible.contains(innocent)) {
        visible.remove(innocent);
        PacketToolsProvider.INSTANCE.sendRemoveGlowPacket(killer, player);
      }
    });
  }
}
