package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class BlindTrap extends SurvivorTrap {

  public BlindTrap() {
    super(
        "blind",
        Material.BLACK_STAINED_GLASS,
        Message.BLIND_NAME.build(),
        Message.BLIND_LORE.build(),
        Message.BLIND_ACTIVATE.build(),
        16,
        Color.BLACK);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    murderer.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, 7 * 20, 0));
    murderer.playSound(key("entity.ghast.scream"));
  }
}
