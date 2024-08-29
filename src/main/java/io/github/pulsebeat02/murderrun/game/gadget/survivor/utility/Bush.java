package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Bush extends SurvivorGadget {

  private static final int BUSH_DURATION = 10 * 20;
  private static final String BUSH_SOUND = "block.sweet_berry_bush.place";

  public Bush() {
    super("bush", Material.OAK_LEAVES, Message.BUSH_NAME.build(), Message.BUSH_LORE.build(), 8);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    player.addPotionEffects(new PotionEffect(PotionEffectType.INVISIBILITY, BUSH_DURATION, 1));

    final Location location = player.getLocation();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> player.teleport(location), 0, 5, BUSH_DURATION);

    final Block block = location.getBlock();
    block.setType(Material.OAK_LEAVES);
    scheduler.scheduleTask(() -> block.setType(Material.AIR), BUSH_DURATION);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(BUSH_SOUND);

    return false;
  }
}
