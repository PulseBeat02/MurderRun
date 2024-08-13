package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class HauntTrap extends SurvivorTrap {

  public HauntTrap() {
    super(
        "haunt",
        Material.WITHER_SKELETON_SKULL,
        Locale.HAUNT_TRAP_NAME.build(),
        Locale.HAUNT_TRAP_LORE.build(),
        Locale.HAUNT_TRAP_ACTIVATE.build(),
        Color.GRAY);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.createSpookyEffect(game, murderer), 0, 14, 7 * 20);
    murderer.addPotionEffects(new PotionEffect(PotionEffectType.NAUSEA, 20 * 7, 10));
  }

  private void createSpookyEffect(final Game game, final GamePlayer gamePlayer) {
    final Location location = gamePlayer.getLocation();
    final GameScheduler scheduler = game.getScheduler();
    gamePlayer.addPotionEffects(new PotionEffect(PotionEffectType.DARKNESS, 20, 10));
    gamePlayer.spawnParticle(Particle.ELDER_GUARDIAN, location, 1, 0, 0, 0);
    gamePlayer.addFakeWorldBorderEffect();
    scheduler.scheduleTask(() -> this.removeSpecialEffects(gamePlayer), 20);
  }

  private void removeSpecialEffects(final GamePlayer gamePlayer) {
    gamePlayer.apply(player -> player.removePotionEffect(PotionEffectType.DARKNESS));
    gamePlayer.removeFakeWorldBorderEffect();
  }
}
