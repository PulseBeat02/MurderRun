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

import java.util.UUID;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.misc.TargetableEntity;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Dormagogg extends KillerGadget implements Listener, TargetableEntity {

  private final Game game;

  public Dormagogg(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "dormagogg",
        properties.getDormagoggCost(),
        ItemFactory.createGadget(
            "dormagogg",
            properties.getDormagoggMaterial(),
            Message.DORMAGOGG_NAME.build(),
            Message.DORMAGOGG_LORE.build()));
    this.game = game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onTargetChange(final EntityTargetEvent event) {
    final Entity entity = event.getEntity();
    if (!(entity instanceof final Zombie zombie)) {
      return;
    }

    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    final String target = container.get(Keys.DORMAGOGG_OWNER, PersistentDataType.STRING);
    if (target == null) {
      return;
    }

    this.handle(event, target, zombie, false);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntityDamage(final EntityDamageByEntityEvent event) {
    final Entity entity = event.getDamager();
    if (!(entity instanceof final Zombie zombie)) {
      return;
    }

    final Entity attacked = event.getEntity();
    if (!(attacked instanceof final Player player)) {
      return;
    }

    final GamePlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    final String owner = container.get(Keys.DORMAGOGG_OWNER, PersistentDataType.STRING);
    if (owner == null) {
      return;
    }
    zombie.remove();

    final UUID ownerUuid = UUID.fromString(owner);
    if (!manager.checkPlayerExists(ownerUuid)) {
      return;
    }

    final GamePlayer nearest = manager.getGamePlayer(player);
    final GamePlayer killer = manager.getGamePlayer(ownerUuid);
    final GameScheduler scheduler = this.game.getScheduler();
    final GameProperties properties = this.game.getProperties();
    final int duration = properties.getDormagoggDuration();
    final int effect = properties.getDormagoggEffectDuration();
    nearest.disableJump(scheduler, duration);
    nearest.disableWalkWithFOVEffects(effect);
    nearest.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, duration, 1));

    final MetadataManager metadata = killer.getMetadataManager();
    metadata.setEntityGlowing(scheduler, nearest, NamedTextColor.RED, duration);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final GamePlayer player = packet.getPlayer();
    final org.bukkit.entity.Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    this.spawnDormagogg(world, location, player);

    final GameProperties properties = this.game.getProperties();
    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getDormagoggSound());

    return false;
  }

  private void spawnDormagogg(final World world, final Location location, final GamePlayer killer) {
    world.spawn(location, Zombie.class, zombie -> {
      this.customizeAttributes(zombie);
      this.setTargetMetadata(killer, zombie);
      this.setEquipment(zombie);
      if (zombie instanceof final Ageable ageable) {
        ageable.setBaby();
      }
    });
  }

  private void customizeAttributes(final Zombie zombie) {
    zombie.addPotionEffect(
        new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2));
    if (zombie instanceof final Ageable ageable) {
      ageable.setBaby();
    }
  }

  private void setTargetMetadata(final GamePlayer killer, final Zombie zombie) {
    final UUID killerUuid = killer.getUUID();
    final String killerData = killerUuid.toString();
    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    container.set(Keys.DORMAGOGG_OWNER, PersistentDataType.STRING, killerData);
  }

  private void setEquipment(final Zombie zombie) {
    final EntityEquipment equipment = requireNonNull(zombie.getEquipment());
    equipment.setHelmet(Item.create(Material.WITHER_SKELETON_SKULL));
    equipment.setChestplate(Item.create(Material.NETHERITE_CHESTPLATE));
    equipment.setLeggings(Item.create(Material.NETHERITE_LEGGINGS));
    equipment.setBoots(Item.create(Material.NETHERITE_BOOTS));
  }

  @Override
  public Game getGame() {
    return this.game;
  }
}
