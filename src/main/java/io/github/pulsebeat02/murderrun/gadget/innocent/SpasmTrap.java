package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.utils.SchedulingUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicBoolean;

public final class SpasmTrap extends SurvivorTrap {

    private static final Vector UP = new Vector(0, 1, 0);
    private static final Vector DOWN = new Vector(0, -1, 0);

    private final AtomicBoolean state;

    public SpasmTrap() {
        super(
                "Spasm Trap",
                Material.SEA_LANTERN,
                Locale.SPASM_TRAP_NAME.build(),
                Locale.SPASM_TRAP_LORE.build(),
                Locale.SPASM_TRAP_ACTIVATE.build());
        this.state = new AtomicBoolean(true);
    }

    @Override
    public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
        super.onTrapActivate(game, murderer);
        SchedulingUtils.scheduleRepeatingTaskDuration(() -> this.alternateHead(murderer), 0, 10, 7 * 20);
    }

    public void alternateHead(final GamePlayer murderer) {
        final boolean up = this.state.get();
        final Location location = this.getProperLocation(murderer, up);
        murderer.teleport(location);
    }

    public Location getProperLocation(final GamePlayer murderer, final boolean up) {
        final Location location = murderer.getLocation();
        final Location clone = location.clone();
        clone.setDirection(up ? UP : DOWN);
        return clone;
    }
}

