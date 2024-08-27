package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class StarTrap extends SurvivorTrap {

  private static final int STAR_TRAP_DURATION = 5 * 20;
  private static final String STAR_TRAP_SOUND = "entity.firework_rocket.blast";

  public StarTrap() {
    super(
        "star",
        Material.FIREWORK_STAR,
        Message.STAR_NAME.build(),
        Message.STAR_LORE.build(),
        Message.STAR_ACTIVATE.build(),
        16,
        new Color(255, 215, 0));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllLivingInnocents(this::addPotionEffect);
    manager.playSoundForAllParticipants(STAR_TRAP_SOUND);
  }

  private void addPotionEffect(final GamePlayer player) {
    player.addPotionEffects(
        new PotionEffect(PotionEffectType.SPEED, STAR_TRAP_DURATION, 2),
        new PotionEffect(PotionEffectType.RESISTANCE, STAR_TRAP_DURATION, 2),
        new PotionEffect(PotionEffectType.REGENERATION, STAR_TRAP_DURATION, 2));
  }
}
