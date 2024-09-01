package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetSettings;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
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
    super(
        "fake_part",
        Material.COMPARATOR,
        Message.FAKE_PART_NAME.build(),
        Message.FAKE_PART_LORE.build(),
        GadgetSettings.FAKE_PART_COST);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final Location location = player.getLocation();
    final Item fakeItem = this.spawnItem(location);

    final GameScheduler scheduler = game.getScheduler();
    final PlayerManager manager = game.getPlayerManager();
    scheduler.scheduleConditionalTask(
        () -> this.spawnParticleOnPart(fakeItem), 0, 2, fakeItem::isDead);

    final Runnable task = () -> this.handlePlayers(scheduler, manager, player, fakeItem);
    scheduler.scheduleConditionalTask(task, 0, 20L, fakeItem::isDead);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GadgetSettings.FAKE_PART_SOUND);

    return false;
  }

  private void handlePlayers(
      final GameScheduler scheduler,
      final PlayerManager manager,
      final GamePlayer killer,
      final Item item) {
    manager.applyToAllLivingInnocents(
        survivor -> this.checkNear(scheduler, survivor, killer, item));
  }

  private void checkNear(
      final GameScheduler scheduler,
      final GamePlayer survivor,
      final GamePlayer killer,
      final Item item) {
    final Location origin = item.getLocation();
    final Location location = survivor.getLocation();
    final double distance = origin.distanceSquared(location);
    final double radius = GadgetSettings.FAKE_PART_RADIUS;
    if (distance < radius * radius) {
      this.handleDebuff(scheduler, survivor, killer, item);
      final PlayerAudience audience = survivor.getAudience();
      final Component msg = Message.FAKE_PART_ACTIVATE.build();
      audience.sendMessage(msg);
      audience.playSound(GadgetSettings.FAKE_PART_EFFECT_SOUND);
    }
  }

  private void handleDebuff(
      final GameScheduler scheduler,
      final GamePlayer survivor,
      final GamePlayer killer,
      final Item item) {

    final int duration = GadgetSettings.FAKE_PART_DURATION;
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
