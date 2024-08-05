package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class TrapSniffer extends MurderGadget {

  private final Map<Player, Set<Item>> glowItemStates;

  public TrapSniffer() {
    super(
        "trap_sniffer",
        Material.IRON_DOOR,
        Locale.TRAP_SNIFFER_TRAP_NAME.build(),
        Locale.TRAP_SNIFFER_TRAP_LORE.build());
    this.glowItemStates = new WeakHashMap<>();
  }

  @Override
  public void onDropEvent(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onDropEvent(game, event, true);

    final MurderPlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final Component message = Locale.TRAP_SNIFFER_TRAP_ACTIVATE.build();
    gamePlayer.sendMessage(message);

    final MurderGameScheduler scheduler = game.getScheduler();
    this.glowItemStates.computeIfAbsent(player, fun -> new HashSet<>());
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
      final Set<Item> set = this.glowItemStates.get(innocent);
      if (set == null) {
        throw new AssertionError("Couldn't get player's glow states!");
      }

      final double distance = origin.distanceSquared(location);
      if (distance <= 36) {
        set.add(entity);
        NMSHandler.NMS_UTILS.sendGlowPacket(innocent, entity);
      } else if (set.contains(entity)) {
        NMSHandler.NMS_UTILS.sendRemoveGlowPacket(innocent, entity);
      }
    }
  }
}
