package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.bossbar.BossBar.bossBar;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.title.Title.title;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract sealed class GamePlayer permits Survivor, Killer {

  private static final String GLOW_TEAM_NAME = "glow-color-%s";
  private static final String HIDE_NAME_TAG_TEAM_NAME = "hide-name-tag-%s";

  private static final Map<Player, Team> GLOW_TEAMS = new WeakHashMap<>();
  private static final Map<Player, Team> HIDE_NAME_TAG_TEAMS = new WeakHashMap<>();
  private static final Map<UUID, WorldBorder> WORLD_BORDERS = new WeakHashMap<>();

  private final Game game;
  private final UUID uuid;
  private final Audience audience;
  private final Collection<PlayerDeathTask> tasks;
  private boolean alive;

  public GamePlayer(final Game game, final UUID uuid) {
    this.game = game;
    this.uuid = uuid;
    this.audience = this.getAudience(game, uuid);
    this.alive = true;
    this.tasks = new HashSet<>();
  }

  private Audience getAudience(
      @UnderInitialization GamePlayer this, final Game game, final UUID uuid) {
    final MurderRun plugin = game.getPlugin();
    final AudienceProvider handler = plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    return audiences.player(uuid);
  }

  public abstract void onPlayerAttemptPickupPartEvent(final EntityPickupItemEvent event);

  public Player getPlayer() {
    return requireNonNull(Bukkit.getPlayer(this.uuid));
  }

  public void apply(final Consumer<Player> consumer) {
    final Player player = this.getPlayer();
    consumer.accept(player);
  }

  public void spawnParticle(
      final Particle particle,
      final Location location,
      final int count,
      final double offSetX,
      final double offSetY,
      final double offSetZ) {
    this.apply(
        player -> player.spawnParticle(particle, location, count, offSetX, offSetY, offSetZ));
  }

  public void addPotionEffects(final PotionEffect... effects) {
    this.apply(player -> {
      for (final PotionEffect effect : effects) {
        player.addPotionEffect(effect);
      }
    });
  }

  public @NonNull Location getLocation() {
    final Player player = this.getPlayer();
    final Location location = player.getLocation();
    if (location == null) {
      /*
      Curse you Bukkit! Why does OfflinePlayer#getLocation have @Nullable, but
      Player#getLocation has @NotNull? I have to implement this useless
      null-check as a result so the Checker Framework is satisfied. And
      I have to add other methods to wrap the Player object.
       */
      throw new AssertionError("Curse you Bukkit!");
    }
    return location;
  }

  public @Nullable Location getDeathLocation() {
    final Player player = this.getPlayer();
    return player.getLastDeathLocation();
  }

  public PlayerInventory getInventory() {
    final Player player = this.getPlayer();
    return player.getInventory();
  }

  public void sendMessage(final Component component) {
    this.audience.sendMessage(component);
  }

  public void showTitle(final Component title, final Component subtitle) {
    this.audience.showTitle(title(title, subtitle));
  }

  public void showBossBar(
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay) {
    this.audience.showBossBar(bossBar(name, progress, color, overlay));
  }

  public void playSound(
      final Key key, final Source category, final float volume, final float pitch) {
    this.audience.playSound(sound(key, category, volume, pitch));
  }

  public void removeAllPotionEffects() {
    this.apply(player -> {
      final Collection<PotionEffect> effects = player.getActivePotionEffects();
      effects.forEach(effect -> {
        final PotionEffectType type = effect.getType();
        player.removePotionEffect(type);
      });
    });
  }

  public void removeAllBossBars() {
    this.apply(player -> {
      final Server server = Bukkit.getServer();
      final Iterator<KeyedBossBar> bars = server.getBossBars();
      while (bars.hasNext()) {
        final KeyedBossBar bar = bars.next();
        final List<Player> players = bar.getPlayers();
        if (players.contains(player)) {
          bar.removePlayer(player);
        }
      }
    });
  }

  public boolean canSeeEntity(final Entity entity, final double maxRangeSquared) {

    final Location entityLocation = entity.getLocation();
    final Location playerLocation = this.getLocation();
    final double distanceSquared = entityLocation.distanceSquared(playerLocation);
    if (distanceSquared > maxRangeSquared) {
      return false;
    }

    final World world = entity.getWorld();
    final Vector playerVector = playerLocation.toVector();
    final Vector entityVector = entityLocation.toVector();
    final Vector direction = playerVector.subtract(entityVector);
    final Vector normalizedDirection = direction.normalize();
    final RayTraceResult result =
        world.rayTraceBlocks(entityLocation, normalizedDirection, maxRangeSquared);
    if (result == null) {
      return true;
    }

    final Block block = result.getHitBlock();
    if (block == null) {
      return true;
    }

    final Material type = block.getType();
    return !type.isSolid();
  }

  public void setGlowColor(final ChatColor color, final Collection<GamePlayer> receivers) {
    this.apply(player -> {
      final ScoreboardManager manager = requireNonNull(Bukkit.getScoreboardManager());
      final Scoreboard scoreboard = manager.getMainScoreboard();
      final UUID glowID = UUID.randomUUID();
      final String name = GLOW_TEAM_NAME.formatted(glowID);
      final Team team = scoreboard.registerNewTeam(name);
      team.setColor(color);
      team.addEntry(player.getDisplayName());
      GLOW_TEAMS.put(player, team);
      receivers.forEach(receiver -> {
        final Player receiverPlayer = receiver.getPlayer();
        PacketToolsProvider.INSTANCE.sendGlowPacket(player, receiverPlayer);
      });
    });
  }

  public void removeGlow(final Collection<GamePlayer> receivers) {
    this.apply(player -> {
      final PotionEffectType type = PotionEffectType.GLOWING;
      player.removePotionEffect(type);
      final Team team = GLOW_TEAMS.get(player);
      if (team == null) {
        return;
      }
      team.unregister();
      GLOW_TEAMS.remove(player);
      receivers.forEach(receiver -> {
        final Player receiverPlayer = receiver.getPlayer();
        PacketToolsProvider.INSTANCE.sendRemoveGlowPacket(player, receiverPlayer);
      });
    });
  }

  public void addFakeWorldBorderEffect() {
    this.apply(player -> {
      final World world = player.getWorld();
      final UUID id = world.getUID();
      WORLD_BORDERS.computeIfAbsent(id, ignore -> {
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

  public void removeFakeWorldBorderEffect() {
    this.apply(player -> player.setWorldBorder(null));
  }

  public void hideNameTag() {
    this.apply(player -> {
      final ScoreboardManager manager = requireNonNull(Bukkit.getScoreboardManager());
      final Scoreboard scoreboard = manager.getMainScoreboard();
      final UUID hideID = UUID.randomUUID();
      final String name = HIDE_NAME_TAG_TEAM_NAME.formatted(hideID);
      final Team team = scoreboard.registerNewTeam(name);
      team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
      team.addEntry(player.getDisplayName());
      HIDE_NAME_TAG_TEAMS.put(player, team);
      final PotionEffectType type = PotionEffectType.GLOWING;
      final int duration = Integer.MAX_VALUE;
      player.addPotionEffect(new PotionEffect(type, duration, 0));
    });
  }

  public void showNameTag() {
    this.apply(player -> {
      final Team team = HIDE_NAME_TAG_TEAMS.get(player);
      if (team == null) {
        return;
      }
      team.unregister();
      HIDE_NAME_TAG_TEAMS.remove(player);
    });
  }

  public void teleport(final Location location) {
    this.apply(player -> player.teleport(location));
  }

  public void addDeathTask(final PlayerDeathTask task) {
    this.tasks.add(task);
  }

  public void removeDeathTask(final PlayerDeathTask task) {
    this.tasks.remove(task);
  }

  public Collection<PlayerDeathTask> getDeathTasks() {
    return this.tasks;
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public boolean isAlive() {
    return this.alive;
  }

  public void setAlive(final boolean alive) {
    this.alive = alive;
  }

  public Game getGame() {
    return this.game;
  }
}
