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

import java.util.Set;
import java.util.UUID;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.misc.TargetableEntity;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.EntityReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Wolf.Variant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class DeathHound extends KillerGadget implements Listener, TargetableEntity {

  private final Game game;

  public DeathHound(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "death_hound",
      properties.getDeathHoundCost(),
      ItemFactory.createGadget(
        "death_hound",
        properties.getDeathHoundMaterial(),
        Message.DEATH_HOUND_NAME.build(),
        Message.DEATH_HOUND_LORE.build()
      )
    );
    this.game = game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onTargetChange(final EntityTargetEvent event) {
    final Entity entity = event.getEntity();
    if (!(entity instanceof final Wolf wolf)) {
      return;
    }

    final PersistentDataContainer container = wolf.getPersistentDataContainer();
    final String target = container.get(Keys.DEATH_HOUND_OWNER, PersistentDataType.STRING);
    if (target == null) {
      return;
    }

    this.handle(event, target, wolf, false);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final GamePlayerManager manager = game.getPlayerManager();
    final Location location = player.getLocation();
    final GamePlayer nearest = manager.getNearestLivingSurvivor(location);
    if (nearest == null) {
      return true;
    }
    item.remove();

    final GameScheduler scheduler = game.getScheduler();
    final Wolf wolf = this.spawnWolf(location, player, nearest);
    final EntityReference reference = EntityReference.of(wolf);
    final GameProperties properties = game.getProperties();
    scheduler.scheduleTask(wolf::remove, properties.getDeathHoundDespawn(), reference);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getDeathHoundSound());

    return false;
  }

  private Wolf spawnWolf(final Location location, final GamePlayer owner, final GamePlayer nearest) {
    final World world = requireNonNull(location.getWorld());
    return world.spawn(location, Wolf.class, entity -> {
      this.customizeProperties(entity, owner, nearest);
      this.addPotionEffects(entity);
      this.addMetadata(entity, owner);
    });
  }

  private void addMetadata(final Wolf entity, final GamePlayer owner) {
    final UUID uuid = owner.getUUID();
    final String data = uuid.toString();
    final PersistentDataContainer container = entity.getPersistentDataContainer();
    container.set(Keys.DEATH_HOUND_OWNER, PersistentDataType.STRING, data);
  }

  private void addPotionEffects(final Wolf entity) {
    entity.addPotionEffects(
      Set.of(
        new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2),
        new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 1)
      )
    );
  }

  private void customizeProperties(final Wolf entity, final GamePlayer owner, final GamePlayer target) {
    entity.setTamed(true);
    entity.setAngry(true);
    entity.setVariant(Variant.BLACK);
    owner.apply(internal ->
      target.apply(internalTarget -> {
        entity.setOwner(internal);
        entity.setTarget(internalTarget);
      })
    );
  }

  @Override
  public Game getGame() {
    return this.game;
  }
}
