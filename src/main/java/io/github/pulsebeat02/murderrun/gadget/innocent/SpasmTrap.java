package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.scheduler.MurderGameScheduler;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public final class SpasmTrap extends SurvivorTrap {

  private static final Vector UP = new Vector(0, 1, 0);
  private static final Vector DOWN = new Vector(0, -1, 0);

  private final Map<GamePlayer, AtomicBoolean> states;

  public SpasmTrap() {
    super(
        "spasm",
        Material.SEA_LANTERN,
        Locale.SPASM_TRAP_NAME.build(),
        Locale.SPASM_TRAP_LORE.build(),
        Locale.SPASM_TRAP_ACTIVATE.build());
    this.states = new WeakHashMap<>();
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
    super.onTrapActivate(game, murderer);
    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatingTaskDuration(() -> this.alternateHead(murderer), 0, 10, 7 * 20);
  }

  private void alternateHead(final GamePlayer murderer) {
    final AtomicBoolean atomic =
        this.states.computeIfAbsent(murderer, fun -> new AtomicBoolean(false));
    final boolean up = atomic.get();
    final Location location = this.getProperLocation(murderer, up);
    murderer.teleport(location);
  }

  private Location getProperLocation(final GamePlayer murderer, final boolean up) {
    final Location location = murderer.getLocation();
    location.setDirection(up ? UP : DOWN);
    return location;
  }
}
