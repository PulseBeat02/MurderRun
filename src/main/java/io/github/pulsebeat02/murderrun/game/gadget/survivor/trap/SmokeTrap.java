package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Item;
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
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    murderer.addPotionEffects(
        new PotionEffect(PotionEffectType.BLINDNESS, 7 * 20, 1),
        new PotionEffect(PotionEffectType.SLOWNESS, 7 * 20, 2));
    scheduler.scheduleRepeatedTask(() -> this.spawnSmoke(murderer), 0, 1, 7 * 20L);
    manager.playSoundForAllParticipants("entity.blaze.ambient");
  }

  private void spawnSmoke(final GamePlayer murderer) {
    final Location location = murderer.getLocation();
    murderer.apply(player -> player.spawnParticle(
        Particle.DUST, location, 10, 4, 2, 2, new DustOptions(org.bukkit.Color.GRAY, 4)));
  }
}
