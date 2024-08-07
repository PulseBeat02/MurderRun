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
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class CryoFreeze extends SurvivorGadget {

  public CryoFreeze() {
    super(
        "cryo_freeze",
        Material.ICE,
        Locale.CRYO_FREEZE_TRAP_NAME.build(),
        Locale.CRYO_FREEZE_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
    final WorldEdit worldEdit = WorldEdit.getInstance();
    final BlockVector3 vector3 = MapUtils.toBlockVector3(location);
    final BlockType ice = requireNonNull(BlockTypes.ICE);
    final BlockState state = ice.getDefaultState();
    try (final EditSession session = worldEdit.newEditSession(weWorld)) {
      try {
        session.makeSphere(vector3, state, 5, false);
      } catch (final MaxChangedBlocksException e) {
        throw new AssertionError(e);
      }
    }
  }
}
