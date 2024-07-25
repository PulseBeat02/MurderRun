package io.github.pulsebeat02.murderrun.trap.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.trap.SurvivorTrap;
import java.util.Collection;
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
  public void activate(final MurderGame game) {
    super.activate(game);
    final PlayerManager manager = game.getPlayerManager();
    final Collection<Murderer> murderers = manager.getMurderers();
    this.scheduleTask(() -> this.setGlowing(murderers, true), 10);
    this.scheduleTask(() -> this.setGlowing(murderers, false), 20 * 5);
  }

  private void setGlowing(final Collection<Murderer> murderers, final boolean glow) {
    murderers.stream().map(Murderer::getPlayer).forEach(player -> player.setGlowing(glow));
  }
}
