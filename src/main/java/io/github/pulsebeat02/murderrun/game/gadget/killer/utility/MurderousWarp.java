package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class MurderousWarp extends KillerGadget {

  public MurderousWarp() {
    super(
        "murderous_warp",
        Material.REDSTONE_BLOCK,
        Message.MURDEROUS_WARP_NAME.build(),
        Message.MURDEROUS_WARP_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer random = manager.getRandomAliveInnocentPlayer();
    final GamePlayer killer = manager.getGamePlayer(player);

    final Location first = random.getLocation();
    final Location second = killer.getLocation();
    random.teleport(second);
    killer.teleport(first);

    final PlayerAudience audienceRand = random.getAudience();
    audienceRand.playSound("entity.enderman.teleport");

    final Component msg = Message.WARP_DISTORT_ACTIVATE.build();
    audienceRand.sendMessage(msg);

    final PlayerAudience audienceKiller = random.getAudience();
    audienceKiller.sendMessage(msg);
  }
}
