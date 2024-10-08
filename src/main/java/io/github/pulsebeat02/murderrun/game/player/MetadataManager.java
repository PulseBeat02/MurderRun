package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
    final WorldBorder[] border = new WorldBorder[1];
    participant.apply(player -> {
      final World world = player.getWorld();
      final WorldBorder worldBorder = world.getWorldBorder();
      final WorldBorder fakeBorder = Bukkit.createWorldBorder();
      fakeBorder.setCenter(worldBorder.getCenter());
      fakeBorder.setDamageAmount(worldBorder.getDamageAmount());
      fakeBorder.setDamageBuffer(worldBorder.getDamageBuffer());
      fakeBorder.setSize(worldBorder.getSize());
      fakeBorder.setWarningDistance(Integer.MAX_VALUE);
      fakeBorder.setWarningTime(worldBorder.getWarningTime());
      border[0] = fakeBorder;
    });
    return border[0];
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
    participant.apply(player -> {
      final Scoreboard scoreboard = player.getScoreboard();
      final ChatColor[] values = ChatColor.values();
      for (final ChatColor color : values) {
        final Team team = this.createGlowTeam(color, scoreboard);
        teams.put(color, team);
      }
    });
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
    if (fake) {
      this.gamePlayer.apply(player -> player.setWorldBorder(this.shadyWorldBorder));
    } else {
      this.gamePlayer.apply(player -> player.setWorldBorder(null));
    }
  }

  public void shutdown() {
    final Collection<ChatColor> keys = this.glowTeams.keySet();
    this.gamePlayer.apply(player -> {
        for (final ChatColor key : keys) {
          final Collection<Entity> stillGlowing = this.glowEntities.get(key);
          for (final Entity entity : stillGlowing) {
            PacketToolsProvider.PACKET_API.setEntityGlowing(entity, player, false);
          }
          final Team team = requireNonNull(this.glowTeams.get(key));
          team.unregister();
        }
      });
    this.glowEntities.clear();
    this.glowTeams.clear();
    this.hideNameTagTeam.unregister();
    this.sidebar.shutdown();
  }

  public void setEntityGlowing(final GameScheduler scheduler, final GamePlayer participant, final ChatColor color, final long time) {
    this.setEntityGlowing(participant, color, true);
    scheduler.scheduleTask(() -> this.setEntityGlowing(participant, color, false), time);
  }

  public void setEntityGlowing(final GamePlayer participant, final ChatColor color, final boolean glowing) {
    participant.apply(player -> this.setEntityGlowing(player, color, glowing));
  }

  public void setEntityGlowing(final Entity entity, final ChatColor color, final boolean glowing) {
    this.gamePlayer.apply(player -> {
        final Team team = requireNonNull(this.glowTeams.get(color));
        final String name = this.getMemberID(entity);
        final String watcher = player.getName();
        if (glowing) {
          this.glowEntities.put(color, entity);
          team.addEntry(name);
          team.addEntry(watcher);
          PacketToolsProvider.PACKET_API.setEntityGlowing(entity, player, true);
        } else {
          this.glowEntities.remove(color, entity);
          PacketToolsProvider.PACKET_API.setEntityGlowing(entity, player, false);
          if (entity instanceof final Player player1) {
            // fixes a protocol bug
            final GameMode gameMode = player1.getGameMode();
            if (gameMode == GameMode.SPECTATOR) {
              return;
            }
          }
          team.removeEntry(name);
          team.removeEntry(watcher);
        }
      });
  }

  private String getMemberID(final Entity entity) {
    if (entity instanceof final Player player) {
      return player.getName();
    }
    final UUID id = entity.getUniqueId();
    return id.toString();
  }

  public void setNameTagStatus(final boolean hide) {
    if (hide) {
      this.gamePlayer.apply(player -> {
          final String name = player.getName();
          this.hideNameTagTeam.addEntry(name);
        });
    } else {
      this.gamePlayer.apply(player -> {
          final GameMode gameMode = player.getGameMode();
          if (gameMode == GameMode.SPECTATOR) {
            return;
          }

          final String name = player.getName();
          if (this.hideNameTagTeam.hasEntry(name)) {
            this.hideNameTagTeam.removeEntry(name);
          }
        });
    }
  }

  public void hideNameTag(final GameScheduler scheduler, final long ticks) {
    this.setNameTagStatus(true);
    scheduler.scheduleTask(() -> this.setNameTagStatus(false), ticks);
  }

  public PlayerScoreboard getSidebar() {
    return this.sidebar;
  }
}
