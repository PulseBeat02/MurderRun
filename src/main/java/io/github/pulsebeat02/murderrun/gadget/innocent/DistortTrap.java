package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.utils.SchedulingUtils;
import org.bukkit.Material;
import org.bukkit.Particle;

public final class DistortTrap extends SurvivorTrap {

    public DistortTrap() {
        super(
                "Distort Trap",
                Material.PRISMARINE_SHARD,
                Locale.DISTORT_TRAP_NAME.build(),
                Locale.DISTORT_TRAP_LORE.build(),
                Locale.DISTORT_TRAP_ACTIVATE.build());
    }

    @Override
    public void activate(final MurderGame game, final GamePlayer murderer) {
        super.activate(game, murderer);
        SchedulingUtils.scheduleRepeatingTaskDuration(() -> this.spawnParticle(murderer), 0, 10, 7 * 20);
    }

    public void spawnParticle(final GamePlayer murderer) {
        murderer.spawnParticle(Particle.ELDER_GUARDIAN, null, 1);
    }
}
