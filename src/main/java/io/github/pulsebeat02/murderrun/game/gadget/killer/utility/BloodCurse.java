package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;

public final class BloodCurse extends KillerGadget {

  private static final Set<Material> BLACKLISTED_MATERIALS = Set.of(Material.AIR, Material.CHEST);

  public BloodCurse() {
    super(
        "blood_curse",
        Material.REDSTONE,
        Message.BLOOD_CURSE_NAME.build(),
        Message.BLOOD_CURSE_LORE.build(),
        GameProperties.BLOOD_CURSE_COST);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final GameScheduler scheduler = game.getScheduler();
    final PlayerManager manager = game.getPlayerManager();
    scheduler.scheduleRepeatedTask(() -> manager.applyToAllInnocents(this::setBloodBlock), 0, 7L);
    manager.playSoundForAllParticipants(GameProperties.BLOOD_CURSE_SOUND);

    final Component msg = Message.BLOOD_CURSE_ACTIVATE.build();
    manager.sendMessageToAllSurvivors(msg);

    return false;
  }

  private void setBloodBlock(final GamePlayer survivor) {

    final Location location = survivor.getLocation();
    final Block block = location.getBlock();
    final Block below = block.getRelative(BlockFace.DOWN);
    final Material type = below.getType();
    if (!type.isSolid() || !type.isOccluding() || BLACKLISTED_MATERIALS.contains(type)) {
      return;
    }

    block.setType(Material.REDSTONE_WIRE);
  }
}
