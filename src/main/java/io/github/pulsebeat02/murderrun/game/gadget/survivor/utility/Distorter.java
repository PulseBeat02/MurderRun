package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Distorter extends SurvivorGadget {

  public Distorter() {
    super(
        "distorter",
        Material.CHORUS_FLOWER,
        Message.DISTORTER_NAME.build(),
        Message.DISTORTER_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    final Item item = event.getItemDrop();
    scheduler.scheduleConditionalTask(
        () -> manager.applyToAllMurderers(killer -> {
          this.applyDistortionEffect(manager, killer, location, item);
        }),
        0L,
        5L,
        item::isDead);

    final World world = requireNonNull(location.getWorld());
    scheduler.scheduleConditionalTask(
        () -> world.spawnParticle(
            Particle.DUST, item.getLocation(), 1, 0.5, 0.5, 0.5, new DustOptions(Color.PURPLE, 3)),
        0L,
        5L,
        item::isDead);
  }

  private void applyDistortionEffect(
      final PlayerManager manager,
      final GamePlayer killer,
      final Location origin,
      final Item item) {
    final Location location = killer.getLocation();
    final double distance = location.distanceSquared(origin);
    if (distance < 1) {
      final Component message = Message.DISTORTER_DEACTIVATE.build();
      manager.applyToAllLivingInnocents(innocent -> innocent.sendMessage(message));
      item.remove();
    } else if (distance < 100) {
      killer.spawnParticle(Particle.ELDER_GUARDIAN, location, 1, 0, 0, 0);
    }
  }
}
