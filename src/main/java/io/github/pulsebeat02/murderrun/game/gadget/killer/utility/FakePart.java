package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
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
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final Item item = this.spawnItem(location);

    final GameScheduler scheduler = game.getScheduler();
    final PlayerManager manager = game.getPlayerManager();
    scheduler.scheduleTaskUntilCondition(
        () -> this.spawnParticleOnPart(location), 0, 20, item::isDead);

    final GamePlayer killer = manager.getGamePlayer(player);
    final Runnable task = () -> this.handlePlayers(scheduler, manager, killer, item);
    scheduler.scheduleTaskUntilCondition(task, 0, 20, item::isDead);
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
    if (distance < 1) {
      this.handleDebuff(scheduler, survivor, killer);
      final Component msg = Message.FAKE_PART_ACTIVATE.build();
      survivor.sendMessage(msg);
    }
  }

  private void handleDebuff(
      final GameScheduler scheduler, final GamePlayer survivor, final GamePlayer killer) {
    survivor.disableJump(scheduler, 5 * 20L);
    survivor.addPotionEffects(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 1));
    killer.setEntityGlowingForPlayer(survivor, ChatColor.RED);
    scheduler.scheduleTask(() -> killer.removeEntityGlowingForPlayer(survivor), 5 * 20L);
  }

  private Item spawnItem(final Location location) {
    final ItemStack fake = ItemFactory.createFakePart();
    final World world = requireNonNull(location.getWorld());
    final Item item = world.dropItem(location, fake);
    item.setPickupDelay(Integer.MAX_VALUE);
    item.setUnlimitedLifetime(true);
    return item;
  }

  private void spawnParticleOnPart(final Location location) {
    final Location clone = location.clone().add(0, 1, 0);
    final World world = requireNonNull(clone.getWorld());
    world.spawnParticle(Particle.DUST, clone, 40, 0.2, 1, 0.2, new DustOptions(Color.YELLOW, 1));
  }
}
