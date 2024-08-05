package io.github.pulsebeat02.murderrun.game.gadget.innocent;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

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
    final Location location = murderer.getLocation();
    final Block block = location.getBlock();
    final Block east = block.getRelative(BlockFace.EAST);
    final Block west = block.getRelative(BlockFace.WEST);
    final Block north = block.getRelative(BlockFace.NORTH);
    final Block south = block.getRelative(BlockFace.SOUTH);
    final Block top = block.getRelative(0, 2, 0);
    final List<Material> history = this.replaceAndSaveOriginalState(east, west, north, south, top);
    final Key key = key("block.anvil.use");
    murderer.playSound(key, Source.MASTER, 1f, 1f);
    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(
        () -> this.replaceWithOriginal(history, east, west, north, south, top), 7 * 20);
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
