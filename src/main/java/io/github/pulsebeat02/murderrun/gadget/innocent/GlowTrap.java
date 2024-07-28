package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import io.github.pulsebeat02.murderrun.utils.SchedulingUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public final class GlowTrap extends SurvivorTrap {

  public GlowTrap() {
    super(
        "Glow Trap",
        Material.GLOWSTONE,
        Locale.GLOW_TRAP_NAME.build(),
        Locale.GLOW_TRAP_LORE.build(),
        Locale.GLOW_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
    super.onTrapActivate(game, murderer);
    PlayerUtils.setGlowColor(murderer, ChatColor.RED);
    SchedulingUtils.scheduleTask(() -> PlayerUtils.removeGlow(murderer), 7 * 20);
  }
}
