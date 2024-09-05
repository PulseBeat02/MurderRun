package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.DisguiseManager;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class Mimic extends KillerGadget {

  public Mimic() {
    super(
        "mimic", Material.GHAST_TEAR, Message.MIMIC_NAME.build(), Message.MIMIC_LORE.build(), 128);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer survivor = manager.getRandomAliveInnocentPlayer();
    final DisguiseManager disguiseManager = game.getDisguiseManager();
    disguiseManager.disguisePlayerAsOtherPlayer(player, survivor);

    final PlayerInventory otherInventory = survivor.getInventory();
    final ItemStack[] armor = otherInventory.getArmorContents();

    final PlayerInventory thisInventory = player.getInventory();
    thisInventory.setArmorContents(armor);

    final Component msg = Message.MIMIC_ACTIVATE.build();
    final PlayerAudience audience = player.getAudience();
    audience.sendMessage(msg);

    return false;
  }
}
