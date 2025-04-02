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
    final Set<Entity> keys = this.entityTeams.keySet();
    // TODO: FIX PROTOCOL ERROR
    //        for (final Entity entity : keys) {
    //          if (!entity.isValid()) {
    //            continue;
    //          }
    //          this.removeEntityGlow(entity);
    //        }
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
    final WrapperPlayServerTeams.CollisionRule collision = WrapperPlayServerTeams.CollisionRule.ALWAYS;
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
