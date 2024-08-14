package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
        Locale.RED_ARROW_TRAP_NAME.build(),
        Locale.RED_ARROW_TRAP_LORE.build(),
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
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllInnocents(this::spawnParticleBeam), 0, 40);

    final Component message = Locale.RED_ARROW_ACTIVATE.build();
    gamePlayer.sendMessage(message);
  }

  private void spawnParticleBeam(final GamePlayer player) {

    final Location location = player.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      return;
    }

    final double startY = location.getY();
    final double skyLimit = world.getMaxHeight();

    for (double y = startY; y <= skyLimit; y += 1.0) {
      final Location particleLocation = new Location(world, location.getX(), y, location.getZ());
      world.spawnParticle(Particle.ENTITY_EFFECT, particleLocation, 1, Color.RED);
    }
  }
}
