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

public final class HauntTrap extends SurvivorTrap {

    public HauntTrap() {
        super(
                "haunt",
                Material.WITHER_SKELETON_SKULL,
                Locale.HAUNT_TRAP_NAME.build(),
                Locale.HAUNT_TRAP_LORE.build(),
                Locale.HAUNT_TRAP_ACTIVATE.build());
    }

    @Override
    public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
        super.onTrapActivate(game, murderer);
        murderer.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 7, 10));
        SchedulingUtils.scheduleRepeatingTaskDuration(() -> this.createSpookyEffect(murderer), 0, 14, 7 * 20);
    }

    public void createSpookyEffect(final GamePlayer player) {
        final Location location = player.getLocation();
        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 20, 10));
        player.spawnParticle(Particle.ELDER_GUARDIAN, location, 1);
        SchedulingUtils.scheduleTask(() -> player.removePotionEffect(PotionEffectType.DARKNESS), 20);
    }
}
