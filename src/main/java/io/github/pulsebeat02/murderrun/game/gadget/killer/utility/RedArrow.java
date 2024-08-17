package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
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
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

public final class RedArrow extends KillerGadget {

  public RedArrow() {
    super(
        "red_arrow",
        Material.TIPPED_ARROW,
        Message.RED_ARROW_NAME.build(),
        Message.RED_ARROW_LORE.build(),
        32,
        stack -> {
          final ItemMeta meta = requireNonNull(stack.getItemMeta());
          if (meta instanceof final PotionMeta potionMeta) {
            potionMeta.setColor(Color.RED);
            stack.setItemMeta(meta);
          }
        });
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleSurvivors(manager), 0, 2 * 20L);

    final Component message = Message.RED_ARROW_ACTIVATE.build();
    gamePlayer.sendMessage(message);
  }

  private void handleSurvivors(final PlayerManager manager) {
    manager.applyToAllLivingInnocents(this::spawnParticleBeam);
  }

  private void spawnParticleBeam(final GamePlayer player) {

    final Location location = player.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      return;
    }

    final double startY = location.getY();
    final double skyLimit = world.getMaxHeight();
    final double x = location.getX();
    final double z = location.getZ();

    for (double y = startY; y <= skyLimit; y += 1.0) {
      final Location particleLocation = new Location(world, x, y, z);
      world.spawnParticle(Particle.DUST, particleLocation, 1, new DustOptions(Color.RED, 1));
    }
  }
}
