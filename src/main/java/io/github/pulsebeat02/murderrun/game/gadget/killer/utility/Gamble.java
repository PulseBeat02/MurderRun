package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
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
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class Gamble extends KillerGadget {

  public Gamble() {
    super(
        "gamble",
        Material.END_PORTAL_FRAME,
        Message.GAMBLE_NAME.build(),
        Message.GAMBLE_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager playerManager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer killer = playerManager.getGamePlayer(player);
    final GadgetManager manager = game.getGadgetManager();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    playerManager.applyToAllLivingInnocents(
        survivor -> this.applyGamble(mechanism, survivor, killer));

    final PlayerAudience audience = killer.getAudience();
    final Component msg = Message.GAMBLE_ACTIVATE.build();
    audience.sendMessage(msg);
    audience.playSound("block.smithing_table.use");
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
    killer.apply(player -> {
      final ItemStack stack = gadget.getGadget();
      final PlayerInventory inventory = player.getInventory();
      inventory.addItem(stack);
    });
  }
}
