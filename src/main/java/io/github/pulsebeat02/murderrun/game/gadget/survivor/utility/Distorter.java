package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Item;

public final class Distorter extends SurvivorGadget {

  private static final double DISTORTER_DESTROY_RADIUS = 2D;
  private static final double DISTORTER_EFFECT_RADIUS = 10D;
  private static final String DISTORTER_SOUND = "block.lever.click";

  public Distorter() {
    super(
        "distorter",
        Material.CHORUS_FLOWER,
        Message.DISTORTER_NAME.build(),
        Message.DISTORTER_LORE.build(),
        32);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTaskUntilDeath(() -> this.handleKillers(manager, item), item);
    scheduler.scheduleParticleTaskUntilDeath(item, Color.PURPLE);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(DISTORTER_SOUND);

    return false;
  }

  private void handleKillers(final PlayerManager manager, final Item item) {
    manager.applyToAllMurderers(killer -> this.applyDistortionEffect(manager, killer, item));
  }

  private void applyDistortionEffect(
      final PlayerManager manager, final GamePlayer killer, final Item item) {
    final Location location = killer.getLocation();
    final Location origin = item.getLocation();
    final double distance = location.distanceSquared(origin);
    if (distance < DISTORTER_DESTROY_RADIUS * DISTORTER_DESTROY_RADIUS) {
      final Component message = Message.DISTORTER_DEACTIVATE.build();
      manager.sendMessageToAllSurvivors(message);
      item.remove();
    } else if (distance < DISTORTER_EFFECT_RADIUS * DISTORTER_EFFECT_RADIUS) {
      killer.spawnPlayerSpecificParticle(Particle.ELDER_GUARDIAN);
    }
  }
}
