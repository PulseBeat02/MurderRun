package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.awt.Color;
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
        Locale.GHOST_TRAP_ACTIVATE.build(),
        Color.WHITE);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllInnocents(player -> player.addPotionEffects(
        new PotionEffect(PotionEffectType.INVISIBILITY, 10 * 20, 1),
        new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1)));
  }
}
