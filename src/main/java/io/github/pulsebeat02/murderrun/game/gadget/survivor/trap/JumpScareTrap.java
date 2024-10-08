package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class JumpScareTrap extends SurvivorTrap {

  private final Set<GamePlayer> currentlyJumpScared;

  public JumpScareTrap() {
    super(
      "jump_scare",
      Material.BLACK_CONCRETE,
      Message.JUMP_SCARE_NAME.build(),
      Message.JUMP_SCARE_LORE.build(),
      Message.JUMP_SCARE_ACTIVATE.build(),
      GameProperties.JUMP_SCARE_COST,
      Color.RED
    );
    this.currentlyJumpScared = Collections.synchronizedSet(new HashSet<>());
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final org.bukkit.entity.Item item) {
    final int duration = GameProperties.JUMP_SCARE_EFFECT_DURATION;
    murderer.addPotionEffects(
      new PotionEffect(PotionEffectType.BLINDNESS, duration, 1),
      new PotionEffect(PotionEffectType.SLOWNESS, duration, 1)
    );

    final PlayerAudience audience = murderer.getAudience();
    audience.playSound(Sounds.JUMP_SCARE);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants("entity.witch.celebrate");

    if (this.currentlyJumpScared.contains(murderer)) {
      return;
    }

    final ItemStack before = this.getHelmet(murderer);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.setBackHelmet(murderer, before), GameProperties.JUMP_SCARE_DURATION);
    this.currentlyJumpScared.add(murderer);
  }

  private void setBackHelmet(final GamePlayer player, final @Nullable ItemStack before) {
    final PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(before);
    this.currentlyJumpScared.remove(player);
  }

  private @Nullable ItemStack getHelmet(final GamePlayer player) {
    final ItemStack stack = Item.create(Material.CARVED_PUMPKIN);
    final PlayerInventory inventory = player.getInventory();
    final ItemStack before = inventory.getHelmet();
    inventory.setHelmet(stack);
    return before;
  }
}
