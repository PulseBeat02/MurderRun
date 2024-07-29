package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class FreezeTrap extends SurvivorTrap {

    public FreezeTrap() {
        super(
                "freeze",
                Material.PACKED_ICE,
                Locale.FREEZE_TRAP_NAME.build(),
                Locale.FREEZE_TRAP_LORE.build(),
                Locale.FREEZE_TRAP_ACTIVATE.build());
    }

    @Override
    public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
        super.onTrapActivate(game, murderer);
        final Player player = murderer.getPlayer();
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, Integer.MAX_VALUE));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 7 * 20,  Integer.MAX_VALUE));
        player.setFreezeTicks(7 * 20);
    }
}

