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
package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.misc.TargetableEntity;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.UUID;
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
    super(
      "ice_spirit",
      GameProperties.ICE_SPIRIT_COST,
      ItemFactory.createGadget(
        "ice_spirit",
        GameProperties.ICE_SPIRIT_MATERIAL,
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
    final int duration = GameProperties.ICE_SPIRIT_DURATION;
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

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.ICE_SPIRIT_SOUND);

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
