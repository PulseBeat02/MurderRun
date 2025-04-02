/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.map.GameMap;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.NullReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class CursedNote extends KillerGadget {

  public CursedNote() {
    super(
      "cursed_note",
      GameProperties.CURSED_NOTE_COST,
      ItemFactory.createGadget("cursed_note", Material.PAPER, Message.CURSED_NOTE_NAME.build(), Message.CURSED_NOTE_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final GameMap map = game.getMap();
    final Location location = player.getLocation();
    final Collection<CarPart> closeParts = this.getCarPartsInRange(map, location);
    if (closeParts.isEmpty()) {
      return true;
    }
    item.remove();

    final GameSettings settings = game.getSettings();
    final Item cursed = this.spawnCursedNote(settings);
    final GameScheduler scheduler = game.getScheduler();
    for (final CarPart part : closeParts) {
      this.scheduleItemTask(game, part, cursed, scheduler);
    }
    scheduler.scheduleAfterDeath(() -> this.resetAllParts(closeParts), cursed);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.CURSED_NOTE_SOUND);

    final Component msg = Message.CURSED_NOTE_DROP.build();
    audience.sendMessage(msg);

    return false;
  }

  private void resetAllParts(final Collection<CarPart> closeParts) {
    for (final CarPart part : closeParts) {
      final Item item = part.getItem();
      item.setPickupDelay(10);
      part.setCursed(null);
    }
  }

  private void scheduleItemTask(final Game game, final CarPart part, final Item cursed, final GameScheduler scheduler) {
    final Item item = part.getItem();
    item.setPickupDelay(Integer.MAX_VALUE);
    part.setCursed(cursed);

    final BooleanSupplier condition = () -> !part.isCursed();
    final NullReference reference = NullReference.of();
    scheduler.scheduleConditionalTask(() -> this.handleSurvivorCurse(game, part), 0, 60L, condition, reference);
  }

  private Item spawnCursedNote(final GameSettings settings) {
    final Arena arena = requireNonNull(settings.getArena());
    final Location drop = arena.getRandomItemLocation();
    final World world = requireNonNull(drop.getWorld());
    final ItemStack item = ItemFactory.createCursedNote();
    return world.dropItem(drop, item);
  }

  private void handleSurvivorCurse(final Game game, final CarPart part) {
    final GamePlayerManager manager = game.getPlayerManager();
    manager.applyToLivingSurvivors(survivor -> {
      final Location partLocation = part.getLocation();
      final Location survivorLocation = survivor.getLocation();
      final double distance = partLocation.distanceSquared(survivorLocation);
      final double radius = GameProperties.CURSED_NOTE_EFFECT_RADIUS;
      if (distance <= radius * radius) {
        survivor.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1));
        survivor.addPotionEffects(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 5, 1));
        final PlayerAudience audience = survivor.getAudience();
        final Component message = Message.CURSED_NOTE_ACTIVATE.build();
        audience.sendMessage(message);
      }
    });
  }

  private Collection<CarPart> getCarPartsInRange(final GameMap map, final Location origin) {
    final PartsManager manager = map.getCarPartManager();
    final Map<String, CarPart> mapping = manager.getParts();
    final Collection<CarPart> parts = mapping.values();
    final Set<CarPart> closeParts = new HashSet<>();
    for (final CarPart part : parts) {
      final Location location = part.getLocation();
      final double distance = origin.distanceSquared(location);
      final double radius = GameProperties.CURSED_NOTE_RADIUS;
      if (distance < radius * radius) {
        closeParts.add(part);
      }
    }
    return closeParts;
  }
}
