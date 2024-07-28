package io.github.pulsebeat02.murderrun.trap.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.trap.SurvivorTrap;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class BearTrap extends SurvivorTrap {

    public BearTrap() {
        super(
                "Bear Trap",
                Material.IRON_TRAPDOOR,
                Locale.BEAR_TRAP_NAME.build(),
                Locale.BEAR_TRAP_LORE.build(),
                Locale.BEAR_TRAP_ACTIVATE.build());
    }

    @Override
    public void activate(final MurderGame game, final GamePlayer murderer) {
        super.activate(game, murderer);
        murderer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 7 * 20, 127));
        murderer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 7 * 20, -1));
    }
}