/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
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
