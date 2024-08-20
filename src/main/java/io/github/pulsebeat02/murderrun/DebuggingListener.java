package io.github.pulsebeat02.murderrun;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

public final class DebuggingListener implements Listener {

    public DebuggingListener(final MurderRun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onResourcePackEvent(final PlayerResourcePackStatusEvent event) {
        final Player player = event.getPlayer();
        final String name = player.getDisplayName();
        final Status status = event.getStatus();
        final String message = String.format("Player %s has a status of %s", name, status);
        System.out.println(message);
    }
}
