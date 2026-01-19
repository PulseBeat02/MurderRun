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

import static net.kyori.adventure.text.Component.empty;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.NameTagVisibility;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.utils.GlowUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PlayerTeamManager {

  private final GamePlayer watcher;
  private final Map<Entity, String> entityTeams;
  private final Map<String, NamedTextColor> teamColors;
  private final Map<Entity, Map<String, GlowLayer>> layersByEntity;
  private final AtomicLong seqCounter = new AtomicLong(0L);

  private record GlowLayer(NamedTextColor color, long priority, long seq) {}

  public PlayerTeamManager(final GamePlayer watcher) {
    this.watcher = watcher;
    this.entityTeams = new ConcurrentHashMap<>();
    this.teamColors = new ConcurrentHashMap<>();
    this.layersByEntity = new ConcurrentHashMap<>();
  }

  public String addGlowLayer(final Entity entity, final NamedTextColor color) {
    return this.addGlowLayer(entity, null, color, null);
  }

  public String addGlowLayer(
      final Entity entity, final String layerId, final NamedTextColor color) {
    return this.addGlowLayer(entity, layerId, color, null);
  }

  public String addGlowLayer(
      final Entity entity,
      final @Nullable String layerId,
      final NamedTextColor color,
      final @Nullable Integer priority) {

    final Map<String, GlowLayer> layers =
        this.layersByEntity.computeIfAbsent(entity, e -> new ConcurrentHashMap<>());

    final String id = (layerId == null || layerId.isBlank())
        ? "auto_" + this.seqCounter.incrementAndGet()
        : layerId;

    final long p = (priority == null) ? this.seqCounter.incrementAndGet() : priority.longValue();
    final long seq = this.seqCounter.incrementAndGet();

    layers.put(id, new GlowLayer(color, p, seq));
    this.applyResolvedGlow(entity);
    return id;
  }

  public void removeGlowLayer(final Entity entity, final String layerId) {
    if (layerId == null) {
      return;
    }
    final Map<String, GlowLayer> layers = this.layersByEntity.get(entity);
    if (layers == null) {
      return;
    }

    layers.remove(layerId);
    if (layers.isEmpty()) {
      this.layersByEntity.remove(entity);
      this.clearGlow(entity);
      return;
    }

    this.applyResolvedGlow(entity);
  }

  public void clearAllGlowLayers(final Entity entity) {
    this.layersByEntity.remove(entity);
    this.clearGlow(entity);
  }

  public void destroyAllTeams() {
    this.entityTeams.clear();
    this.layersByEntity.clear();

    for (final String teamName : this.teamColors.keySet()) {
      final WrapperPlayServerTeams removePacket = new WrapperPlayServerTeams(
          teamName,
          WrapperPlayServerTeams.TeamMode.REMOVE,
          (WrapperPlayServerTeams.ScoreBoardTeamInfo) null);
      this.watcher.sendPacket(removePacket);
    }

    this.teamColors.clear();
  }

  private void applyResolvedGlow(final Entity entity) {
    final GlowLayer resolved = this.resolveLayer(entity);
    if (resolved == null) {
      this.clearGlow(entity);
      return;
    }
    this.setResolvedGlow(entity, resolved.color());
  }

  private @Nullable GlowLayer resolveLayer(final Entity entity) {
    final Map<String, GlowLayer> layers = this.layersByEntity.get(entity);
    if (layers == null || layers.isEmpty()) {
      return null;
    }

    GlowLayer best = null;
    for (final GlowLayer layer : layers.values()) {
      if (best == null) {
        best = layer;
        continue;
      }
      if (layer.priority() > best.priority()) {
        best = layer;
      } else if (layer.priority() == best.priority() && layer.seq() > best.seq()) {
        best = layer;
      }
    }
    return best;
  }

  private void setResolvedGlow(final Entity entity, final NamedTextColor color) {
    final String teamName = this.getTeamName(entity, color);
    final String entryName = this.getEntryName(entity);
    this.ensureTeam(teamName, color);
    final String oldTeam = this.entityTeams.get(entity);
    if (oldTeam == null) {
      this.sendAddEntity(teamName, entryName);
      this.entityTeams.put(entity, teamName);
    } else if (!oldTeam.equals(teamName)) {
      this.sendRemoveEntity(oldTeam, entryName);
      this.sendAddEntity(teamName, entryName);
      this.entityTeams.put(entity, teamName);
    }

    this.watcher.apply(player -> GlowUtils.setEntityGlowing(player, entity, true));
  }

  private void clearGlow(final Entity entity) {
    this.removeEntityFromTeam(entity);
    this.watcher.apply(player -> GlowUtils.setEntityGlowing(player, entity, false));
  }

  private void removeEntityFromTeam(final Entity entity) {
    final String teamName = this.entityTeams.remove(entity);
    if (teamName == null) {
      return;
    }
    final String entryName = this.getEntryName(entity);
    this.sendRemoveEntity(teamName, entryName);
  }

  private void ensureTeam(final String teamName, final NamedTextColor color) {
    final NamedTextColor prevColor = this.teamColors.get(teamName);
    final WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = this.buildTeamInfo(color);

    if (prevColor == null) {
      final WrapperPlayServerTeams createPacket =
          new WrapperPlayServerTeams(teamName, WrapperPlayServerTeams.TeamMode.CREATE, teamInfo);
      this.watcher.sendPacket(createPacket);
      this.teamColors.put(teamName, color);
      return;
    }

    if (prevColor != color) {
      final WrapperPlayServerTeams updatePacket =
          new WrapperPlayServerTeams(teamName, WrapperPlayServerTeams.TeamMode.UPDATE, teamInfo);
      this.watcher.sendPacket(updatePacket);
      this.teamColors.put(teamName, color);
    }
  }

  private WrapperPlayServerTeams.ScoreBoardTeamInfo buildTeamInfo(final NamedTextColor color) {
    final Component display = empty();
    final Component prefix = empty();
    final Component suffix = empty();
    final NameTagVisibility visibility = NameTagVisibility.ALWAYS;
    final WrapperPlayServerTeams.CollisionRule collision =
        WrapperPlayServerTeams.CollisionRule.NEVER;
    final WrapperPlayServerTeams.OptionData data = WrapperPlayServerTeams.OptionData.ALL;
    return new WrapperPlayServerTeams.ScoreBoardTeamInfo(
        display, prefix, suffix, visibility, collision, color, data);
  }

  private void sendAddEntity(final String teamName, final String entryName) {
    final WrapperPlayServerTeams addPacket = new WrapperPlayServerTeams(
        teamName,
        WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
        (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
        entryName);
    this.watcher.sendPacket(addPacket);
  }

  private void sendRemoveEntity(final String teamName, final String entryName) {
    final WrapperPlayServerTeams removeEntitiesPacket = new WrapperPlayServerTeams(
        teamName,
        WrapperPlayServerTeams.TeamMode.REMOVE_ENTITIES,
        (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
        entryName);
    this.watcher.sendPacket(removeEntitiesPacket);
  }

  private String getTeamName(final Entity entity, final NamedTextColor color) {
    final UUID uuid = entity.getUniqueId();
    final String base = uuid.toString().replace("-", "");
    final String c = color.toString().toLowerCase(Locale.ROOT);
    final String name = "mr_" + c + "_" + base;
    return name.length() <= 16 ? name : name.substring(0, 16);
  }

  private String getEntryName(final Entity entity) {
    if (entity instanceof final Player player) {
      return player.getName();
    }
    return entity.getUniqueId().toString();
  }
}
