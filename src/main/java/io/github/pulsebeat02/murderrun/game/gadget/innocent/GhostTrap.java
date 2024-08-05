package io.github.pulsebeat02.murderrun.game.gadget.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class GhostTrap extends SurvivorTrap {

  public GhostTrap() {
    super(
        "ghost",
        Material.WHITE_WOOL,
        Locale.GHOST_TRAP_NAME.build(),
        Locale.GHOST_TRAP_LORE.build(),
        Locale.GHOST_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllInnocents(player -> {
      player.addPotionEffects(
          new PotionEffect(PotionEffectType.INVISIBILITY, 10 * 20, 1),
          new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1));
    });
  }
}
