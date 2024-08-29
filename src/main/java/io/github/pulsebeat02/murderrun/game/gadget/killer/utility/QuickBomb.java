package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
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

  private static final String QUICK_BOMB_SOUND = "entity.creeper.primed";

  public QuickBomb() {
    super(
        "quick_bomb",
        Material.TNT,
        Message.QUICK_BOMB_NAME.build(),
        Message.QUICK_BOMB_LORE.build(),
        32);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllLivingInnocents(this::spawnPrimedTnt);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(QUICK_BOMB_SOUND);

    return false;
  }

  private void spawnPrimedTnt(final GamePlayer survivor) {
    final Location location = survivor.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawn(location, TNTPrimed.class, tnt -> tnt.setFuseTicks(40));
  }
}
