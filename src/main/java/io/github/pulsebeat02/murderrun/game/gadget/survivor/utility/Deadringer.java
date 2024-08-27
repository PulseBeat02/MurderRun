package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
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
        Material.ZOMBIE_HEAD,
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
    manager.sendMessageToAllParticipants(message);
    player.setInvulnerable(true);

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    gamePlayer.addPotionEffects(
        new PotionEffect(PotionEffectType.SPEED, 15 * 20, 1, true, false),
        new PotionEffect(PotionEffectType.INVISIBILITY, 15 * 20, 1, true, false));
    gamePlayer.playSound("item.totem.use");

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> player.setInvulnerable(false), 15 * 20L);
  }
}
