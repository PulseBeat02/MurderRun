package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
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

public final class SixthSense extends SurvivorGadget {

  private final Multimap<GamePlayer, GamePlayer> glowPlayerStates;

  public SixthSense() {
    super(
        "sixth_sense",
        Material.GOLDEN_CARROT,
        Message.SIXTH_SENSE_NAME.build(),
        Message.SIXTH_SENSE_LORE.build(),
        48);
    this.glowPlayerStates = HashMultimap.create();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final Component message = Message.SIXTH_SENSE_ACTIVATE.build();
    gamePlayer.sendMessage(message);
    gamePlayer.playSound("entity.sniffer.digging");

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleKillers(manager, gamePlayer), 0, 2 * 20L);
  }

  private void handleKillers(final PlayerManager manager, final GamePlayer player) {
    manager.applyToAllMurderers(murderer -> this.handleGlowMurderer(murderer, player));
  }

  private void handleGlowMurderer(final Killer killer, final GamePlayer state) {
    final Location location = state.getLocation();
    final Location other = killer.getLocation();
    final Collection<GamePlayer> visible = this.glowPlayerStates.get(state);
    final double distance = location.distanceSquared(other);
    final MetadataManager metadata = state.getMetadataManager();
    if (distance < 100) {
      visible.add(killer);
      metadata.setEntityGlowing(killer, ChatColor.BLUE, true);
    } else if (visible.contains(killer)) {
      visible.remove(killer);
      metadata.setEntityGlowing(killer, ChatColor.BLUE, false);
    }
  }
}
