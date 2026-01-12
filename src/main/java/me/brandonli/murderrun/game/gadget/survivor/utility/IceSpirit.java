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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import java.util.UUID;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.misc.TargetableEntity;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

public final class IceSpirit extends SurvivorGadget implements Listener, TargetableEntity {

  private final Game game;

  public IceSpirit(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "ice_spirit",
      properties.getIceSpiritCost(),
      ItemFactory.createGadget(
        "ice_spirit",
        properties.getIceSpiritMaterial(),
        Message.ICE_SPIRIT_NAME.build(),
        Message.ICE_SPIRIT_LORE.build()
      )
    );
    this.game = game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onTargetChange(final EntityTargetEvent event) {
    final Entity entity = event.getEntity();
    if (!(entity instanceof final Zombie zombie)) {
      return;
    }

    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    final String target = container.get(Keys.ICE_SPIRIT_OWNER, PersistentDataType.STRING);
    if (target == null) {
      return;
    }

    this.handle(event, target, zombie, true);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntityDamage(final EntityDamageByEntityEvent event) {
    final Entity entity = event.getDamager();
    if (!(entity instanceof final Zombie zombie)) {
      return;
    }

    final Entity damaged = event.getEntity();
    if (!(damaged instanceof final Player player)) {
      return;
    }

    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    final String target = container.get(Keys.ICE_SPIRIT_OWNER, PersistentDataType.STRING);
    if (target == null) {
      return;
    }
    zombie.remove();

    final UUID uuid = UUID.fromString(target);
    final GamePlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(uuid)) {
      return;
    }

    final GamePlayer nearest = manager.getGamePlayer(player);
    final GameScheduler scheduler = this.game.getScheduler();
    final Game game = this.getGame();
    final GameProperties properties = game.getProperties();
    final int duration = properties.getIceSpiritDuration();
    nearest.disableJump(scheduler, duration);
    nearest.setFreezeTicks(duration);
    nearest.disableWalkWithFOVEffects(duration);

    final Component msg = Message.FREEZE_ACTIVATE.build();
    manager.sendMessageToAllLivingSurvivors(msg);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final org.bukkit.entity.Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawn(location, Zombie.class, zombie -> {
      this.customizeAttributes(zombie);
      this.setTargetMetadata(player, zombie);
      this.setEquipment(zombie);
    });

    final GameProperties properties = game.getProperties();
    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getIceSpiritSound());

    return false;
  }

  private void customizeAttributes(final Zombie zombie) {
    zombie.setInvulnerable(true);
    zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2));
    zombie.setBaby();
  }

  private void setTargetMetadata(final GamePlayer owner, final Zombie zombie) {
    final UUID uuid = owner.getUUID();
    final String data = uuid.toString();
    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    container.set(Keys.ICE_SPIRIT_OWNER, PersistentDataType.STRING, data);
  }

  private void setEquipment(final Zombie zombie) {
    final EntityEquipment equipment = requireNonNull(zombie.getEquipment());
    equipment.setHelmet(Item.create(Material.ICE));
    equipment.setChestplate(Item.create(Material.DIAMOND_CHESTPLATE));
    equipment.setLeggings(Item.create(Material.DIAMOND_LEGGINGS));
    equipment.setBoots(Item.create(Material.DIAMOND_BOOTS));
  }

  @Override
  public Game getGame() {
    return this.game;
  }
}
