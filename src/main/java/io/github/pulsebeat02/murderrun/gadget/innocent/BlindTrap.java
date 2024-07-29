package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
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
        Locale.BLIND_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
    super.onTrapActivate(game, murderer);
    murderer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 7, 0));
  }
}
