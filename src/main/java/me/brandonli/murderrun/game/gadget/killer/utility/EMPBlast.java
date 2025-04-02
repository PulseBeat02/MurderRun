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
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.gadget.Gadget;
import me.brandonli.murderrun.game.gadget.GadgetLoadingMechanism;
import me.brandonli.murderrun.game.gadget.GadgetManager;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.sound.Sounds;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

public final class EMPBlast extends KillerGadget {

  public EMPBlast() {
    super(
      "emp_grenade",
      GameProperties.EMP_BLAST_COST,
      ItemFactory.createGadget(
        "emp_grenade",
        GameProperties.EMP_BLAST_MATERIAL,
        Message.EMP_BLAST_NAME.build(),
        Message.EMP_BLAST_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());

    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final BoundingBox box = arena.createBox();

    final GadgetManager manager = game.getGadgetManager();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    final Collection<Entity> entities = world.getNearbyEntities(box);
    this.removeAllSurvivorGadgets(entities, mechanism);

    final GamePlayerManager playerManager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    playerManager.applyToLivingSurvivors(survivor -> this.stunSurvivors(scheduler, survivor));
    playerManager.playSoundForAllParticipants(Sounds.FLASHBANG);

    return false;
  }

  private void stunSurvivors(final GameScheduler scheduler, final GamePlayer survivor) {
    final PlayerAudience audience = survivor.getAudience();
    final Component msg = Message.EMP_BLAST_ACTIVATE.build();
    final int duration = GameProperties.EMP_BLAST_DURATION;
    survivor.disableJump(scheduler, duration);
    survivor.disableWalkWithFOVEffects(duration);
    survivor.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, duration, 1));
    audience.sendMessage(msg);
  }

  private void removeAllSurvivorGadgets(final Collection<Entity> entities, final GadgetLoadingMechanism mechanism) {
    for (final Entity entity : entities) {
      if (!(entity instanceof final Item item)) {
        continue;
      }

      final ItemStack stack = item.getItemStack();
      final Gadget gadget = mechanism.getGadgetFromStack(stack);
      if (gadget == null) {
        continue;
      }

      if (gadget instanceof KillerGadget) {
        continue;
      }

      item.remove();
    }
  }
}
