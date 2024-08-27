package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

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
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class LevitationTrap extends SurvivorTrap {

  private static final int LEVITATION_TRAP_DURATION = 7 * 20;
  private static final String LEVITATION_TRAP_SOUND = "entity.shulker.ambient";

  public LevitationTrap() {
    super(
        "levitation",
        Material.SHULKER_SHELL,
        Message.LEVITATION_NAME.build(),
        Message.LEVITATION_LORE.build(),
        Message.LEVITATION_ACTIVATE.build(),
        32,
        new Color(177, 156, 217));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final Location location = murderer.getLocation();
    final Location clone = location.clone();
    clone.add(0, 10, 0);

    murderer.addPotionEffects(
        new PotionEffect(PotionEffectType.LEVITATION, LEVITATION_TRAP_DURATION, 1));
    murderer.teleport(clone);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> murderer.teleport(location), LEVITATION_TRAP_DURATION);
    scheduler.scheduleRepeatedTask(
        () -> this.spawnParticles(murderer), 0, 5, LEVITATION_TRAP_DURATION);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(LEVITATION_TRAP_SOUND);
  }

  private void spawnParticles(final GamePlayer player) {
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(
        Particle.DUST, location, 10, 1, 1, 1, new DustOptions(org.bukkit.Color.PURPLE, 3));
  }
}
