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

import com.google.common.util.concurrent.AtomicDouble;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.EntityReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;

public final class MedBot extends SurvivorGadget {

  public MedBot() {
    super(
      "med_bot",
      GameProperties.MED_BOT_COST,
      ItemFactory.createGadget("med_bot", GameProperties.MED_BOT_MATERIAL, Message.MED_BOT_NAME.build(), Message.MED_BOT_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final org.bukkit.entity.Item item = packet.getItem();
    item.remove();

    final GamePlayerManager manager = game.getPlayerManager();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
    armorStand.setInvisible(true);
    armorStand.setGravity(false);
    armorStand.setMarker(true);

    final EntityEquipment equipment = requireNonNull(armorStand.getEquipment());
    equipment.setHelmet(Item.create(Material.CHORUS_FLOWER));

    final GameScheduler scheduler = game.getScheduler();
    this.handleRotation(scheduler, armorStand);
    this.handleVerticalMotion(scheduler, armorStand);
    this.handleParticles(scheduler, armorStand);
    this.handleMedBotUpdate(scheduler, manager, armorStand);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.MED_BOT_SOUND);

    return false;
  }

  private void handleMedBotUpdate(final GameScheduler scheduler, final GamePlayerManager manager, final ArmorStand stand) {
    final Consumer<GamePlayer> consumer = survivor -> this.handleInnocentEffects(survivor, stand);
    final Consumer<GamePlayer> killerConsumer = killer -> this.handleKillerDestroy(manager, killer, stand);
    final Runnable task = () -> {
      manager.applyToLivingSurvivors(consumer);
      manager.applyToKillers(killerConsumer);
    };
    final EntityReference reference = EntityReference.of(stand);
    scheduler.scheduleRepeatedTask(task, 0, 5L, reference);
  }

  private void handleKillerDestroy(final GamePlayerManager manager, final GamePlayer killer, final ArmorStand stand) {
    final Location origin = stand.getLocation();
    final Location location = killer.getLocation();
    final World first = stand.getWorld();
    final World second = location.getWorld();
    if (first != second) {
      return;
    }

    final double distance = origin.distanceSquared(location);
    final double radius = GameProperties.MED_BOT_DESTROY_RADIUS;
    if (distance < radius * radius) {
      final Component message = Message.MED_BOT_DEACTIVATE.build();
      manager.sendMessageToAllLivingSurvivors(message);
      stand.remove();
    }
  }

  private void handleInnocentEffects(final GamePlayer innocent, final ArmorStand stand) {
    final Location origin = stand.getLocation();
    final Location location = innocent.getLocation();
    final double distance = origin.distanceSquared(location);
    final double radius = GameProperties.MED_BOT_RADIUS;
    if (distance < radius * radius) {
      innocent.addPotionEffects(new PotionEffect(PotionEffectType.REGENERATION, 2 * 20, 3));
    }
  }

  private void handleRotation(final GameScheduler scheduler, final ArmorStand stand) {
    final EntityReference reference = EntityReference.of(stand);
    scheduler.scheduleRepeatedTask(() -> this.rotateOneIteration(stand), 0, 1, reference);
  }

  private void handleVerticalMotion(final GameScheduler scheduler, final ArmorStand stand) {
    final AtomicDouble lastYOffset = new AtomicDouble();
    final AtomicLong currentTick = new AtomicLong();
    final Runnable task = () -> lastYOffset.set(this.moveVerticallyOneIteration(stand, currentTick.getAndIncrement(), lastYOffset.get()));
    final EntityReference reference = EntityReference.of(stand);
    scheduler.scheduleRepeatedTask(task, 0, 1, reference);
  }

  private void handleParticles(final GameScheduler scheduler, final ArmorStand stand) {
    final EntityReference reference = EntityReference.of(stand);
    scheduler.scheduleRepeatedTask(() -> this.spawnParticle(stand), 0, 2, reference);
  }

  private void spawnParticle(final ArmorStand stand) {
    final Location location = stand.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 5, 8, 8, 8, new DustOptions(Color.PURPLE, 4));
  }

  private double moveVerticallyOneIteration(final ArmorStand stand, final long current, final double lastYOffset) {
    final double yOffset = Math.sin(Math.toRadians(current * 5.0d));
    stand.teleport(stand.getLocation().add(0, (yOffset - lastYOffset), 0));
    return yOffset;
  }

  private void rotateOneIteration(final ArmorStand stand) {
    final EulerAngle angle = stand.getHeadPose();
    final EulerAngle result = angle.add(0, 0.1, 0);
    stand.setHeadPose(result);
  }
}
