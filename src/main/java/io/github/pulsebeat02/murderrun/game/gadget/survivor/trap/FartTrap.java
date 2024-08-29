package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
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

  private static final int FART_TRAP_EFFECT_DURATION = 5 * 20;
  private static final int FART_TRAP_DURATION = 7 * 20;

  public FartTrap() {
    super(
        "fart",
        Material.GREEN_WOOL,
        Message.FART_NAME.build(),
        Message.FART_LORE.build(),
        Message.FART_ACTIVATE.build(),
        16,
        Color.GREEN);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(Sounds.FART);

    murderer.addPotionEffects(
        new PotionEffect(PotionEffectType.SLOWNESS, FART_TRAP_EFFECT_DURATION, 4),
        new PotionEffect(PotionEffectType.NAUSEA, FART_TRAP_EFFECT_DURATION, 1));

    final GameScheduler scheduler = game.getScheduler();
    final Location location = murderer.getLocation();
    scheduler.scheduleRepeatedTask(() -> this.spawnParticles(location), 0, 5, FART_TRAP_DURATION);
  }

  private void spawnParticles(final Location location) {
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(
        Particle.DUST, location, 10, 2, 2, 2, new DustOptions(org.bukkit.Color.GREEN, 4));
  }
}
