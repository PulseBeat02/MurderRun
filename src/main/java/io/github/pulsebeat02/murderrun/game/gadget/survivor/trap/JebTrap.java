package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Sheep;

public final class JebTrap extends SurvivorTrap {

  public JebTrap() {
    super(
      "jeb",
      Material.CYAN_WOOL,
      Message.JEB_NAME.build(),
      Message.JEB_LORE.build(),
      Message.JEB_ACTIVATE.build(),
      GameProperties.JEB_COST,
      Color.WHITE
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final Location location = murderer.getLocation();
    final World world = requireNonNull(location.getWorld());
    for (int i = 0; i < GameProperties.JEB_SHEEP_COUNT; i++) {
      world.spawn(location, Sheep.class, sheep -> sheep.setCustomName("jeb_"));
    }

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> spawnRainbowParticles(location), 0, 5, GameProperties.JEB_DURATION);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.JEB_SOUND);
  }

  private void spawnRainbowParticles(Location location) {
    final World world = requireNonNull(location.getWorld());
    final int r = RandomUtils.generateInt(255);
    final int g = RandomUtils.generateInt(255);
    final int b = RandomUtils.generateInt(255);
    final org.bukkit.Color color = org.bukkit.Color.fromRGB(r, g, b);
    world.spawnParticle(Particle.DUST, location, 15, 3, 3, 3, new DustOptions(color, 4));
  }
}
