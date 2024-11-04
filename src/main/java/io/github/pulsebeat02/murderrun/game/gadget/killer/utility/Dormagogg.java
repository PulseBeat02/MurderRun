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
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.misc.TargetableEntity;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.UUID;
import org.bukkit.ChatColor;
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
    super(
      "dormagogg",
      Material.WITHER_SKELETON_SKULL,
      Message.DORMAGOGG_NAME.build(),
      Message.DORMAGOGG_LORE.build(),
      GameProperties.DORMAGOGG_COST
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

    final PlayerManager manager = this.game.getPlayerManager();
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
    final int duration = GameProperties.DORMAGOGG_DURATION;
    final int effect = GameProperties.DORMAGOGG_EFFECT_DURATION;
    nearest.disableJump(scheduler, duration);
    nearest.disableWalkWithFOVEffects(effect);
    nearest.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, duration, 1));

    final MetadataManager metadata = killer.getMetadataManager();
    metadata.setEntityGlowing(scheduler, nearest, ChatColor.RED, duration);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final GamePlayer player = packet.getPlayer();
    final org.bukkit.entity.Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    this.spawnDormagogg(world, location, player);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.DORMAGOGG_SOUND);

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
    zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2));
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
