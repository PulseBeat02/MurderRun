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
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Parasite extends SurvivorGadget {

  private static final double PARASITE_DESTROY_DISTANCE = 2D;
  private static final double PARASITE_RADIUS = 10D;
  private static final String PARASITE_SOUND = "block.lever.click";

  public Parasite() {
    super(
        "parasite",
        Material.VINE,
        Message.PARASITE_NAME.build(),
        Message.PARASITE_LORE.build(),
        48);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTaskUntilDeath(() -> this.handleKillers(manager, item), item);
    scheduler.scheduleParticleTaskUntilDeath(item, Color.GREEN);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(PARASITE_SOUND);

    return false;
  }

  private void handleKillers(final PlayerManager manager, final Item item) {
    manager.applyToAllMurderers(killer -> this.checkActivationDistance(killer, manager, item));
  }

  private void checkActivationDistance(
      final GamePlayer player, final PlayerManager manager, final Item item) {
    final Location origin = item.getLocation();
    final Location location = player.getLocation();
    final double distance = origin.distanceSquared(location);
    if (distance < PARASITE_DESTROY_DISTANCE * PARASITE_DESTROY_DISTANCE) {
      final Component message = Message.PARASITE_DEACTIVATE.build();
      manager.sendMessageToAllSurvivors(message);
      item.remove();
    } else if (distance < PARASITE_RADIUS * PARASITE_RADIUS) {
      player.addPotionEffects(
          new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, 0),
          new PotionEffect(PotionEffectType.POISON, 10 * 20, 0),
          new PotionEffect(PotionEffectType.WEAKNESS, 10 * 20, 0));
    }
  }
}
