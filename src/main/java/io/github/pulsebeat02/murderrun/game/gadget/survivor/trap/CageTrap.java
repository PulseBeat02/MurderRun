package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public final class CageTrap extends SurvivorTrap {

  public CageTrap() {
    super(
        "cage",
        Material.IRON_BARS,
        Message.CAGE_NAME.build(),
        Message.CAGE_LORE.build(),
        Message.CAGE_ACTIVATE.build(),
        48,
        Color.GRAY);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {

    final Location location = murderer.getLocation();
    final Block block = location.getBlock();
    final Block down = block.getRelative(BlockFace.DOWN);
    final Block east = block.getRelative(BlockFace.EAST);
    final Block west = block.getRelative(BlockFace.WEST);
    final Block north = block.getRelative(BlockFace.NORTH);
    final Block south = block.getRelative(BlockFace.SOUTH);
    final Block top = block.getRelative(0, 2, 0);
    final List<Material> history =
        this.replaceAndSaveOriginalState(down, east, west, north, south, top);
    murderer.playSound(key("block.anvil.use"));

    final GameScheduler scheduler = game.getScheduler();
    final Runnable task =
        () -> this.replaceWithOriginal(history, down, east, west, north, south, top);
    scheduler.scheduleTask(task, 7 * 20L);
  }

  private void replaceWithOriginal(final List<Material> history, final Block... blocks) {
    for (int i = 0; i < history.size(); i++) {
      final Material material = history.get(i);
      final Block block = blocks[i];
      block.setType(material);
    }
  }

  private List<Material> replaceAndSaveOriginalState(final Block... blocks) {
    final List<Material> list = new ArrayList<>();
    for (final Block block : blocks) {
      final Material type = block.getType();
      list.add(type);
      block.setType(Material.BEDROCK);
    }
    return list;
  }
}
