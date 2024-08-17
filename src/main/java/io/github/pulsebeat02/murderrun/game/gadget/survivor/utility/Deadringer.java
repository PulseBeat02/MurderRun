package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Deadringer extends SurvivorGadget {

  public Deadringer() {
    super(
        "deadringer",
        Material.WITHER_SKELETON_SKULL,
        Message.DEADRINGER_NAME.build(),
        Message.DEADRINGER_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final String name = player.getDisplayName();
    final PlayerManager manager = game.getPlayerManager();
    final Component message = Message.PLAYER_DEATH.build(name);
    manager.applyToAllParticipants(gamePlayer -> gamePlayer.showTitle(message, empty()));
    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15 * 20, 1, true, false));
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.INVISIBILITY, 15 * 20, 1, true, false));
    player.setInvulnerable(true);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> player.setInvulnerable(false), 15 * 20L);
  }
}
