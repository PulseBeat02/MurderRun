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
import org.bukkit.entity.Item;

public final class DistortTrap extends SurvivorTrap {

  public DistortTrap() {
    super(
        "distort",
        Material.PRISMARINE_SHARD,
        Message.DISTORT_NAME.build(),
        Message.DISTORT_LORE.build(),
        Message.DISTORT_ACTIVATE.build(),
        16,
        new Color(177, 156, 217));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    manager.playSoundForAllParticipants("entity.elder_guardian.curse");
    scheduler.scheduleRepeatedTask(() -> this.spawnParticle(murderer), 0, 5, 7 * 20L);
  }

  private void spawnParticle(final GamePlayer murderer) {
    final Location dummy = murderer.getLocation();
    murderer.spawnParticle(Particle.ELDER_GUARDIAN, dummy, 1, 0, 0, 0);
  }
}
