package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class SmokeTrap extends SurvivorTrap {

  public SmokeTrap() {
    super(
        "smoke",
        Material.GUNPOWDER,
        Message.SMOKE_NAME.build(),
        Message.SMOKE_LORE.build(),
        Message.SMOKE_ACTIVATE.build(),
        16,
        Color.GRAY);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    murderer.addPotionEffects(
        new PotionEffect(PotionEffectType.BLINDNESS, 7 * 20, 1),
        new PotionEffect(PotionEffectType.SLOWNESS, 7 * 20, 2));
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnSmoke(murderer), 0, 10, 7 * 20L);
  }

  private void spawnSmoke(final GamePlayer murderer) {
    final Location location = murderer.getLocation();
    murderer.apply(player -> player.spawnParticle(Particle.SMOKE, location, 25, 2, 2, 2));
  }
}
