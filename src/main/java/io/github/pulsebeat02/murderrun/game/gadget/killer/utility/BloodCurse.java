package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
    manager.applyToAllLivingInnocents(survivor -> this.scheduleTaskForSurvivors(game, survivor));
  }

  private void scheduleTaskForSurvivors(final Game game, final GamePlayer survivor) {

    final Component msg = Message.BLOOD_CURSE_ACTIVATE.build();
    survivor.sendMessage(msg);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.setBloodBlock(survivor), 0, 20);
  }

  private void setBloodBlock(final GamePlayer survivor) {
    final Location location = survivor.getLocation();
    final Block block = location.getBlock();
    final Block replace = block.getRelative(BlockFace.UP);
    replace.setType(Material.REDSTONE);
  }
}
