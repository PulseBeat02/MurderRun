package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
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
      GameProperties.DISTORT_COST,
      new Color(177, 156, 217)
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnParticle(murderer), 0, 5, GameProperties.DISTORT_DURATION);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.DISTORT_SOUND);
  }

  private void spawnParticle(final GamePlayer murderer) {
    murderer.spawnPlayerSpecificParticle(Particle.ELDER_GUARDIAN);
  }
}
