package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

public final class PlayerTracker extends KillerGadget {

  public PlayerTracker() {
    super(
        "player_tracker",
        Material.COMPASS,
        Message.PLAYER_TRACKER_NAME.build(),
        Message.PLAYER_TRACKER_LORE.build(),
        GameProperties.PLAYER_TRACKER_COST,
        ItemFactory::createPlayerTracker);
  }

  @Override
  public void onGadgetRightClick(
      final Game game, final PlayerInteractEvent event, final boolean remove) {

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final Location location = player.getLocation();
    final int distance = (int) Math.round(this.getNearestSurvivorDistance(manager, location));
    final int count = this.increaseAndGetSurvivorCount(player);
    final boolean destroy = count >= GameProperties.PLAYER_TRACKER_USES;
    super.onGadgetRightClick(game, event, destroy);

    final PlayerAudience audience = gamePlayer.getAudience();
    final Component message = Message.PLAYER_TRACKER_ACTIVATE.build(distance);
    audience.sendMessage(message);
    audience.playSound(GameProperties.PLAYER_TRACKER_SOUND);
  }

  private int increaseAndGetSurvivorCount(final Player player) {

    final PlayerInventory inventory = player.getInventory();
    final ItemStack stack = inventory.getItemInMainHand();
    final NamespacedKey key = Keys.PLAYER_TRACKER;
    final PersistentDataType<Integer, Integer> type = PersistentDataType.INTEGER;
    final Integer val = requireNonNull(PDCUtils.getPersistentDataAttribute(stack, key, type));
    final int count = val + 1;
    PDCUtils.setPersistentDataAttribute(stack, key, type, count);

    return count;
  }

  private double getNearestSurvivorDistance(final PlayerManager manager, final Location origin) {
    double min = Double.MAX_VALUE;
    final Collection<Survivor> survivors = manager.getAliveInnocentPlayers();
    for (final Survivor survivor : survivors) {
      final Location location = survivor.getLocation();
      final double distance = location.distance(origin);
      if (distance < min) {
        min = distance;
      }
    }
    return min;
  }
}
