package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.utils.SchedulingUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public final class CageTrap extends SurvivorTrap {

    public CageTrap() {
        super(
                "cage",
                Material.IRON_BARS,
                Locale.CAGE_TRAP_NAME.build(),
                Locale.CAGE_TRAP_LORE.build(),
                Locale.CAGE_TRAP_ACTIVATE.build());
    }

    @Override
    public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
        super.onTrapActivate(game, murderer);
        final Location location = murderer.getLocation();
        final Block block = location.getBlock();
        final Block east = block.getRelative(BlockFace.EAST);
        final Block west = block.getRelative(BlockFace.WEST);
        final Block north = block.getRelative(BlockFace.NORTH);
        final Block south = block.getRelative(BlockFace.SOUTH);
        final Block top = block.getRelative(0, 2, 0);
        final List<Material> history = this.replaceAndSaveOriginalState(east, west, north, south, top);
        murderer.playSound(location, Sound.BLOCK_ANVIL_USE, SoundCategory.MASTER, 1, 1);
        SchedulingUtils.scheduleTask(() -> this.replaceWithOriginal(history, east, west, north, south, top), 7 * 20);
    }

    public void replaceWithOriginal(final List<Material> history, final Block... blocks) {
        for (int i = 0; i < history.size(); i++) {
            final Material material = history.get(i);
            final Block block = blocks[i];
            block.setType(material);
        }
    }

    public List<Material> replaceAndSaveOriginalState(final Block... blocks) {
        final List<Material> list = new ArrayList<>();
        for (final Block block : blocks) {
            final Material type = block.getType();
            list.add(type);
            block.setType(Material.BEDROCK);
        }
        return list;
    }
}
