package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.map.BlockWhitelistManager;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;

public final class CryoFreeze extends SurvivorGadget {

  public CryoFreeze() {
    super(
        "cryo_freeze",
        Material.ICE,
        Message.CRYO_FREEZE_NAME.build(),
        Message.CRYO_FREEZE_LORE.build(),
        GameProperties.CRYO_FREEZE_COST);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final Location location = player.getLocation();
    final BlockVector3 vector3 = BukkitAdapter.asBlockVector(location);
    final World world = requireNonNull(location.getWorld());
    final com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
    final BlockType ice = requireNonNull(BlockTypes.PACKED_ICE);
    final BlockState state = ice.getDefaultState();
    final Map map = game.getMap();
    final BlockWhitelistManager whitelistManager = map.getBlockWhitelistManager();
    this.createSphere(weWorld, vector3, state, whitelistManager);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.CRYO_FREEZE_SOUND);

    return false;
  }

  private void createSphere(
      final com.sk89q.worldedit.world.World weWorld,
      final BlockVector3 vector3,
      final BlockState state,
      final BlockWhitelistManager whitelistManager) {
    final WorldEdit worldEdit = WorldEdit.getInstance();
    try (final EditSession session = worldEdit.newEditSession(weWorld)) {
      try {
        session.makeSphere(vector3, state, GameProperties.CRYO_FREEZE_RADIUS, false);
        whitelistManager.addWhitelistedBlocks(session);
      } catch (final MaxChangedBlocksException e) {
        throw new AssertionError(e);
      }
    }
  }
}
