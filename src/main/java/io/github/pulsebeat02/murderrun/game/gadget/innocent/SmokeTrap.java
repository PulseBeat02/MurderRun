package io.github.pulsebeat02.murderrun.game.gadget.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
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
        Locale.SMOKE_TRAP_NAME.build(),
        Locale.SMOKE_TRAP_LORE.build(),
        Locale.SMOKE_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
    murderer.addPotionEffects(
        new PotionEffect(PotionEffectType.BLINDNESS, 7 * 20, 1),
        new PotionEffect(PotionEffectType.SLOWNESS, 7 * 20, 2));
    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnSmoke(murderer), 0, 10, 7 * 20);
  }

  private void spawnSmoke(final GamePlayer murderer) {
    final Location location = murderer.getLocation();
    murderer.apply(player -> player.spawnParticle(Particle.SMOKE, location, 25, 2, 2, 2));
  }
}
