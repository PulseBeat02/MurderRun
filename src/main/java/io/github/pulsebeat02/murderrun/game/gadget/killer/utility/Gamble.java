package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class Gamble extends KillerGadget {

  public Gamble() {
    super(
        "gamble",
        Material.END_PORTAL_FRAME,
        Message.GAMBLE_NAME.build(),
        Message.GAMBLE_LORE.build(),
        GameProperties.GAMBLE_COST);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager playerManager = game.getPlayerManager();
    final GadgetManager manager = game.getGadgetManager();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    playerManager.applyToAllLivingInnocents(
        survivor -> this.applyGamble(mechanism, survivor, player));

    final PlayerAudience audience = player.getAudience();
    final Component msg = Message.GAMBLE_ACTIVATE.build();
    audience.sendMessage(msg);
    audience.playSound(GameProperties.GAMBLE_SOUND);

    return false;
  }

  private void applyGamble(
      final GadgetLoadingMechanism mechanism, final GamePlayer survivor, final GamePlayer killer) {
    final DeathManager manager = survivor.getDeathManager();
    final Gadget random = mechanism.getRandomKillerGadget();
    final PlayerDeathTask task =
        new PlayerDeathTask(() -> this.giveKillerItem(killer, random), false);
    manager.addDeathTask(task);
  }

  private void giveKillerItem(final GamePlayer killer, final Gadget gadget) {
    final ItemStack stack = gadget.getGadget();
    final PlayerInventory inventory = killer.getInventory();
    inventory.addItem(stack);
  }
}
