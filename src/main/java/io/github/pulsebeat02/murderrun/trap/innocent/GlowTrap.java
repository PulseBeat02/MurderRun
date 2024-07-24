package io.github.pulsebeat02.murderrun.trap.innocent;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.trap.SurvivorTrap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;

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
  public void activate(final MurderGame game) {
    super.activate(game);
    final MurderRun run = game.getPlugin();
    final PlayerManager manager = game.getPlayerManager();
    final Collection<Murderer> murderers = manager.getMurderers();
    for (final Murderer murderer : murderers) {
      final Player player = murderer.getPlayer();
      player.setGlowing(true);
    }
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.runTaskLater(run, () -> {
      for (final Murderer murderer : murderers) {
        final Player player = murderer.getPlayer();
        player.setGlowing(false);
      }
    }, 20 * 10);
  }
}
