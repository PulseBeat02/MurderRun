package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.data.GadgetConstants;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Fright extends KillerGadget {

  public Fright() {
    super(
        "fright",
        Material.BLACK_CONCRETE,
        Message.FRIGHT_NAME.build(),
        Message.FRIGHT_LORE.build(),
        32);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game,
      final GamePlayer player,
      final org.bukkit.entity.Item item,
      final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    manager.applyToAllLivingInnocents(survivor -> this.jumpScareSurvivor(survivor, scheduler));

    return false;
  }

  private void jumpScareSurvivor(final GamePlayer survivor, final GameScheduler scheduler) {

    final ItemStack before = this.setPumpkinItemStack(survivor);
    final int duration = GadgetConstants.FRIGHT_DURATION;
    survivor.addPotionEffects(
        new PotionEffect(PotionEffectType.BLINDNESS, duration, 1),
        new PotionEffect(PotionEffectType.SLOWNESS, duration, 1));
    scheduler.scheduleTask(() -> this.setBackHelmet(survivor, before), 2 * 20L);

    final PlayerAudience audience = survivor.getAudience();
    audience.playSound(Sounds.JUMP_SCARE);
  }

  private void setBackHelmet(final GamePlayer player, final @Nullable ItemStack before) {
    final PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(before);
  }

  private @Nullable ItemStack setPumpkinItemStack(final GamePlayer player) {
    final ItemStack stack = Item.create(Material.CARVED_PUMPKIN);
    final PlayerInventory inventory = player.getInventory();
    final ItemStack before = inventory.getHelmet();
    inventory.setHelmet(stack);
    return before;
  }
}
