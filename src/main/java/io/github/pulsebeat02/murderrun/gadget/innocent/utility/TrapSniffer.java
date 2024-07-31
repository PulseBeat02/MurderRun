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
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class TrapSniffer extends MurderGadget {

  private final Set<Item> glowItems;

  public TrapSniffer() {
    super(
        "trap_sniffer",
        Material.IRON_DOOR,
        Locale.TRAP_SNIFFER_TRAP_NAME.build(),
        Locale.TRAP_SNIFFER_TRAP_LORE.build());
    this.glowItems = Collections.newSetFromMap(new WeakHashMap<>());
  }

  @Override
  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
    super.onDropEvent(game, event);
    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    gamePlayer.sendMessage(Locale.TRAP_SNIFFER_TRAP_ACTIVATE.build());
    SchedulingUtils.scheduleTaskUntilCondition(
        () -> manager.applyToAllMurderers(murderer -> this.handleTrapSniffing(game, player)),
        0,
        2 * 20,
        game::isFinished);
  }

  public void handleTrapSniffing(final MurderGame game, final Player innocent) {
    final Location origin = innocent.getLocation();
    final MurderMap map = game.getMurderMap();
    final CarPartManager manager = map.getCarPartManager();
    final Map<String, CarPartItemStack> parts = manager.getParts();
    final Collection<CarPartItemStack> stacks = parts.values();
    for (final CarPartItemStack stack : stacks) {
      final Location location = stack.getLocation();
      final Item entity = stack.getItem();
      if (origin.distanceSquared(location) <= 36) {
        this.glowItems.add(entity);
        NMSHandler.NMS_UTILS.sendGlowPacket(innocent, entity);
      } else if (this.glowItems.contains(entity)) {
        NMSHandler.NMS_UTILS.sendRemoveGlowPacket(innocent, entity);
      }
    }
  }
}
