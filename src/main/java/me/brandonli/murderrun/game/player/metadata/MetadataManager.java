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

import java.util.*;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.Participant;
import me.brandonli.murderrun.game.player.PlayerScoreboard;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.utils.GlowUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class MetadataManager {

  private final GamePlayer gamePlayer;
  private final PlayerTeamManager manager;
  private final WorldBorder shadyWorldBorder;
  private final Map<Location, String> blockLayerIds;
  private final Map<UUID, String> entityLayerIds;

  private PlayerScoreboard sidebar;

  public MetadataManager(final GamePlayer gamePlayer) {
    this.gamePlayer = gamePlayer;
    this.shadyWorldBorder = this.createWorldBorder(gamePlayer);
    this.manager = new PlayerTeamManager(gamePlayer);
    this.blockLayerIds = new HashMap<>();
    this.entityLayerIds = new HashMap<>();
  }

  public void start() {
    this.sidebar = new PlayerScoreboard(this.gamePlayer);
    this.sidebar.updateSidebar();
  }

  private WorldBorder createWorldBorder(
      @UnderInitialization MetadataManager this, final Participant participant) {
    final Location location = participant.getLocation();
    final World world = requireNonNull(location.getWorld());
    final WorldBorder worldBorder = world.getWorldBorder();
    final WorldBorder fakeBorder = Bukkit.createWorldBorder();
    fakeBorder.setCenter(worldBorder.getCenter());
    fakeBorder.setDamageAmount(worldBorder.getDamageAmount());
    fakeBorder.setDamageBuffer(worldBorder.getDamageBuffer());
    fakeBorder.setSize(worldBorder.getSize());
    fakeBorder.setWarningDistance(Integer.MAX_VALUE);
    fakeBorder.setWarningTimeTicks(worldBorder.getWarningTimeTicks());
    return fakeBorder;
  }

  public void setWorldBorderEffect(final boolean fake) {
    this.gamePlayer.setWorldBorder(fake ? this.shadyWorldBorder : null);
  }

  public void shutdown() {
    this.manager.destroyAllTeams();
    this.sidebar.shutdown();
  }

  public void setBlockGlowing(
      final Block block, final NamedTextColor color, final boolean glowing) {
    final Location location = block.getLocation();
    if (!this.gamePlayer.isAlive()) {
      return;
    }
    this.gamePlayer.apply(player -> this.spawnSlime(color, glowing, player, location));
  }

  private void spawnSlime(
      final NamedTextColor color,
      final boolean glowing,
      final Player player,
      final Location location) {
    if (glowing) {
      final Slime slime = GlowUtils.setBlockGlowing(player, location, true);
      if (slime == null) {
        return;
      }
      final String layer = this.manager.addGlowLayer(slime, color);
      this.blockLayerIds.put(location, layer);
    } else {
      final Slime slime = GlowUtils.setBlockGlowing(player, location, false);
      if (slime == null) {
        return;
      }
      final String layer = this.blockLayerIds.remove(location);
      if (layer == null) {
        return;
      }
      this.manager.removeGlowLayer(slime, layer);
    }
  }

  public void setEntityGlowing(
      final GameScheduler scheduler,
      final GamePlayer participant,
      final NamedTextColor color,
      final long time) {
    final StrictPlayerReference reference = StrictPlayerReference.of(participant);
    this.setEntityGlowing(participant, color, true);
    scheduler.scheduleTask(() -> this.setEntityGlowing(participant, color, false), time, reference);
  }

  public void setEntityGlowing(
      final GamePlayer participant, final NamedTextColor color, final boolean glowing) {
    participant.apply(player -> this.setEntityGlowing(player, color, glowing));
  }

  public void setEntityGlowing(
      final Entity entity, final NamedTextColor color, final boolean glowing) {
    if (!this.gamePlayer.isAlive()) {
      return;
    }
    if (glowing) {
      final String layer = this.manager.addGlowLayer(entity, color);
      final UUID uuid = entity.getUniqueId();
      this.entityLayerIds.put(uuid, layer);
    } else {
      final UUID uuid = entity.getUniqueId();
      final String layer = this.entityLayerIds.remove(uuid);
      if (layer == null) {
        return;
      }
      this.manager.removeGlowLayer(entity, layer);
    }
  }

  public PlayerScoreboard getSidebar() {
    return this.sidebar;
  }
}
