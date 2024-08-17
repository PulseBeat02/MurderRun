package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PoisonSmog extends KillerGadget {

  public PoisonSmog() {
    super(
        "poison_smog",
        Material.SLIME_BALL,
        Message.POISON_SMOG_NAME.build(),
        Message.POISON_SMOG_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GameScheduler scheduler = game.getScheduler();
    final PlayerManager manager = game.getPlayerManager();
    scheduler.scheduleRepeatedTask(
        () -> this.handleSmog(world, location, manager), 0, 10, 60 * 20L);
  }

  private void handleSmog(final World world, final Location location, final PlayerManager manager) {
    this.spawnSmogParticles(world, location);
    this.handleSurvivors(manager, location);
  }

  private void handleSurvivors(final PlayerManager manager, final Location origin) {
    manager.applyToAllLivingInnocents(survivor -> this.handleDebuffs(survivor, origin));
  }

  private void handleDebuffs(final GamePlayer survivor, final Location origin) {
    final Location location = survivor.getLocation();
    final double distance = location.distanceSquared(origin);
    if (distance <= 25) {
      survivor.addPotionEffects(new PotionEffect(PotionEffectType.WITHER, 3 * 20, 1));
      survivor.addPotionEffects(new PotionEffect(PotionEffectType.POISON, 3 * 20, 1));
    }
  }

  private void spawnSmogParticles(final World world, final Location origin) {
    world.spawnParticle(Particle.ENTITY_EFFECT, origin, 25, 10, 10, 10, Color.GREEN);
  }
}
