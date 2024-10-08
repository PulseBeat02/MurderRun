package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Fright extends KillerGadget {

  private final Set<GamePlayer> currentlyJumpScared;

  public Fright() {
    super("fright", Material.BLACK_CONCRETE, Message.FRIGHT_NAME.build(), Message.FRIGHT_LORE.build(), GameProperties.FRIGHT_COST);
    this.currentlyJumpScared = Collections.synchronizedSet(new HashSet<>());
  }

  @Override
  public boolean onGadgetDrop(final Game game, final GamePlayer player, final org.bukkit.entity.Item item, final boolean remove) {
    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    manager.applyToAllLivingInnocents(survivor -> this.jumpScareSurvivor(survivor, scheduler));

    return false;
  }

  private void jumpScareSurvivor(final GamePlayer survivor, final GameScheduler scheduler) {
    final ItemStack before = this.setPumpkinItemStack(survivor);
    final int duration = GameProperties.FRIGHT_DURATION;
    survivor.addPotionEffects(
      new PotionEffect(PotionEffectType.BLINDNESS, duration, 1),
      new PotionEffect(PotionEffectType.SLOWNESS, duration, 1)
    );

    final PlayerAudience audience = survivor.getAudience();
    audience.playSound(Sounds.JUMP_SCARE);

    if (this.currentlyJumpScared.contains(survivor)) {
      return;
    }

    scheduler.scheduleTask(() -> this.setBackHelmet(survivor, before), 2 * 20L);
    this.currentlyJumpScared.add(survivor);
  }

  private void setBackHelmet(final GamePlayer player, final @Nullable ItemStack before) {
    final PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(before);
    this.currentlyJumpScared.remove(player);
  }

  private @Nullable ItemStack setPumpkinItemStack(final GamePlayer player) {
    final ItemStack stack = Item.create(Material.CARVED_PUMPKIN);
    final PlayerInventory inventory = player.getInventory();
    final ItemStack before = inventory.getHelmet();
    inventory.setHelmet(stack);
    return before;
  }
}
