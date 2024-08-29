package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

public final class BloodCurse extends KillerGadget {

  private static final String BLOOD_CURSE_SOUND = "entity.wither.ambient";

  public BloodCurse() {
    super(
        "blood_curse",
        Material.REDSTONE,
        Message.BLOOD_CURSE_NAME.build(),
        Message.BLOOD_CURSE_LORE.build(),
        64);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final GameScheduler scheduler = game.getScheduler();
    final PlayerManager manager = game.getPlayerManager();
    final Consumer<GamePlayer> task =
        survivor -> scheduler.scheduleRepeatedTask(() -> this.setBloodBlock(survivor), 0, 10L);
    manager.applyToAllLivingInnocents(task);
    manager.playSoundForAllParticipants(BLOOD_CURSE_SOUND);

    final Component msg = Message.BLOOD_CURSE_ACTIVATE.build();
    manager.sendMessageToAllSurvivors(msg);

    return false;
  }

  private void setBloodBlock(final GamePlayer survivor) {
    final Location location = survivor.getLocation();
    final Block block = location.getBlock();
    block.setType(Material.REDSTONE_WIRE);
  }
}
