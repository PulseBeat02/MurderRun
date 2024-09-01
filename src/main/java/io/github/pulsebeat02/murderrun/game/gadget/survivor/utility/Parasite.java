package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.HashSet;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Parasite extends SurvivorGadget {

  private final Set<Integer> removed;

  public Parasite() {
    super(
        "parasite",
        Material.VINE,
        Message.PARASITE_NAME.build(),
        Message.PARASITE_LORE.build(),
        GameProperties.PARASITE_COST);
    this.removed = new HashSet<>();
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTaskUntilDeath(() -> this.handleKillers(manager, item), item);
    scheduler.scheduleParticleTaskUntilDeath(item, Color.GREEN);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.PARASITE_SOUND);

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
    final double destroyRadius = GameProperties.PARASITE_DESTROY_RADIUS;
    final double radius = GameProperties.PARASITE_RADIUS;
    final int id = item.getEntityId();
    if (distance < destroyRadius * destroyRadius && !this.removed.contains(id)) {
      final Component message = Message.PARASITE_DEACTIVATE.build();
      manager.sendMessageToAllSurvivors(message);
      item.remove();
      this.removed.add(id);
    } else if (distance < radius * radius) {
      player.addPotionEffects(
          new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, 0),
          new PotionEffect(PotionEffectType.POISON, 10 * 20, 0),
          new PotionEffect(PotionEffectType.WEAKNESS, 10 * 20, 0));
    }
  }
}
