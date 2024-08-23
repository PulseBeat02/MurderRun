package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class MindControl extends SurvivorGadget {

  public MindControl() {
    super(
        "mind_control",
        Material.STRUCTURE_VOID,
        Message.MIND_CONTROL_NAME.build(),
        Message.MIND_CONTROL_LORE.build(),
        64);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer nearest = manager.getNearestKiller(player.getLocation());
    if (nearest == null) {
      return;
    }

    final Location origin = player.getLocation();
    final Location location = nearest.getLocation();
    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10 * 20, 1));
    player.setInvulnerable(true);
    player.teleport(location);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> this.applyMindControlEffects(player, nearest), 0L, 1L, 10 * 20L);
    scheduler.scheduleTask(() -> this.resetPlayer(player, origin), 10 * 20L);
  }

  private void resetPlayer(final Player player, final Location location) {
    player.teleport(location);
    player.setInvulnerable(false);
  }

  private void applyMindControlEffects(final Player player, final GamePlayer killer) {
    final Location location = player.getLocation();
    final Vector velocity = player.getVelocity();
    killer.apply(other -> {
      other.teleport(location);
      other.setVelocity(velocity);
    });
  }
}
