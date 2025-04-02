/*

MIT License

Copyright (c) 2025 Brandon Li

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
package me.brandonli.murderrun.game.extension.parties;

import static java.util.Objects.requireNonNull;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import java.util.Collection;
import java.util.UUID;
import me.brandonli.murderrun.MurderRun;
import org.bukkit.entity.Player;

public final class PartiesManager {

  private final MurderRun plugin;
  private final PartiesAPI api;

  public PartiesManager(final MurderRun plugin) {
    this.plugin = plugin;
    this.api = Parties.getApi();
  }

  public boolean isInParty(final Player player) {
    final UUID uuid = player.getUniqueId();
    final Party party = this.api.getPartyOfPlayer(uuid);
    return party != null;
  }

  public boolean isLeader(final Player player) {
    final UUID uuid = player.getUniqueId();
    final Party party = requireNonNull(this.api.getPartyOfPlayer(uuid));
    final UUID leaderUUID = requireNonNull(party.getLeader());
    final PartyPlayer partyPlayer = requireNonNull(this.api.getPartyPlayer(leaderUUID));
    final UUID leader = partyPlayer.getPlayerUUID();
    return uuid.equals(leader);
  }

  public Collection<UUID> getPartyMembers(final Player player) {
    final UUID uuid = player.getUniqueId();
    final Party party = requireNonNull(this.api.getPartyOfPlayer(uuid));
    return party.getMembers();
  }

  public UUID getPartyId(final Player player) {
    final UUID uuid = player.getUniqueId();
    final Party party = requireNonNull(this.api.getPartyOfPlayer(uuid));
    return party.getId();
  }

  public UUID getBukkitUuid(final UUID partyUuid) {
    final PartyPlayer partyPlayer = requireNonNull(this.api.getPartyPlayer(partyUuid));
    return partyPlayer.getPlayerUUID();
  }
}
