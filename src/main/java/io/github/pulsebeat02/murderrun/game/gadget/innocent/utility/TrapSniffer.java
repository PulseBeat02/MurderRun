package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.map.MurderMap;
import io.github.pulsebeat02.murderrun.game.map.part.CarPartItemStack;
import io.github.pulsebeat02.murderrun.game.map.part.CarPartManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.reflect.NMSHandler;
import java.util.Collection;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class TrapSniffer extends MurderGadget {

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
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final MurderPlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final Component message = Locale.TRAP_SNIFFER_TRAP_ACTIVATE.build();
    gamePlayer.sendMessage(message);

    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllMurderers(murderer -> this.handleTrapSniffing(game, player)),
        0,
        2 * 20);
  }

  private void handleTrapSniffing(final MurderGame game, final Player innocent) {

    final Location origin = innocent.getLocation();
    final MurderMap map = game.getMurderMap();
    final CarPartManager manager = map.getCarPartManager();
    final Map<String, CarPartItemStack> parts = manager.getParts();
    final Collection<CarPartItemStack> stacks = parts.values();
    for (final CarPartItemStack stack : stacks) {

      final Location location = stack.getLocation();
      final Item entity = stack.getItem();
      final Collection<Item> set = this.glowItemStates.get(innocent);
      if (set == null) {
        throw new AssertionError("Couldn't get player's glow states!");
      }

      final double distance = origin.distanceSquared(location);
      if (distance <= 36) {
        set.add(entity);
        NMSHandler.NMS_UTILS.sendGlowPacket(innocent, entity);
      } else if (set.contains(entity)) {
        set.remove(entity);
        NMSHandler.NMS_UTILS.sendRemoveGlowPacket(innocent, entity);
      }
    }
  }
}
