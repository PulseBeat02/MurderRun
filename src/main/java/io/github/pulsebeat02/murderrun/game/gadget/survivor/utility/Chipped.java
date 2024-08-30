package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.data.GadgetConstants;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;

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
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    final MetadataManager metadata = player.getMetadataManager();
    final GameScheduler scheduler = game.getScheduler();
    this.setOtherSurvivorsGlowing(manager, metadata, scheduler);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GadgetConstants.CHIPPED_SOUND);

    return false;
  }

  private void setOtherSurvivorsGlowing(
      final PlayerManager manager, final MetadataManager metadata, final GameScheduler scheduler) {
    manager.applyToAllLivingInnocents(innocent -> metadata.setEntityGlowing(
        scheduler, innocent, ChatColor.GREEN, GadgetConstants.CHIPPED_DURATION));
  }
}
