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
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.EntityReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class FakePart extends KillerGadget {

  public FakePart() {
    super("fake_part", Material.COMPARATOR, Message.FAKE_PART_NAME.build(), Message.FAKE_PART_LORE.build(), GameProperties.FAKE_PART_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final Item fakeItem = this.spawnItem(location);

    final GameScheduler scheduler = game.getScheduler();
    final GamePlayerManager manager = game.getPlayerManager();
    final EntityReference reference = EntityReference.of(fakeItem);
    scheduler.scheduleRepeatedTask(() -> this.spawnParticleOnPart(fakeItem), 0, 2, reference);

    final Runnable task = () -> this.handlePlayers(scheduler, manager, player, fakeItem);
    scheduler.scheduleRepeatedTask(task, 0, 20L, reference);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.FAKE_PART_SOUND);

    return false;
  }

  private void handlePlayers(final GameScheduler scheduler, final GamePlayerManager manager, final GamePlayer killer, final Item item) {
    manager.applyToLivingSurvivors(survivor -> this.checkNear(scheduler, survivor, killer, item));
  }

  private void checkNear(final GameScheduler scheduler, final GamePlayer survivor, final GamePlayer killer, final Item item) {
    final Location origin = item.getLocation();
    final Location location = survivor.getLocation();
    final double distance = origin.distanceSquared(location);
    final double radius = GameProperties.FAKE_PART_RADIUS;
    if (distance < radius * radius) {
      this.handleDebuff(scheduler, survivor, killer, item);
      final PlayerAudience audience = survivor.getAudience();
      final Component msg = Message.FAKE_PART_ACTIVATE.build();
      audience.sendMessage(msg);
      audience.playSound(GameProperties.FAKE_PART_EFFECT_SOUND);
    }
  }

  private void handleDebuff(final GameScheduler scheduler, final GamePlayer survivor, final GamePlayer killer, final Item item) {
    final int duration = GameProperties.FAKE_PART_DURATION;
    survivor.disableJump(scheduler, duration);
    survivor.disableWalkWithFOVEffects(duration);
    survivor.addPotionEffects(new PotionEffect(PotionEffectType.SLOWNESS, duration, 1));
    item.remove();

    final MetadataManager metadata = killer.getMetadataManager();
    metadata.setEntityGlowing(scheduler, survivor, ChatColor.RED, duration);
  }

  private Item spawnItem(final Location location) {
    final ItemStack fake = ItemFactory.createFakePart();
    final World world = requireNonNull(location.getWorld());
    final Item item = world.dropItem(location, fake);
    item.setPickupDelay(Integer.MAX_VALUE);
    item.setUnlimitedLifetime(true);
    return item;
  }

  private void spawnParticleOnPart(final Item item) {
    final Location location = item.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 4, 0.2, 1, 0.2, new DustOptions(Color.YELLOW, 1));
  }
}
