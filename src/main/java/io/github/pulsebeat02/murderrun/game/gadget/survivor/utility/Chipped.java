package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Chipped extends SurvivorGadget {

  private static final int CHIPPED_DURATION = 5 * 20;
  private static final String CHIPPED_SOUND = "block.amethyst_block.chime";

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
    this.setOtherSurvivorsGlowing(manager, metadata, scheduler);

    final PlayerAudience audience = owner.getAudience();
    audience.playSound(CHIPPED_SOUND);
  }

  private void setOtherSurvivorsGlowing(
      final PlayerManager manager, final MetadataManager metadata, final GameScheduler scheduler) {
    manager.applyToAllLivingInnocents(innocent ->
        metadata.setEntityGlowing(scheduler, innocent, ChatColor.GREEN, CHIPPED_DURATION));
  }
}
