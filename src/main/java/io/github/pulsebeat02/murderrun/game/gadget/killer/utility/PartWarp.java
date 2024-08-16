package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.StreamUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class PartWarp extends KillerGadget {

  public PartWarp() {
    super(
        "part_warp",
        Material.REPEATER,
        Locale.PART_WARP_TRAP_NAME.build(),
        Locale.PART_WARP_TRAP_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final io.github.pulsebeat02.murderrun.game.map.Map map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final Map<String, CarPart> parts = manager.getParts();
    final Collection<CarPart> values = parts.values();
    final List<CarPart> shuffled = values.stream().collect(StreamUtils.toShuffledList());
    final CarPart part = getRandomCarPart(shuffled);
    final Item item = part.getItem();

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    item.teleport(location);
  }

  public CarPart getRandomCarPart(final List<CarPart> shuffled) {
    CarPart chosen = shuffled.getFirst();
    while (chosen.isPickedUp()) {
      shuffled.remove(chosen);
      chosen = shuffled.getFirst();
    }
    return chosen;
  }
}
