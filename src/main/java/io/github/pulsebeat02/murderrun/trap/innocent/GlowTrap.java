package io.github.pulsebeat02.murderrun.trap.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.trap.SurvivorTrap;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

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
  public void onDropEvent(final PlayerDropItemEvent event) {}

  @Override
  public void activate(final MurderGame game, final Murderer murderer) {
    super.activate(game, murderer);
    PlayerUtils.setGlowColor(murderer, ChatColor.RED);
    this.scheduleTask(() -> PlayerUtils.removeGlow(murderer), 20 * 10);
  }
}
