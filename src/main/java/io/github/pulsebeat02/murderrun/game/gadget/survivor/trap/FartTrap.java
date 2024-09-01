package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class FartTrap extends SurvivorTrap {

  public FartTrap() {
    super(
        "fart",
        Material.GREEN_WOOL,
        Message.FART_NAME.build(),
        Message.FART_LORE.build(),
        Message.FART_ACTIVATE.build(),
        GameProperties.FART_COST,
        Color.GREEN);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final int duration = GameProperties.FART_EFFECT_DURATION;
    murderer.addPotionEffects(
        new PotionEffect(PotionEffectType.SLOWNESS, duration, 4),
        new PotionEffect(PotionEffectType.NAUSEA, duration, 1));

    final GameScheduler scheduler = game.getScheduler();
    final Location location = murderer.getLocation();
    scheduler.scheduleRepeatedTask(
        () -> this.spawnParticles(location), 0, 5, GameProperties.FART_DURATION);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(Sounds.FART);
  }

  private void spawnParticles(final Location location) {
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(
        Particle.DUST, location, 10, 2, 2, 2, new DustOptions(org.bukkit.Color.GREEN, 4));
  }
}
