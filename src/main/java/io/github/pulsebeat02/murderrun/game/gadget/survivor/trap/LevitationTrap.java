package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class LevitationTrap extends SurvivorTrap {

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
  public void onTrapActivate(final Game game, final GamePlayer murderer) {

    final Location location = murderer.getLocation();
    final Location clone = location.clone();
    clone.add(0, 10, 0);

    final World world = requireNonNull(location.getWorld());
    final GameScheduler scheduler = game.getScheduler();
    murderer.playSound(key("entity.shulker.ambient"));
    murderer.addPotionEffects(new PotionEffect(PotionEffectType.LEVITATION, 7 * 20, 1));
    murderer.teleport(clone);
    scheduler.scheduleTask(() -> murderer.teleport(location), 7 * 20L);
    scheduler.scheduleRepeatedTask(() -> this.spawnParticle(world, murderer), 0, 5, 7 * 20L);
  }

  private void spawnParticle(final World world, final GamePlayer player) {
    final Location location = player.getLocation();
    world.spawnParticle(
        Particle.DUST, location, 10, 1, 1, 1, new DustOptions(org.bukkit.Color.PURPLE, 3));
  }
}
