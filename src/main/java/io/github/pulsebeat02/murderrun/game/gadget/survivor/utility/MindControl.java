package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class MindControl extends SurvivorGadget {

  private static final int MIND_CONTROL_DURATION = 10 * 20;
  private static final String MIND_CONTROL_SOUND = "entity.enderman.scream";

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

    final GamePlayer owner = manager.getGamePlayer(player);
    if (!(owner instanceof final Survivor survivor)) {
      return;
    }
    survivor.setCanPickupCarPart(false);

    final Location origin = player.getLocation();
    final Location location = nearest.getLocation();
    owner.addPotionEffects(
        new PotionEffect(PotionEffectType.INVISIBILITY, MIND_CONTROL_DURATION, 1));
    owner.setInvulnerable(true);
    owner.teleport(location);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> this.applyMindControlEffects(player, nearest), 0L, 1L, MIND_CONTROL_DURATION);
    scheduler.scheduleTask(() -> this.resetPlayer(survivor, origin), MIND_CONTROL_DURATION);

    final String targetName = nearest.getDisplayName();
    final Component targetMsg = Message.MIND_CONTROL_ACTIVATE_SURVIVOR.build(targetName);
    final PlayerAudience audience1 = owner.getAudience();
    audience1.sendMessage(targetMsg);
    audience1.playSound(MIND_CONTROL_SOUND);

    final String name = player.getDisplayName();
    final Component msg = Message.MIND_CONTROL_ACTIVATE_KILLER.build(name);
    final PlayerAudience audience = nearest.getAudience();
    audience.sendMessage(msg);
  }

  private void resetPlayer(final Survivor player, final Location location) {
    player.teleport(location);
    player.setInvulnerable(false);
    player.setCanPickupCarPart(true);
  }

  private void applyMindControlEffects(final Player player, final GamePlayer killer) {
    final Location location = player.getLocation();
    final Vector velocity = player.getVelocity();
    killer.teleport(location);
    killer.setVelocity(velocity);
  }
}
