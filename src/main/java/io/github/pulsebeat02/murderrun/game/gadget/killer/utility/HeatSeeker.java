package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class HeatSeeker extends KillerGadget {

  private final Multimap<GamePlayer, GamePlayer> glowPlayerStates;

  public HeatSeeker() {
    super(
        "heat_seeker",
        Material.BLAZE_ROD,
        Message.HEAT_SEEKER_NAME.build(),
        Message.HEAT_SEEKER_LORE.build(),
        48);
    this.glowPlayerStates = ArrayListMultimap.create();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final Component message = Message.HEAT_SEEKER_ACTIVATE.build();
    gamePlayer.sendMessage(message);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.scheduleTasks(manager, gamePlayer), 0, 2 * 20L);
  }

  private void scheduleTasks(final PlayerManager manager, final GamePlayer player) {
    player.apply(killer ->
        manager.applyToAllLivingInnocents(innocent -> this.handleGlowInnocent(innocent, player)));
  }

  private void handleGlowInnocent(final GamePlayer innocent, final GamePlayer state) {
    final Location location = innocent.getLocation();
    final Location other = state.getLocation();
    final Collection<GamePlayer> visible = this.glowPlayerStates.get(state);
    final double distance = location.distanceSquared(other);
    final MetadataManager metadata = state.getMetadataManager();
    if (distance < 100) {
      visible.add(innocent);
      metadata.setEntityGlowing(innocent, ChatColor.RED, true);
    } else if (visible.contains(innocent)) {
      visible.remove(innocent);
      metadata.setEntityGlowing(innocent, ChatColor.RED, false);
    }
  }
}
