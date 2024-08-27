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
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.map.BlockWhitelistManager;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class CryoFreeze extends SurvivorGadget {

  private static final String CRYO_FREEZE_SOUND = "block.glass.break";
  private static final int CRYO_FREEZE_RADIUS = 5;

  public CryoFreeze() {
    super(
        "cryo_freeze",
        Material.ICE,
        Message.CRYO_FREEZE_NAME.build(),
        Message.CRYO_FREEZE_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final BlockVector3 vector3 = BukkitAdapter.asBlockVector(location);
    final World world = requireNonNull(location.getWorld());
    final com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
    final BlockType ice = requireNonNull(BlockTypes.PACKED_ICE);
    final BlockState state = ice.getDefaultState();
    final Map map = game.getMap();
    final BlockWhitelistManager whitelistManager = map.getBlockWhitelistManager();
    this.createSphere(weWorld, vector3, state, whitelistManager);

    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer owner = manager.getGamePlayer(player);
    final PlayerAudience audience = owner.getAudience();
    audience.playSound(CRYO_FREEZE_SOUND);
  }

  private void createSphere(
      final com.sk89q.worldedit.world.World weWorld,
      final BlockVector3 vector3,
      final BlockState state,
      final BlockWhitelistManager whitelistManager) {
    final WorldEdit worldEdit = WorldEdit.getInstance();
    try (final EditSession session = worldEdit.newEditSession(weWorld)) {
      try {
        session.makeSphere(vector3, state, CRYO_FREEZE_RADIUS, false);
        whitelistManager.addWhitelistedBlocks(session);
      } catch (final MaxChangedBlocksException e) {
        throw new AssertionError(e);
      }
    }
  }
}
