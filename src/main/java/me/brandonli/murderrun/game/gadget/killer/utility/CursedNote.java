/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.map.part.CarPart;
import me.brandonli.murderrun.game.map.part.PartsManager;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
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
      ItemFactory.createGadget(
        "cursed_note",
        GameProperties.CURSED_NOTE_MATERIAL,
        Message.CURSED_NOTE_NAME.build(),
        Message.CURSED_NOTE_LORE.build()
      )
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
