/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.StrictPlayerReference;
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

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants("entity.witch.celebrate");

    if (this.currentlyJumpScared.contains(murderer)) {
      return;
    }

    final ItemStack before = this.getHelmet(murderer);
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(murderer);
    scheduler.scheduleTask(() -> this.setBackHelmet(murderer, before), GameProperties.JUMP_SCARE_DURATION, reference);
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
