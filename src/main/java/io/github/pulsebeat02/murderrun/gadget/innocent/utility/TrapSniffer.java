package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.map.MurderMap;
import io.github.pulsebeat02.murderrun.map.part.CarPartItemStack;
import io.github.pulsebeat02.murderrun.map.part.CarPartManager;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.reflect.NMSHandler;
import io.github.pulsebeat02.murderrun.utils.SchedulingUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class TrapSniffer extends MurderGadget {

  private final Map<GamePlayer, Set<Item>> glowItemStates;

  public TrapSniffer() {
    super(
        "trap_sniffer",
        Material.IRON_DOOR,
        Locale.TRAP_SNIFFER_TRAP_NAME.build(),
        Locale.TRAP_SNIFFER_TRAP_LORE.build());
    this.glowItemStates = new WeakHashMap<>();
  }

  @Override
  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
    super.onDropEvent(game, event);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    gamePlayer.sendMessage(Locale.TRAP_SNIFFER_TRAP_ACTIVATE.build());
    this.glowItemStates.computeIfAbsent(gamePlayer, fun -> new HashSet<>());

    SchedulingUtils.scheduleTaskUntilCondition(
        () -> manager.applyToAllMurderers(murderer -> this.handleTrapSniffing(game, gamePlayer)),
        0,
        2 * 20,
        game::isFinished);
  }

  public void handleTrapSniffing(final MurderGame game, final GamePlayer innocent) {
    final Player player = innocent.getPlayer();
    final Location origin = innocent.getLocation();
    final MurderMap map = game.getMurderMap();
    final CarPartManager manager = map.getCarPartManager();
    final Map<String, CarPartItemStack> parts = manager.getParts();
    final Collection<CarPartItemStack> stacks = parts.values();
    for (final CarPartItemStack stack : stacks) {
      final Location location = stack.getLocation();
      final Item entity = stack.getItem();
      final Set<Item> set = this.glowItemStates.get(innocent);
      if (origin.distanceSquared(location) <= 36) {
        set.add(entity);
        NMSHandler.NMS_UTILS.sendGlowPacket(player, entity);
      } else if (set.contains(entity)) {
        NMSHandler.NMS_UTILS.sendRemoveGlowPacket(player, entity);
      }
    }
  }
}
