package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class StarTrap extends SurvivorTrap {

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
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllLivingInnocents(this::addPotionEffect);
    manager.playSoundForAllParticipants("entity.firework_rocket.blast");
  }

  private void addPotionEffect(final GamePlayer player) {
    player.addPotionEffects(
        new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2),
        new PotionEffect(PotionEffectType.RESISTANCE, 5 * 20, 2),
        new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 2));
  }
}
