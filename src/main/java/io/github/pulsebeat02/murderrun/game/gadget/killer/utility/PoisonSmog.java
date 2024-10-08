package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PoisonSmog extends KillerGadget {

  public PoisonSmog() {
    super(
      "poison_smog",
      Material.SLIME_BALL,
      Message.POISON_SMOG_NAME.build(),
      Message.POISON_SMOG_LORE.build(),
      GameProperties.POISON_SMOG_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final Game game, final GamePlayer player, final Item item, final boolean remove) {
    super.onGadgetDrop(game, player, item, true);

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GameScheduler scheduler = game.getScheduler();
    final PlayerManager manager = game.getPlayerManager();
    scheduler.scheduleRepeatedTask(
      () -> this.handleSmog(world, location, manager),
      0,
      2 * 20L,
      GameProperties.POISON_SMOG_DURATION
    );

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.POISON_SMOG_SOUND);

    return false;
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
    final double radius = GameProperties.POISON_SMOG_RADIUS;
    if (distance < radius * radius) {
      survivor.addPotionEffects(new PotionEffect(PotionEffectType.POISON, 3 * 20, 0));
    }
  }

  private void spawnSmogParticles(final World world, final Location origin) {
    world.spawnParticle(Particle.DUST, origin, 25, 10, 10, 10, new DustOptions(Color.GREEN, 4));
  }
}
