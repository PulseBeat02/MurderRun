package io.github.pulsebeat02.murderrun.commmand.game;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class InviteManager {

  private final Map<Player, PlayerInviteManager> invites;

  public InviteManager() {
    this.invites = new HashMap<>();
  }

  public void invitePlayer(final CommandSender sender, final Player receiver) {
    this.invites.putIfAbsent(receiver, new PlayerInviteManager());
    final PlayerInviteManager manager = this.invites.get(receiver);
    manager.addInvite(sender);
  }

  public void removeInvite(final CommandSender sender, final Player receiver) {
    final PlayerInviteManager manager = this.invites.get(receiver);
    if (manager == null) {
      return;
    }

    manager.removeInvite(sender);
  }

  public boolean hasInvite(final CommandSender sender, final Player receiver) {
    final PlayerInviteManager manager = this.invites.get(receiver);
    if (manager == null) {
      return false;
    }

    return manager.hasInvite(sender);
  }
}
