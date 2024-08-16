package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class TrapSniffer extends SurvivorGadget {

  private final Multimap<GamePlayer, Item> glowItemStates;

  public TrapSniffer() {
    super(
        "trap_sniffer",
        Material.IRON_DOOR,
        Locale.TRAP_SNIFFER_TRAP_NAME.build(),
        Locale.TRAP_SNIFFER_TRAP_LORE.build(),
        64);
    this.glowItemStates = ArrayListMultimap.create();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final Component message = Locale.TRAP_SNIFFER_TRAP_ACTIVATE.build();
    gamePlayer.sendMessage(message);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleTrapSniffing(game, gamePlayer), 0, 2 * 20L);
  }

  private void handleTrapSniffing(final Game game, final GamePlayer player) {
    final Location origin = player.getLocation();
    final Map map = game.getMurderMap();
    final PartsManager manager = map.getCarPartManager();
    final java.util.Map<String, CarPart> parts = manager.getParts();
    final Collection<CarPart> stacks = parts.values();
    for (final CarPart stack : stacks) {
      final Location location = stack.getLocation();
      final Item entity = stack.getItem();
      final Collection<Item> set = requireNonNull(this.glowItemStates.get(player));
      final double distance = origin.distanceSquared(location);
      if (distance <= 36) {
        set.add(entity);
        player.setEntityGlowingForPlayer(entity);
      } else if (set.contains(entity)) {
        set.remove(entity);
        player.removeEntityGlowingForPlayer(entity);
      }
    }
  }
}
