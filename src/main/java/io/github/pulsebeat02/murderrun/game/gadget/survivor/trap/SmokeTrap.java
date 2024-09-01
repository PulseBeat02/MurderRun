package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
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
        GameProperties.SMOKE_COST,
        Color.GRAY);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final int duration = GameProperties.SMOKE_DURATION;
    murderer.addPotionEffects(
        new PotionEffect(PotionEffectType.BLINDNESS, duration, 1),
        new PotionEffect(PotionEffectType.SLOWNESS, duration, 2));

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnSmoke(murderer), 0, 1, duration);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.SMOKE_SOUND);
  }

  private void spawnSmoke(final GamePlayer murderer) {
    final Location location = murderer.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(
        Particle.DUST, location, 10, 4, 2, 2, new DustOptions(org.bukkit.Color.GRAY, 4));
  }
}
