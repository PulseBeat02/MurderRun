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
package me.brandonli.murderrun.game.player.metadata;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.NameTagVisibility;
import java.util.*;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.utils.GlowUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class PlayerTeamManager {

  private final GamePlayer watcher;
  private final Map<Entity, String> entityTeams;
  private final Map<String, NamedTextColor> states;

  public PlayerTeamManager(final GamePlayer watcher) {
    this.watcher = watcher;
    this.entityTeams = new HashMap<>();
    this.states = new HashMap<>();
  }

  public void destroyAllTeams() {
    this.entityTeams.clear();
    this.states.clear();
  }

  public void removeEntityGlow(final Entity entity) {
    this.removeTeam(entity);
    this.watcher.apply(player -> GlowUtils.setEntityGlowing(player, entity, false));
  }

  public void setEntityGlow(final Entity entity, final NamedTextColor color) {
    final String team = this.entityTeams.getOrDefault(entity, this.createTeam(entity, color));
    final NamedTextColor state = requireNonNull(this.states.get(team));
    if (state != color) {
      this.removeTeam(entity);
      this.createTeam(entity, color);
    }
    this.watcher.apply(player -> GlowUtils.setEntityGlowing(player, entity, true));
  }

  private void removeTeam(final Entity entity) {
    final String team = this.entityTeams.remove(entity);
    if (team == null) {
      return;
    }

    final String entryName = this.getEntryName(entity);
    final String watcherName = this.watcher.getName();
    final WrapperPlayServerTeams.TeamMode removeEntitiesMode = WrapperPlayServerTeams.TeamMode.REMOVE_ENTITIES;
    final WrapperPlayServerTeams removeEntitiesPacket = new WrapperPlayServerTeams(
      team,
      removeEntitiesMode,
      (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
      entryName,
      watcherName
    );
    this.watcher.sendPacket(removeEntitiesPacket);

    final WrapperPlayServerTeams.TeamMode removeMode = WrapperPlayServerTeams.TeamMode.REMOVE;
    final WrapperPlayServerTeams removePacket = new WrapperPlayServerTeams(
      team,
      removeMode,
      (WrapperPlayServerTeams.ScoreBoardTeamInfo) null
    );
    this.watcher.sendPacket(removePacket);

    this.states.remove(team);
  }

  private String createTeam(final Entity entry, final NamedTextColor color) {
    final String entryName = this.getEntryName(entry);
    final String watcherName = this.watcher.getName();
    final UUID watcherUUID = entry.getUniqueId();
    final String teamName = watcherUUID.toString();
    final WrapperPlayServerTeams.TeamMode teamMode = WrapperPlayServerTeams.TeamMode.CREATE;

    final Component display = empty();
    final Component prefix = empty();
    final Component suffix = empty();
    final NameTagVisibility visibility = NameTagVisibility.ALWAYS;
    final WrapperPlayServerTeams.CollisionRule collision = WrapperPlayServerTeams.CollisionRule.NEVER;
    final WrapperPlayServerTeams.OptionData data = WrapperPlayServerTeams.OptionData.ALL;

    final WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
      display,
      prefix,
      suffix,
      visibility,
      collision,
      color,
      data
    );
    final WrapperPlayServerTeams packet = new WrapperPlayServerTeams(teamName, teamMode, teamInfo);
    this.watcher.sendPacket(packet);

    final WrapperPlayServerTeams.TeamMode addMode = WrapperPlayServerTeams.TeamMode.ADD_ENTITIES;
    final WrapperPlayServerTeams addPacket = new WrapperPlayServerTeams(teamName, addMode, teamInfo, entryName, watcherName);
    this.watcher.sendPacket(addPacket);

    final WrapperPlayServerTeams.TeamMode updateMode = WrapperPlayServerTeams.TeamMode.UPDATE;
    final WrapperPlayServerTeams updatePacket = new WrapperPlayServerTeams(teamName, updateMode, teamInfo);
    this.watcher.sendPacket(updatePacket);

    this.states.put(teamName, color);
    this.entityTeams.put(entry, teamName);

    return teamName;
  }

  private String getEntryName(final Entity entity) {
    if (entity instanceof final Player player) {
      return player.getName();
    } else {
      final UUID uuid = entity.getUniqueId();
      return uuid.toString();
    }
  }
}
