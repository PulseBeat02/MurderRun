package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class BloodCurse extends KillerGadget {

  public BloodCurse() {
    super(
        "blood_curse",
        Material.REDSTONE,
        Message.BLOOD_CURSE_NAME.build(),
        Message.BLOOD_CURSE_LORE.build(),
        64);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    super.onGadgetDrop(game, event, true);
    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants("entity.wither.ambient");
    manager.applyToAllLivingInnocents(survivor -> this.scheduleTaskForSurvivors(game, survivor));
  }

  private void scheduleTaskForSurvivors(final Game game, final GamePlayer survivor) {

    final Component msg = Message.BLOOD_CURSE_ACTIVATE.build();
    final PlayerAudience audience = survivor.getAudience();
    audience.sendMessage(msg);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.setBloodBlock(survivor), 0, 10L);
  }

  private void setBloodBlock(final GamePlayer survivor) {
    final Location location = survivor.getLocation();
    final Block block = location.getBlock();
    block.setType(Material.REDSTONE_WIRE);
  }
}
