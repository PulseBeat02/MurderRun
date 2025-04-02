/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
