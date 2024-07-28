package io.github.pulsebeat02.murderrun.trap.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.trap.SurvivorTrap;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class BearTrap extends SurvivorTrap {



    public BearTrap() {
        super(
                "Bear Trap",
                Material.BROWN_CONCRETE,
                null, null, null);
    }

    @Override
    public void onDropEvent(final PlayerDropItemEvent event) {}

    @Override
    public void activate(final MurderGame game, final Murderer murderer) {
        super.activate(game, murderer);
        PlayerUtils.setGlowColor(murderer, ChatColor.RED);
        this.scheduleTask(() -> PlayerUtils.removeGlow(murderer), 20 * 10);
    }
}
