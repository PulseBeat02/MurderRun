package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.map.BlockWhitelistManager;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

public final class CryoFreeze extends SurvivorGadget {

  public CryoFreeze() {
    super("cryo_freeze", Material.ICE, Message.CRYO_FREEZE_NAME.build(), Message.CRYO_FREEZE_LORE.build(), GameProperties.CRYO_FREEZE_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final Map map = game.getMap();
    final BlockWhitelistManager whitelistManager = map.getBlockWhitelistManager();
    final int cx = location.getBlockX();
    final int cy = location.getBlockY();
    final int cz = location.getBlockZ();

    final int radius = GameProperties.CRYO_FREEZE_RADIUS;
    for (int x = -radius; x <= radius; x++) {
      for (int y = -radius; y <= radius; y++) {
        for (int z = -radius; z <= radius; z++) {
          final double distance = Math.sqrt(x * x + y * y + z * z);
          if (distance >= radius - 0.5 && distance <= radius + 0.5) {
            final Block block = world.getBlockAt(cx + x, cy + y, cz + z);
            block.setType(Material.PACKED_ICE);
            whitelistManager.addWhitelistedBlock(block);
          }
        }
      }
    }

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.CRYO_FREEZE_SOUND);

    return false;
  }
}
