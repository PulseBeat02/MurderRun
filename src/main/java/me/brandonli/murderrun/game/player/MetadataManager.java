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
package me.brandonli.murderrun.game.player;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.*;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.utils.GlowUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class MetadataManager {

  private final GamePlayer gamePlayer;
  private final Multimap<ChatColor, Entity> glowEntities;
  private final Map<ChatColor, Team> glowTeams;
  private final WorldBorder shadyWorldBorder;
  private final Team hideNameTagTeam;

  private PlayerScoreboard sidebar;

  public MetadataManager(final GamePlayer gamePlayer) {
    this.gamePlayer = gamePlayer;
    this.glowTeams = this.createGlowTeams(gamePlayer);
    this.shadyWorldBorder = this.createWorldBorder(gamePlayer);
    this.hideNameTagTeam = this.createHideNameTagTeam();
    this.glowEntities = HashMultimap.create();
  }

  public void start() {
    this.sidebar = new PlayerScoreboard(this.gamePlayer);
    this.sidebar.updateSidebar();
  }

  private WorldBorder createWorldBorder(@UnderInitialization MetadataManager this, final Participant participant) {
    final Location location = participant.getLocation();
    final World world = requireNonNull(location.getWorld());
    final WorldBorder worldBorder = world.getWorldBorder();
    final WorldBorder fakeBorder = Bukkit.createWorldBorder();
    fakeBorder.setCenter(worldBorder.getCenter());
    fakeBorder.setDamageAmount(worldBorder.getDamageAmount());
    fakeBorder.setDamageBuffer(worldBorder.getDamageBuffer());
    fakeBorder.setSize(worldBorder.getSize());
    fakeBorder.setWarningDistance(Integer.MAX_VALUE);
    fakeBorder.setWarningTime(worldBorder.getWarningTime());
    return fakeBorder;
  }

  private Team createHideNameTagTeam(@UnderInitialization MetadataManager this) {
    final ScoreboardManager manager = requireNonNull(Bukkit.getScoreboardManager());
    final Scoreboard scoreboard = manager.getMainScoreboard();
    final UUID uuid = UUID.randomUUID();
    final String name = uuid.toString();
    final Team team = scoreboard.registerNewTeam(name);
    team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
    return team;
  }

  private Map<ChatColor, Team> createGlowTeams(@UnderInitialization MetadataManager this, final GamePlayer participant) {
    final Map<ChatColor, Team> teams = new HashMap<>();
    final Scoreboard scoreboard = participant.getScoreboard();
    final ChatColor[] values = ChatColor.values();
    for (final ChatColor color : values) {
      final Team team = this.createGlowTeam(color, scoreboard);
      teams.put(color, team);
    }
    return teams;
  }

  private Team createGlowTeam(@UnderInitialization MetadataManager this, final ChatColor color, final Scoreboard scoreboard) {
    final UUID uuid = UUID.randomUUID();
    final String name = uuid.toString();
    final Team team = scoreboard.registerNewTeam(name);
    team.setColor(color);
    return team;
  }

  public void setWorldBorderEffect(final boolean fake) {
    this.gamePlayer.setWorldBorder(fake ? this.shadyWorldBorder : null);
  }

  public void shutdown() {
    final Collection<ChatColor> keys = this.glowTeams.keySet();
    this.gamePlayer.apply(player -> this.removeTeams(player, keys));
    this.glowEntities.clear();
    this.glowTeams.clear();
    this.hideNameTagTeam.unregister();
    this.sidebar.shutdown();
  }

  private void removeTeams(final Player player, final Collection<ChatColor> keys) {
    for (final ChatColor key : keys) {
      final Collection<Entity> stillGlowing = this.glowEntities.get(key);
      for (final Entity entity : stillGlowing) {
        GlowUtils.setEntityGlowing(player, entity, false);
      }
      final Team team = requireNonNull(this.glowTeams.get(key));
      team.unregister();
    }
  }

  public void setBlockGlowing(final Block block, final ChatColor color, final boolean glowing) {
    final Location location = block.getLocation();
    if (!this.gamePlayer.isAlive()) {
      return;
    }

    this.gamePlayer.apply(player -> {
        final Team team = requireNonNull(this.glowTeams.get(color));
        final String watcher = player.getName();
        this.spawnSlime(color, glowing, player, location, team, watcher);
      });

    this.gamePlayer.apply(player -> GlowUtils.setBlockGlowing(player, location, glowing));
  }

  private void spawnSlime(
    final ChatColor color,
    final boolean glowing,
    final Player player,
    final Location location,
    final Team team,
    final String watcher
  ) {
    if (glowing) {
      final Slime slime = GlowUtils.setBlockGlowing(player, location, true);
      if (slime == null) {
        return;
      }
      final String name = this.getMemberID(slime);
      this.glowEntities.put(color, slime);
      team.addEntry(name);
      team.addEntry(watcher);
    } else {
      final Slime slime = GlowUtils.setBlockGlowing(player, location, false);
      if (slime == null) {
        return;
      }
      final String name = this.getMemberID(slime);
      this.glowEntities.remove(color, slime);
      team.removeEntry(name);
      team.removeEntry(watcher);
    }
  }

  public void setEntityGlowing(final GameScheduler scheduler, final GamePlayer participant, final ChatColor color, final long time) {
    final StrictPlayerReference reference = StrictPlayerReference.of(participant);
    this.setEntityGlowing(participant, color, true);
    scheduler.scheduleTask(() -> this.setEntityGlowing(participant, color, false), time, reference);
  }

  public void setEntityGlowing(final GamePlayer participant, final ChatColor color, final boolean glowing) {
    participant.apply(player -> this.setEntityGlowing(player, color, glowing));
  }

  public void setEntityGlowing(final Entity entity, final ChatColor color, final boolean glowing) {
    if (!this.gamePlayer.isAlive()) {
      return;
    }

    this.gamePlayer.apply(player -> {
        if (!this.checkValidity(player, entity)) {
          return;
        }
        this.applyGlow(entity, color, glowing, player);
      });
  }

  private void applyGlow(final Entity entity, final ChatColor color, final boolean glowing, final Player player) {
    final Team team = requireNonNull(this.glowTeams.get(color));
    final String name = this.getMemberID(entity);
    final String watcher = player.getName();
    if (glowing) {
      this.glowEntities.put(color, entity);
      team.addEntry(name);
      team.addEntry(watcher);
      GlowUtils.setEntityGlowing(player, entity, true);
    } else {
      this.glowEntities.remove(color, entity);
      GlowUtils.setEntityGlowing(player, entity, false);
      team.removeEntry(name);
      team.removeEntry(watcher);
    }
  }

  private boolean checkValidity(final Entity... entities) {
    return Arrays.stream(entities).anyMatch(Entity::isValid);
  }

  private String getMemberID(final Entity entity) {
    if (entity instanceof final Player player) {
      return player.getName();
    }
    final UUID id = entity.getUniqueId();
    return id.toString();
  }

  public void setNameTagStatus(final boolean hide) {
    if (!this.gamePlayer.isAlive()) {
      return;
    }

    this.gamePlayer.apply(player -> {
        if (!this.checkValidity(player)) {
          return;
        }
        this.applyTag(hide, player);
      });
  }

  private void applyTag(final boolean hide, final Player player) {
    final String name = player.getName();
    if (hide) {
      this.hideNameTagTeam.addEntry(name);
    } else if (this.hideNameTagTeam.hasEntry(name)) {
      this.hideNameTagTeam.removeEntry(name);
    }
  }

  public void hideNameTag(final GameScheduler scheduler, final long ticks) {
    final StrictPlayerReference reference = StrictPlayerReference.of(this.gamePlayer);
    this.setNameTagStatus(true);
    scheduler.scheduleTask(() -> this.setNameTagStatus(false), ticks, reference);
  }

  public PlayerScoreboard getSidebar() {
    return this.sidebar;
  }
}
