package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundResource;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.awt.Color;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class JumpScareTrap extends SurvivorTrap {

  private static final int JUMP_SCARE_TRAP_DURATION = 2 * 20;
  private static final int JUMP_SCARE_TRAP_EFFECT_DURATION = 5 * 20;
  private static final String JUMP_SCARE_TRAP_SOUND;

  static {
    final SoundResource resource = Sounds.JUMP_SCARE;
    final Key key = resource.getKey();
    JUMP_SCARE_TRAP_SOUND = key.asString();
  }

  public JumpScareTrap() {
    super(
        "jump_scare",
        Material.BLACK_CONCRETE,
        Message.JUMP_SCARE_NAME.build(),
        Message.JUMP_SCARE_LORE.build(),
        Message.JUMP_SCARE_ACTIVATE.build(),
        32,
        Color.RED);
  }

  @Override
  public void onTrapActivate(
      final Game game, final GamePlayer murderer, final org.bukkit.entity.Item item) {

    murderer.addPotionEffects(
        new PotionEffect(PotionEffectType.BLINDNESS, JUMP_SCARE_TRAP_EFFECT_DURATION, 1),
        new PotionEffect(PotionEffectType.SLOWNESS, JUMP_SCARE_TRAP_EFFECT_DURATION, 1));

    final ItemStack before = this.getHelmet(murderer);
    final PlayerAudience audience = murderer.getAudience();
    audience.playSound(JUMP_SCARE_TRAP_SOUND);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.setBackHelmet(murderer, before), JUMP_SCARE_TRAP_DURATION);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants("entity.witch.celebrate");
  }

  private void setBackHelmet(final GamePlayer player, final @Nullable ItemStack before) {
    final PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(before);
  }

  private @Nullable ItemStack getHelmet(final GamePlayer player) {
    final ItemStack stack = Item.create(Material.CARVED_PUMPKIN);
    final PlayerInventory inventory = player.getInventory();
    final ItemStack before = inventory.getHelmet();
    inventory.setHelmet(stack);
    return before;
  }
}
