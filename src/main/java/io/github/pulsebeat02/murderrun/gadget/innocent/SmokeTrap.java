package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.utils.SchedulingUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class SmokeTrap extends SurvivorTrap {

    public SmokeTrap() {
        super(
                "Smoke Trap",
                Material.GUNPOWDER,
                Locale.SMOKE_TRAP_NAME.build(),
                Locale.SMOKE_TRAP_LORE.build(),
                Locale.SMOKE_TRAP_ACTIVATE.build());
    }

    @Override
    public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
        super.onTrapActivate(game, murderer);
        murderer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 7 * 20, 1));
        murderer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 7 * 20, 2));
        SchedulingUtils.scheduleRepeatingTaskDuration(() -> this.spawnSmoke(murderer), 0, 10, 7 * 20);
    }

    public void spawnSmoke(final GamePlayer murderer) {
        final Location location = murderer.getLocation();
        murderer.spawnParticle(Particle.SMOKE, location, 25, 2, 2, 2);
    }
}
