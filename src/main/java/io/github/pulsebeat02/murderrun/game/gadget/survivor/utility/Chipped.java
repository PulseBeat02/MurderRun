package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Chipped extends SurvivorGadget {

  public Chipped() {
    super(
        "chipped",
        Material.GOLD_NUGGET,
        Message.CHIPPED_NAME.build(),
        Message.CHIPPED_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    super.onGadgetDrop(game, event, true);
    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer owner = manager.getGamePlayer(player);
    final MetadataManager metadata = owner.getMetadataManager();
    final GameScheduler scheduler = game.getScheduler();
    manager.applyToAllLivingInnocents(
        innocent -> metadata.setEntityGlowing(scheduler, innocent, ChatColor.GREEN, 5 * 20L));
    owner.playSound("block.amethyst_block.chime");
  }
}
