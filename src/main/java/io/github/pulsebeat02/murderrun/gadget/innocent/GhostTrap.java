package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
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
    super.onTrapActivate(game, murderer);
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllInnocents(player -> {
      player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10 * 20, 1));
      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1));
    });
  }
}
