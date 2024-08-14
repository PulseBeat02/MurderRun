package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class BlindTrap extends SurvivorTrap {

  public BlindTrap() {
    super(
        "blind",
        Material.BLACK_STAINED_GLASS,
        Locale.BLIND_TRAP_NAME.build(),
        Locale.BLIND_TRAP_LORE.build(),
        Locale.BLIND_TRAP_ACTIVATE.build(),
        16,
        Color.BLACK);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    murderer.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 7, 0));
  }
}
