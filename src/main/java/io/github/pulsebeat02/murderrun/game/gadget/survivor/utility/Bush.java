package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.data.GadgetConstants;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Bush extends SurvivorGadget {

  public Bush() {
    super("bush", Material.OAK_LEAVES, Message.BUSH_NAME.build(), Message.BUSH_LORE.build(), 8);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final int duration = GadgetConstants.BUSH_DURATION;
    player.addPotionEffects(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 1));

    final PlayerInventory inventory = player.getInventory();
    final Location location = player.getLocation();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> player.teleport(location), 0, 5, duration);

    final ItemStack[] before = inventory.getArmorContents();
    final ItemStack[] empty = new ItemStack[4];
    inventory.setArmorContents(empty);
    scheduler.scheduleTask(() -> inventory.setArmorContents(before), duration);

    final Block block = location.getBlock();
    block.setType(Material.OAK_LEAVES);
    scheduler.scheduleTask(() -> block.setType(Material.AIR), duration);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GadgetConstants.BUSH_SOUND);

    return false;
  }
}
