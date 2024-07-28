package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.resourcepack.sound.FXSound;
import io.github.pulsebeat02.murderrun.utils.SchedulingUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class JumpScareTrap extends SurvivorTrap {

  public JumpScareTrap() {
    super(
        "jump_scare",
        Material.BLACK_CONCRETE,
        Locale.JUMP_SCARE_TRAP_NAME.build(),
        Locale.JUMP_SCARE_TRAP_LORE.build(),
        Locale.JUMP_SCARE_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
    super.onTrapActivate(game, murderer);
    final Location location = murderer.getLocation();
    final ItemStack before = this.setPumpkinItemStack(murderer);
    murderer.playSound(location, FXSound.JUMP_SCARE.getSoundName(), SoundCategory.MASTER, 1, 1);
    murderer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 1));
    murderer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 1));
    SchedulingUtils.scheduleTask(() -> this.setBackHelmet(murderer, before), 2 * 20);
  }

  public void setBackHelmet(final GamePlayer player, final @Nullable ItemStack before) {
    final PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(before);
  }

  public @Nullable ItemStack setPumpkinItemStack(final GamePlayer player) {

    final ItemStack stack = new ItemStack(Material.CARVED_PUMPKIN);
    final ItemMeta meta = stack.getItemMeta();
    if (meta == null) {
      throw new AssertionError("Failed to create jump scare mask!");
    }
    meta.setCustomModelData(1);
    stack.setItemMeta(meta);

    final PlayerInventory inventory = player.getInventory();
    final ItemStack before = inventory.getHelmet();
    inventory.setHelmet(stack);

    return before;
  }
}
