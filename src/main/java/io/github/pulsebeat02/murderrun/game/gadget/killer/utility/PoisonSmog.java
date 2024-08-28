package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
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
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PoisonSmog extends KillerGadget {

  private static final String POISON_SMOG_SOUND = "entity.slime.jump";
  private static final double POISON_SMOG_RANGE = 10;
  private static final int POISON_SMOG_DURATION = 60 * 20;

  public PoisonSmog() {
    super(
        "poison_smog",
        Material.SLIME_BALL,
        Message.POISON_SMOG_NAME.build(),
        Message.POISON_SMOG_LORE.build(),
        48);
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
        () -> this.handleSmog(world, location, manager), 0, 10, POISON_SMOG_DURATION);

    final GamePlayer owner = manager.getGamePlayer(player);
    final PlayerAudience audience = owner.getAudience();
    audience.playSound(POISON_SMOG_SOUND);
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
    if (distance < POISON_SMOG_RANGE * POISON_SMOG_RANGE) {
      survivor.addPotionEffects(new PotionEffect(PotionEffectType.POISON, 3 * 20, 0));
    }
  }

  private void spawnSmogParticles(final World world, final Location origin) {
    world.spawnParticle(Particle.DUST, origin, 25, 10, 10, 10, new DustOptions(Color.GREEN, 4));
  }
}
