package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
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
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class TrapSniffer extends SurvivorGadget {

  private final Multimap<GamePlayer, Item> glowItemStates;

  public TrapSniffer() {
    super(
        "trap_sniffer",
        Material.TRIPWIRE_HOOK,
        Message.TRAP_SNIFFER_NAME.build(),
        Message.TRAP_SNIFFER_LORE.build(),
        64);
    this.glowItemStates = HashMultimap.create();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final Component message = Message.TRAP_SNIFFER_ACTIVATE.build();
    gamePlayer.sendMessage(message);
    gamePlayer.playSound("entity.sniffer.digging");

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleTrapSniffing(game, gamePlayer), 0, 2 * 20L);
  }

  private void handleTrapSniffing(final Game game, final GamePlayer player) {
    final Location origin = player.getLocation();
    final Map map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final java.util.Map<String, CarPart> parts = manager.getParts();
    final Collection<CarPart> stacks = parts.values();
    for (final CarPart stack : stacks) {
      final Location location = stack.getLocation();
      final Item entity = stack.getItem();
      final Collection<Item> set = requireNonNull(this.glowItemStates.get(player));
      final double distance = origin.distanceSquared(location);
      final MetadataManager metadata = player.getMetadataManager();
      if (distance < 225) {
        set.add(entity);
        metadata.setEntityGlowing(entity, ChatColor.RED, true);
      } else if (set.contains(entity)) {
        set.remove(entity);
        metadata.setEntityGlowing(entity, ChatColor.RED, false);
      }
    }
  }
}
