package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.TNTPrimed;

public final class QuickBomb extends KillerGadget {

  public QuickBomb() {
    super("quick_bomb", Material.TNT, Message.QUICK_BOMB_NAME.build(), Message.QUICK_BOMB_LORE.build(), GameProperties.QUICK_BOMB_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final PlayerManager manager = game.getPlayerManager();
    manager.applyToLivingSurvivors(this::spawnPrimedTnt);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.QUICK_BOMB_SOUND);

    return false;
  }

  private void spawnPrimedTnt(final GamePlayer survivor) {
    final Location location = survivor.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawn(location, TNTPrimed.class, tnt -> tnt.setFuseTicks(40));
  }
}
