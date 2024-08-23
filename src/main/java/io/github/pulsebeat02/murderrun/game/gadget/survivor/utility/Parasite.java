package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Parasite extends SurvivorGadget {

  public Parasite() {
    super(
        "parasite",
        Material.VINE,
        Message.PARASITE_NAME.build(),
        Message.PARASITE_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    final Item item = event.getItemDrop();
    final Player player = event.getPlayer();
    final Location origin = player.getLocation();
    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleConditionalTask(
        () -> this.handleKillers(manager, origin, item), 0, 2 * 20L, item::isDead);

    final World world = requireNonNull(origin.getWorld());
    scheduler.scheduleConditionalTask(
        () -> world.spawnParticle(
            Particle.DUST, item.getLocation(), 1, 0.5, 0.5, 0.5, new DustOptions(Color.GREEN, 3)),
        0L,
        5L,
        item::isDead);
  }

  private void handleKillers(final PlayerManager manager, final Location origin, final Item item) {
    manager.applyToAllMurderers(
        killer -> this.checkActivationDistance(killer, origin, manager, item));
  }

  private void checkActivationDistance(
      final GamePlayer player,
      final Location origin,
      final PlayerManager manager,
      final Item item) {
    final Location location = player.getLocation();
    final double distance = origin.distanceSquared(location);
    if (distance < 4) {
      final Component message = Message.PARASITE_DEACTIVATE.build();
      manager.applyToAllLivingInnocents(survivor -> survivor.sendMessage(message));
      item.remove();
    } else if (distance < 100) {
      player.addPotionEffects(
          new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, 0),
          new PotionEffect(PotionEffectType.POISON, 10 * 20, 0),
          new PotionEffect(PotionEffectType.WEAKNESS, 10 * 20, 0));
    }
  }
}
