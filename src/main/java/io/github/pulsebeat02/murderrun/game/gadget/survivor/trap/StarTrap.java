package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;
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
      GameProperties.STAR_COST,
      new Color(255, 215, 0)
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllLivingInnocents(this::addPotionEffect);
    manager.playSoundForAllParticipants(GameProperties.STAR_SOUND);
  }

  private void addPotionEffect(final GamePlayer player) {
    final int duration = GameProperties.STAR_DURATION;
    player.addPotionEffects(
      new PotionEffect(PotionEffectType.SPEED, duration, 2),
      new PotionEffect(PotionEffectType.RESISTANCE, duration, 2),
      new PotionEffect(PotionEffectType.REGENERATION, duration, 2)
    );
  }
}
