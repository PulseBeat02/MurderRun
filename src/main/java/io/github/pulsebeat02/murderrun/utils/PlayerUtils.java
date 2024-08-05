package io.github.pulsebeat02.murderrun.utils;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.reflect.NMSHandler;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public final class PlayerUtils {

  private static final Map<Player, Team> GLOW_TEAMS = new WeakHashMap<>();
  private static final Map<Player, Team> HIDE_NAME_TAG_TEAMS = new WeakHashMap<>();
  private static final Map<UUID, WorldBorder> WORLD_BORDERS = new WeakHashMap<>();

  private PlayerUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static void removeAllBossBars(final Player player) {
    final Server server = Bukkit.getServer();
    final Iterator<KeyedBossBar> bars = server.getBossBars();
    while (bars.hasNext()) {
      final KeyedBossBar bar = bars.next();
      final List<Player> players = bar.getPlayers();
      if (!players.contains(player)) {
        continue;
      }
      bar.removePlayer(player);
    }
  }

  public static Optional<GamePlayer> checkIfValidEventPlayer(
      final MurderGame game, final Player player) {
    final MurderPlayerManager manager = game.getPlayerManager();
    return manager.lookupPlayer(player);
  }

  public static void removeAllPotionEffects(final Player player) {
    player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
  }

  public static void setGlowColor(
      final GamePlayer gamePlayer, final ChatColor color, final Collection<GamePlayer> receivers) {

    final Player player = gamePlayer.getPlayer();
    final ScoreboardManager manager = Bukkit.getScoreboardManager();
    if (manager == null) {
      throw new AssertionError("Failed to access the main scoreboard!");
    }

    final Scoreboard scoreboard = manager.getMainScoreboard();
    final UUID glowID = UUID.randomUUID();
    final String name = String.format("color-%s", glowID);
    final Team team = scoreboard.registerNewTeam(name);
    team.setColor(color);
    team.addEntry(player.getDisplayName());
    GLOW_TEAMS.put(player, team);

    receivers.forEach(
        receiver -> NMSHandler.NMS_UTILS.sendGlowPacket(player, receiver.getPlayer()));
  }

  public static void removeGlow(
      final GamePlayer gamePlayer, final Collection<GamePlayer> receivers) {

    final Player player = gamePlayer.getPlayer();
    player.removePotionEffect(PotionEffectType.GLOWING);

    final Team team = GLOW_TEAMS.get(player);
    if (team == null) {
      return;
    }
    team.unregister();

    GLOW_TEAMS.remove(player);

    receivers.forEach(
        receiver -> NMSHandler.NMS_UTILS.sendRemoveGlowPacket(player, receiver.getPlayer()));
  }

  public static void addFakeWorldBorderEffect(final GamePlayer gamePlayer) {
    final Player player = gamePlayer.getPlayer();
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
  }

  public static void removeFakeWorldBorderEffect(final GamePlayer gamePlayer) {
    final Player player = gamePlayer.getPlayer();
    player.setWorldBorder(null);
  }

  public static void hideNameTag(final GamePlayer gamePlayer) {
    final Player player = gamePlayer.getPlayer();
    final ScoreboardManager manager = Bukkit.getScoreboardManager();
    if (manager == null) {
      throw new AssertionError("Failed to access the main scoreboard!");
    }
    final Scoreboard scoreboard = manager.getMainScoreboard();
    final UUID hideID = UUID.randomUUID();
    final String name = String.format("hide-name-tag-%s", hideID);
    final Team team = scoreboard.registerNewTeam(name);
    team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
    team.addEntry(player.getDisplayName());
    HIDE_NAME_TAG_TEAMS.put(player, team);
    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
  }

  public static void showNameTag(final GamePlayer gamePlayer) {
    final Player player = gamePlayer.getPlayer();
    final Team team = HIDE_NAME_TAG_TEAMS.get(player);
    if (team == null) {
      return;
    }
    team.unregister();
    HIDE_NAME_TAG_TEAMS.remove(player);
  }

  public static boolean canEntitySeePlayer(
      final Entity entity, final GamePlayer player, final double maxRangeSquared) {
    final Location entityLocation = entity.getLocation();
    final Location playerLocation = player.getLocation();
    final double distanceSquared = entityLocation.distanceSquared(playerLocation);
    if (distanceSquared > maxRangeSquared) {
      return false;
    }

    final World world = entity.getWorld();
    final Vector direction =
        playerLocation.toVector().subtract(entityLocation.toVector()).normalize();
    final RayTraceResult result = world.rayTraceBlocks(entityLocation, direction, maxRangeSquared);
    final Block block = result == null ? null : result.getHitBlock();
    return block == null || !block.getType().isSolid();
  }
}
