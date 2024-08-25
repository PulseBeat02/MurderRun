package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.bossbar.BossBar.bossBar;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.sound.SoundStop.named;
import static net.kyori.adventure.title.Title.title;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract sealed class GamePlayer implements Participant permits Survivor, Killer {

  private static final String HIDE_NAME_TAG_TEAM_NAME = "hide-name-tag-%s";

  private final Game game;
  private final UUID uuid;
  private final Audience audience;
  private final Collection<PlayerDeathTask> tasks;
  private final Collection<BossBar> bars;
  private final Map<UUID, UUID> glowEntities;
  private final Map<UUID, Team> nameTags;
  private final Map<UUID, WorldBorder> worldBorders;

  private @Nullable ArmorStand corpse;
  private boolean alive;

  public GamePlayer(final Game game, final UUID uuid) {
    this.game = game;
    this.uuid = uuid;
    this.audience = this.getAudience(game, uuid);
    this.alive = true;
    this.bars = new HashSet<>();
    this.tasks = new HashSet<>();
    this.glowEntities = new HashMap<>();
    this.nameTags = new HashMap<>();
    this.worldBorders = new HashMap<>();
  }

  private Audience getAudience(
      @UnderInitialization GamePlayer this, final Game game, final UUID uuid) {
    final MurderRun plugin = game.getPlugin();
    final AudienceProvider handler = plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    return audiences.player(uuid);
  }

  @Override
  public void addFakeWorldBorderEffect() {
    this.apply(player -> {
      final World world = player.getWorld();
      final UUID id = world.getUID();
      this.worldBorders.computeIfAbsent(id, ignore -> {
        final WorldBorder worldBorder = world.getWorldBorder();
        final WorldBorder fakeBorder = Bukkit.createWorldBorder();
        fakeBorder.setCenter(worldBorder.getCenter());
        fakeBorder.setDamageAmount(worldBorder.getDamageAmount());
        fakeBorder.setDamageBuffer(worldBorder.getDamageBuffer());
        fakeBorder.setSize(worldBorder.getSize());
        fakeBorder.setWarningDistance(Integer.MAX_VALUE);
        fakeBorder.setWarningTime(worldBorder.getWarningTime());
        player.setWorldBorder(fakeBorder);
        return fakeBorder;
      });
    });
  }

  @Override
  public void hideNameTag(final GameScheduler scheduler, final long ticks) {
    this.apply(player -> {
      final UUID id = player.getUniqueId();
      final ScoreboardManager manager = requireNonNull(Bukkit.getScoreboardManager());
      final Scoreboard scoreboard = manager.getMainScoreboard();
      final UUID hideID = UUID.randomUUID();
      final String name = HIDE_NAME_TAG_TEAM_NAME.formatted(hideID);
      final Team team = scoreboard.registerNewTeam(name);
      team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
      team.addEntry(player.getName());
      this.nameTags.put(id, team);
    });
    scheduler.scheduleTask(this::showNameTag, ticks);
  }

  @Override
  public void showNameTag() {
    this.apply(player -> {
      final UUID id = player.getUniqueId();
      final Team team = this.nameTags.get(id);
      if (team == null) {
        return;
      }
      team.unregister();
      this.nameTags.remove(id);
    });
  }

  @Override
  public void setEntityGlowingForPlayer(final Entity entity, final ChatColor color) {
    this.apply(player -> {
      final String watcher = player.getName();
      final Scoreboard scoreboard = player.getScoreboard();
      final UUID key = entity.getUniqueId();
      final UUID value = UUID.randomUUID();
      final String raw = value.toString();
      this.glowEntities.put(key, value);

      Team team = scoreboard.getTeam(raw);
      if (team == null) {
        team = scoreboard.registerNewTeam(raw);
      }

      String id = key.toString();
      if (entity instanceof final Player glow) {
        id = glow.getName();
      }

      team.addEntry(id);
      team.addEntry(watcher);
      team.setColor(color);
      PacketToolsProvider.PACKET_API.setEntityGlowing(entity, player, true);
    });
  }

  @Override
  public void removeEntityGlowingForPlayer(final Entity entity) {
    this.apply(player -> {
      final UUID key = entity.getUniqueId();
      final UUID value = this.glowEntities.remove(key);
      if (value == null) {
        return;
      }

      final Scoreboard scoreboard = player.getScoreboard();
      final String id = value.toString();
      final Team temp = scoreboard.getTeam(id);
      if (temp != null) {
        temp.unregister();
      }

      PacketToolsProvider.PACKET_API.setEntityGlowing(entity, player, false);
    });
  }

  @Override
  public Player getInternalPlayer() {
    return requireNonNull(Bukkit.getPlayer(this.uuid));
  }

  @Override
  public void sendMessage(final Component component) {
    this.audience.sendMessage(component);
  }

  @Override
  public void showTitle(final Component title, final Component subtitle) {
    this.audience.showTitle(title(title, subtitle));
  }

  @Override
  public void showBossBar(
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay) {
    final BossBar bar = bossBar(name, progress, color, overlay);
    this.bars.add(bar);
    this.audience.showBossBar(bar);
  }

  @Override
  public void stopSound(final Key key) {
    this.audience.stopSound(named(key));
  }

  @Override
  public void playSound(
      final Key key, final Source category, final float volume, final float pitch) {
    this.audience.playSound(sound(key, category, volume, pitch));
  }

  @Override
  public void addDeathTask(final PlayerDeathTask task) {
    this.tasks.add(task);
  }

  @Override
  public void removeDeathTask(final PlayerDeathTask task) {
    this.tasks.remove(task);
  }

  @Override
  public Collection<PlayerDeathTask> getDeathTasks() {
    return this.tasks;
  }

  @Override
  public UUID getUuid() {
    return this.uuid;
  }

  @Override
  public boolean isAlive() {
    return this.alive;
  }

  @Override
  public void setAlive(final boolean alive) {
    this.alive = alive;
  }

  @Override
  public Game getGame() {
    return this.game;
  }

  @Override
  @Nullable
  public ArmorStand getCorpse() {
    return this.corpse;
  }

  @Override
  public void setCorpse(final @Nullable ArmorStand corpse) {
    this.corpse = corpse;
  }

  @Override
  public void removeAllBossBars() {
    for (final BossBar bar : this.bars) {
      this.audience.hideBossBar(bar);
    }
  }
}
