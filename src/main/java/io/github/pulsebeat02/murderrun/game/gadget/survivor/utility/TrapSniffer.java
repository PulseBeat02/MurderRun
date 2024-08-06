package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

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
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class TrapSniffer extends SurvivorGadget {

  private final Multimap<Player, Item> glowItemStates;

  public TrapSniffer() {
    super(
        "trap_sniffer",
        Material.IRON_DOOR,
        Locale.TRAP_SNIFFER_TRAP_NAME.build(),
        Locale.TRAP_SNIFFER_TRAP_LORE.build());
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
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllMurderers(murderer -> this.handleTrapSniffing(game, player)),
        0,
        2 * 20);
  }

  private void handleTrapSniffing(final Game game, final Player innocent) {

    final Location origin = innocent.getLocation();
    final Map map = game.getMurderMap();
    final PartsManager manager = map.getCarPartManager();
    final java.util.Map<String, CarPart> parts = manager.getParts();
    final Collection<CarPart> stacks = parts.values();
    for (final CarPart stack : stacks) {

      final Location location = stack.getLocation();
      final Item entity = stack.getItem();
      final Collection<Item> set = this.glowItemStates.get(innocent);
      if (set == null) {
        throw new AssertionError("Couldn't get player's glow states!");
      }

      final double distance = origin.distanceSquared(location);
      if (distance <= 36) {
        set.add(entity);
        PacketToolsProvider.NMS_UTILS.sendGlowPacket(innocent, entity);
      } else if (set.contains(entity)) {
        set.remove(entity);
        PacketToolsProvider.NMS_UTILS.sendRemoveGlowPacket(innocent, entity);
      }
    }
  }
}
