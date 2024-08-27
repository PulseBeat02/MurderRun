package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class CursedNote extends KillerGadget {

  public CursedNote() {
    super(
        "cursed_note",
        Material.PAPER,
        Message.CURSED_NOTE_NAME.build(),
        Message.CURSED_NOTE_LORE.build(),
        64);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final io.github.pulsebeat02.murderrun.game.map.Map map = game.getMap();
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final Collection<CarPart> closeParts = this.getCarPartsInRange(map, location);
    if (closeParts.isEmpty()) {
      return;
    }

    final GameSettings settings = game.getSettings();
    final Item cursed = this.spawnCursedNote(settings);

    final GameScheduler scheduler = game.getScheduler();
    for (final CarPart part : closeParts) {

      final Item item = part.getItem();
      item.setPickupDelay(Integer.MAX_VALUE);
      part.setCursed(cursed);

      final Supplier<Boolean> condition = () -> !part.isCursed();
      scheduler.scheduleConditionalTask(
          () -> this.handleSurvivorCurse(game, part), 0, 60L, condition);
    }

    scheduler.scheduleAfterDead(
        () -> {
          for (final CarPart part : closeParts) {
            final Item item = part.getItem();
            item.setPickupDelay(10);
            part.setCursed(null);
          }
        },
        cursed);

    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer killer = manager.getGamePlayer(player);
    final Component msg = Message.CURSED_NOTE_DROP.build();
    killer.sendMessage(msg);
  }

  private Item spawnCursedNote(final GameSettings settings) {
    final Arena arena = requireNonNull(settings.getArena());
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final World world = requireNonNull(first.getWorld());
    final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
    final ItemStack item = ItemFactory.createCursedNote();
    return world.dropItem(new Location(world, coords[0], 320, coords[1]), item);
  }

  private void handleSurvivorCurse(final Game game, final CarPart part) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllInnocents(survivor -> {
      final Location partLocation = part.getLocation();
      final Location survivorLocation = survivor.getLocation();
      final double distance = partLocation.distanceSquared(survivorLocation);
      if (distance <= 16) {
        survivor.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1));
        survivor.addPotionEffects(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 5, 1));
        final Component message = Message.CURSED_NOTE_ACTIVATE.build();
        survivor.sendMessage(message);
      }
    });
  }

  private Collection<CarPart> getCarPartsInRange(
      final io.github.pulsebeat02.murderrun.game.map.Map map, final Location origin) {
    final PartsManager manager = map.getCarPartManager();
    final Map<String, CarPart> mapping = manager.getParts();
    final Collection<CarPart> parts = mapping.values();
    final Set<CarPart> closeParts = new HashSet<>();
    for (final CarPart part : parts) {
      final Location location = part.getLocation();
      final double distance = origin.distanceSquared(location);
      if (distance <= 400) {
        closeParts.add(part);
      }
    }
    return closeParts;
  }
}
