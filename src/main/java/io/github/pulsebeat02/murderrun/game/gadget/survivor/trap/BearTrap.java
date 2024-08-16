package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class BearTrap extends SurvivorTrap {

  public BearTrap() {
    super(
        "bear",
        Material.IRON_TRAPDOOR,
        Locale.BEAR_TRAP_NAME.build(),
        Locale.BEAR_TRAP_LORE.build(),
        Locale.BEAR_TRAP_ACTIVATE.build(),
        16,
        new Color(35, 23, 9));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    murderer.addPotionEffects(
        new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, Integer.MAX_VALUE),
        new PotionEffect(PotionEffectType.JUMP_BOOST, 5 * 20, Integer.MAX_VALUE));
  }
}
