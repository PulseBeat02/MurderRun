package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
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

  private static final double TRAP_SNIFFER_RADIUS = 15D;
  private static final String TRAP_SNIFFER_SOUND = "entity.sniffer.digging";

  public TrapSniffer() {
    super(
        "trap_sniffer",
        Material.TRIPWIRE_HOOK,
        Message.TRAP_SNIFFER_NAME.build(),
        Message.TRAP_SNIFFER_LORE.build(),
        64);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (!(gamePlayer instanceof Survivor survivor)) {
      return;
    }

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleTrapSniffing(game, survivor), 0, 2 * 20L);

    final PlayerAudience audience = gamePlayer.getAudience();
    final Component message = Message.TRAP_SNIFFER_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(TRAP_SNIFFER_SOUND);
  }

  private void handleTrapSniffing(final Game game, final Survivor player) {
    final Location origin = player.getLocation();
    final Map map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final java.util.Map<String, CarPart> parts = manager.getParts();
    final Collection<CarPart> stacks = parts.values();
    final Collection<Item> set = player.getGlowingCarParts();
    for (final CarPart stack : stacks) {
      final Location location = stack.getLocation();
      final Item entity = stack.getItem();
      final double distance = origin.distanceSquared(location);
      final MetadataManager metadata = player.getMetadataManager();
      if (distance < TRAP_SNIFFER_RADIUS * TRAP_SNIFFER_RADIUS) {
        set.add(entity);
        metadata.setEntityGlowing(entity, ChatColor.RED, true);
      } else if (set.contains(entity)) {
        set.remove(entity);
        metadata.setEntityGlowing(entity, ChatColor.RED, false);
      }
    }
  }
}
