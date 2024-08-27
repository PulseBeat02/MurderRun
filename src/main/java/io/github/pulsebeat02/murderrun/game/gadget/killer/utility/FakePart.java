package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
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
    scheduler.scheduleConditionalTask(() -> this.spawnParticleOnPart(item), 0, 20L, item::isDead);

    final GamePlayer killer = manager.getGamePlayer(player);
    killer.playSound("block.lever.click");

    final Runnable task = () -> this.handlePlayers(scheduler, manager, killer, item);
    scheduler.scheduleConditionalTask(task, 0, 20L, item::isDead);
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
    if (distance < 4) {
      this.handleDebuff(scheduler, survivor, killer, item);
      final Component msg = Message.FAKE_PART_ACTIVATE.build();
      survivor.sendMessage(msg);
    }
  }

  private void handleDebuff(
      final GameScheduler scheduler,
      final GamePlayer survivor,
      final GamePlayer killer,
      final Item item) {

    item.remove();
    survivor.disableJump(scheduler, 8 * 20L);
    survivor.disableWalkWithFOVEffects(8 * 20);
    survivor.addPotionEffects(new PotionEffect(PotionEffectType.SLOWNESS, 8 * 20, 1));

    final MetadataManager metadata = killer.getMetadataManager();
    metadata.setEntityGlowing(scheduler, survivor, ChatColor.RED, 8 * 20L);
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
    final Location clone = location.add(0, 1, 0);
    final World world = requireNonNull(clone.getWorld());
    world.spawnParticle(Particle.DUST, clone, 40, 0.2, 1, 0.2, new DustOptions(Color.YELLOW, 1));
  }
}
