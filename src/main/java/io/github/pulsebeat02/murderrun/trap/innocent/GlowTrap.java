package io.github.pulsebeat02.murderrun.trap.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.trap.SurvivorTrap;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
    final Player player = murderer.getPlayer();
    PlayerUtils.setGlowColor(player, ChatColor.RED);
    this.scheduleTask(() -> PlayerUtils.removeGlow(player), 20 * 10);
  }
}
