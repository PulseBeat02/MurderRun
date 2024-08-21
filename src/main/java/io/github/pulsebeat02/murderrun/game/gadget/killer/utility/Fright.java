package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;
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
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    manager.applyToAllLivingInnocents(survivor -> this.jumpScareSurvivor(survivor, scheduler));
  }

  private void jumpScareSurvivor(final GamePlayer survivor, final GameScheduler scheduler) {
    final ItemStack before = this.setPumpkinItemStack(survivor);
    final Key key = Sounds.JUMP_SCARE.getKey();
    survivor.playSound(key, Source.MASTER, 1f, 1f);
    survivor.addPotionEffects(
        new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 1),
        new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 1));
    scheduler.scheduleTask(() -> this.setBackHelmet(survivor, before), 2 * 20L);
  }

  private void setBackHelmet(final GamePlayer player, final @Nullable ItemStack before) {
    final PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(before);
  }

  private @Nullable ItemStack setPumpkinItemStack(final GamePlayer player) {
    final ItemStack stack = new ItemStack(Material.CARVED_PUMPKIN);
    final PlayerInventory inventory = player.getInventory();
    final ItemStack before = inventory.getHelmet();
    inventory.setHelmet(stack);
    return before;
  }
}
