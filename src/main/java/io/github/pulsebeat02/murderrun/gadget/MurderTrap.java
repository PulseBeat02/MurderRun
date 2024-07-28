package io.github.pulsebeat02.murderrun.gadget;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract sealed class MurderTrap extends MurderGadget permits SurvivorTrap, KillerTrap {

    private final Component announcement;

    public MurderTrap(
            final String name,
            final Material material,
            final Component itemName,
            final Component itemLore,
            final Component announcement) {
        super(name, material, itemName, itemLore);
        this.announcement = announcement;
    }

    public Component getAnnouncement() {
        return this.announcement;
    }

    public void onRightClickEvent(final MurderGame game, final PlayerInteractEvent event) {}

    public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {}

    public void onTrapActivate(final MurderGame game, final GamePlayer activee) {
        if (this.announcement == null) {
            return;
        }
        final PlayerManager manager = game.getPlayerManager();
        manager.applyToAllParticipants(player -> player.sendMessage(this.announcement));
    }
}
