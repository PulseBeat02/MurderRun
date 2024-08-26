package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Cloak extends SurvivorGadget {

  public Cloak() {
    super(
        "cloak", Material.WHITE_BANNER, Message.CLOAK_NAME.build(), Message.CLOAK_LORE.build(), 32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    manager.applyToAllLivingInnocents(player -> {
      final MetadataManager metadata = player.getMetadataManager();
      metadata.hideNameTag(scheduler, 7 * 20L);
    });

    final Component message = Message.CLOAK_ACTIVATE.build();
    manager.sendMessageToAllSurvivors(message);
    manager.playSoundForAllParticipants("entity.phantom.flap");
  }
}
