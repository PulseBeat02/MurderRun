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
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.EntityReference;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
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
    super(
      "death_hound",
      Material.BONE,
      Message.DEATH_HOUND_NAME.build(),
      Message.DEATH_HOUND_LORE.build(),
      GameProperties.DEATH_HOUND_COST
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
    scheduler.scheduleTask(wolf::remove, GameProperties.DEATH_HOUND_DESPAWN, reference);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.DEATH_HOUND_SOUND);

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
