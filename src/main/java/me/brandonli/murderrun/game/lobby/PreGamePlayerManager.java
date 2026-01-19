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
package me.brandonli.murderrun.game.lobby;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.Iterables;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.*;
import me.brandonli.murderrun.game.lobby.player.PlayerSelectionManager;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.provider.ResourcePackProvider;
import me.brandonli.murderrun.utils.ComponentUtils;
import me.brandonli.murderrun.utils.RandomUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PreGamePlayerManager {

  private final PreGameManager manager;
  private final Collection<Player> survivors;
  private final Collection<Player> murderers;
  private final Collection<Player> participants;
  private final CommandSender leader;
  private final int min;
  private final int max;
  private final boolean quickJoinable;

  private @Nullable LobbyTimeManager lobbyTimeManager;
  private @Nullable PreGameExpirationTimer expirationTimer;
  private PlayerSelectionManager selectionManager;

  private LobbyScoreboard scoreboard;
  private LobbyBossbar bossbar;
  private boolean locked;

  public PreGamePlayerManager(
      final PreGameManager manager,
      final CommandSender leader,
      final int min,
      final int max,
      final boolean quickJoinable) {
    this.manager = manager;
    this.survivors = ConcurrentHashMap.newKeySet();
    this.murderers = ConcurrentHashMap.newKeySet();
    this.participants = ConcurrentHashMap.newKeySet();
    this.leader = leader;
    this.min = min;
    this.max = max;
    this.quickJoinable = quickJoinable;
    this.locked = false;
  }

  public boolean isLeader(final CommandSender sender) {
    return this.leader == sender;
  }

  public void initialize() {
    final MurderRun plugin = this.manager.getPlugin();
    final PreGameManager preGameManager = this.getManager();
    final GameProperties properties = preGameManager.getProperties();
    this.selectionManager = new PlayerSelectionManager(plugin, properties);
    this.bossbar = new LobbyBossbar(this);
    this.scoreboard = new LobbyScoreboard(this.manager);
    this.scoreboard.updateScoreboard();
    this.checkGameShutdownTimer();
  }

  public void forceShutdown() {
    for (final Player player : this.participants) {
      this.clearInventory(player);
      this.removePersistentData(player);
      this.teleportToMainWorld(player);
      player.setLevel(0);
    }
  }

  public void shutdown() {
    if (this.lobbyTimeManager != null) {
      this.lobbyTimeManager.shutdown();
    }
    this.scoreboard.shutdown();
    this.bossbar.shutdown();
    this.locked = false;
  }

  public synchronized void setPlayerToMurderer(final Player murderer) {
    this.removeParticipantFromLobby(murderer);
    this.addParticipantToLobby(murderer, true);
    this.selectionManager.removeSelection(murderer);
  }

  private void giveSpecialItems(final Player player) {
    final MurderRun plugin = this.manager.getPlugin();
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.runTaskLater(
        plugin,
        () -> {
          final PreGameManager gameManager = this.getManager();
          final GameProperties properties = gameManager.getProperties();
          final ItemStack sword = ItemFactory.createKillerSword(properties);
          final ItemStack arrow = ItemFactory.createKillerArrow(properties);
          final ItemStack[] gear = ItemFactory.createKillerGear(properties);
          final PlayerInventory inventory = player.getInventory();
          final PersistentDataContainer container = player.getPersistentDataContainer();
          inventory.setArmorContents(gear);
          inventory.setItem(1, sword);
          inventory.setItem(2, arrow);
          this.addCurrency(player, true);
          container.set(Keys.KILLER_ROLE, PersistentDataType.BOOLEAN, true);
        },
        10L);
  }

  public synchronized void setPlayerToInnocent(final Player innocent) {
    this.removeParticipantFromLobby(innocent);
    this.addParticipantToLobby(innocent, false);
    this.selectionManager.removeSelection(innocent);
  }

  public synchronized void removeParticipantFromLobby(final Player player) {
    this.survivors.remove(player);
    this.murderers.remove(player);
    this.participants.remove(player);
    this.scoreboard.updateScoreboard();
    this.clearInventory(player);
    this.removePersistentData(player);
    this.teleportToMainWorld(player);
    this.checkIfEnoughPlayers();
    this.checkGameShutdownTimer();
  }

  public synchronized void removeParticipantFromGameInternal(final Player player) {
    this.survivors.remove(player);
    this.murderers.remove(player);
    this.participants.remove(player);
    this.clearInventory(player);
    this.removePersistentData(player);
    this.teleportToMainWorld(player);
  }

  private void teleportToMainWorld(final Player player) {
    final List<World> worlds = Bukkit.getWorlds();
    final World world = worlds.getFirst();
    final Location spawn = world.getSpawnLocation();
    player.teleport(spawn);
  }

  private void checkGameShutdownTimer() {
    final int count = this.participants.size();
    if (count != 0) {
      return;
    }
    final MurderRun plugin = this.manager.getPlugin();
    this.expirationTimer = new PreGameExpirationTimer(this);
    this.expirationTimer.runTaskTimer(plugin, 1L, 20L);
  }

  private void removePersistentData(final Player player) {
    final PersistentDataContainer container = player.getPersistentDataContainer();
    container.remove(Keys.KILLER_ROLE);
  }

  public void loadResourcePack(final Player player) {
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    final MurderRun plugin = this.manager.getPlugin();
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final Audience audience = audiences.player(player);
    scheduler.runTaskLater(
        plugin,
        () -> {
          final PlayerResourcePackChecker checker = plugin.getPlayerResourcePackChecker();
          if (!checker.isLoaded(player)) {
            this.setResourcePack(player);
          }
          audience.sendMessage(Message.RESOURCE_PACK_ACTIVATE.build());
        },
        2 * 20L);
  }

  public void addParticipantToLobby(final Player player, final boolean killer) {
    final MurderRun plugin = this.manager.getPlugin();
    final PlayerPrepareRunnable task = new PlayerPrepareRunnable(player, killer);
    task.runTaskTimer(plugin, 0L, 1L);
  }

  private class PlayerPrepareRunnable extends BukkitRunnable {

    private final Player player;
    private final boolean killer;

    public PlayerPrepareRunnable(final Player player, final boolean killer) {
      this.player = player;
      this.killer = killer;
    }

    @Override
    public void run() {
      if (!this.waitUntilReady()) {
        return;
      }
      PreGamePlayerManager.this.participants.add(this.player);
      PreGamePlayerManager.this.bossbar.addPlayer(this.player);
      PreGamePlayerManager.this.scoreboard.addPlayer(this.player);
      PreGamePlayerManager.this.scoreboard.updateScoreboard();
      PreGamePlayerManager.this.clearInventory(this.player);
      PreGamePlayerManager.this.teleportPlayerToLobby(this.player);
      PreGamePlayerManager.this.giveItems(this.player, this.killer);
      PreGamePlayerManager.this.loadResourcePack(this.player);
      PreGamePlayerManager.this.checkIfEnoughPlayers();
      if (PreGamePlayerManager.this.lobbyTimeManager != null) {
        PreGamePlayerManager.this.lobbyTimeManager.resetTime();
      }
      if (PreGamePlayerManager.this.expirationTimer != null) {
        PreGamePlayerManager.this.expirationTimer.cancel();
      }
      this.cancel();
    }

    private boolean waitUntilReady() {
      final GameSettings settings = PreGamePlayerManager.this.manager.getSettings();
      final Lobby lobby = requireNonNull(settings.getLobby());
      final Location spawn = lobby.getLobbySpawn();
      final Block block = spawn.getBlock();
      final int x = block.getX();
      final int z = block.getZ();
      final World world = block.getWorld();
      final Block under = world.getHighestBlockAt(x, z);
      return under.isSolid(); // any block will do as long as it's solid, showing it loaded
    }
  }

  private void checkIfEnoughPlayers() {
    final int count = this.participants.size();
    if (count < this.min) {
      this.cancelTimer();
      return;
    }

    if (this.lobbyTimeManager != null) {
      return;
    }

    this.lobbyTimeManager = new LobbyTimeManager(this.manager);
    this.lobbyTimeManager.startTimer();
  }

  private void cancelTimer() {
    if (this.lobbyTimeManager != null) {
      this.lobbyTimeManager.cancelTimer();
      this.lobbyTimeManager = null;
    }
  }

  private void giveItems(final Player player, final boolean killer) {
    if (killer) {
      this.murderers.add(player);
      this.giveSpecialItems(player);
    } else {
      this.survivors.add(player);
      this.addCurrency(player, false);
    }
    this.giveEmptyAbility(player);
    this.giveLeaveItem(player);
  }

  private void giveLeaveItem(final Player player) {
    final Item.Builder builder = ItemFactory.createLeaveItem();
    final ItemStack stack = builder.build();
    final PlayerInventory inventory = player.getInventory();
    inventory.setItem(1, stack);
  }

  private void giveEmptyAbility(final Player player) {
    final Item.Builder builder = ItemFactory.createEmptyAbility();
    final ItemStack stack = builder.build();
    final PlayerInventory inventory = player.getInventory();
    inventory.setItem(0, stack);
  }

  private void clearInventory(final Player player) {
    final PlayerInventory inventory = player.getInventory();
    inventory.clear();
  }

  private void teleportPlayerToLobby(final Player player) {
    final GameSettings settings = this.manager.getSettings();
    final Lobby lobby = requireNonNull(settings.getLobby());
    final Location spawn = lobby.getLobbySpawn();
    player.teleport(spawn);
  }

  private void addCurrency(final Player player, final boolean killer) {
    final PreGameManager gameManager = this.getManager();
    final GameProperties properties = gameManager.getProperties();
    final int count =
        killer ? properties.getKillerStartingCurrency() : properties.getSurvivorStartingCurrency();
    final PlayerInventory inventory = player.getInventory();
    final ItemStack stack = ItemFactory.createCurrency(properties, 1);
    for (int i = 0; i < count; i++) {
      inventory.addItem(stack);
    }
  }

  private void setResourcePack(final Player player) {
    final MurderRun plugin = this.manager.getPlugin();
    final ResourcePackProvider daemon = plugin.getProvider();
    final ResourcePackRequest request = daemon.getResourcePackRequest();
    ComponentUtils.sendPacksLegacy(player, request);
  }

  public boolean hasPlayer(final CommandSender player) {
    if (player instanceof final Player p) {
      return this.participants.contains(p);
    }
    return this.isLeader(player);
  }

  public synchronized void assignRoles() {
    final GameMode mode = this.manager.getMode();
    if (mode == GameMode.DEFAULT || mode == GameMode.FREEZE_TAG) {
      if (this.murderers.isEmpty()) {
        final int size = this.participants.size();
        if (size == 0) {
          return;
        }
        final int index = RandomUtils.generateInt(size);
        final Player random = Iterables.get(this.participants, index);
        final Component msg = Message.KILLER_ASSIGN.build();
        final String raw = ComponentUtils.serializeComponentToLegacyString(msg);
        this.setPlayerToMurderer(random);
        random.sendMessage(raw);
      }
    } else if (mode == GameMode.ONE_BOUNCE) {
      if (this.murderers.isEmpty()) { // assign roles by default
        final int size = this.participants.size();
        if (size == 0) {
          return;
        }
        final int index = RandomUtils.generateInt(size);
        final Player random = Iterables.get(this.participants, index);
        final Component msg = Message.SURVIVOR_ASSIGN.build();
        final String raw = ComponentUtils.serializeComponentToLegacyString(msg);
        random.sendMessage(raw);
        final List<Player> snapshot = new ArrayList<>(this.participants);
        for (final Player player : snapshot) {
          if (player == random) {
            continue;
          }
          this.setPlayerToMurderer(player);
        }
      }
    }
  }

  public boolean isEnoughPlayers() {
    final int current = this.getCurrentPlayerCount();
    return current >= 2;
  }

  public Collection<Player> getSurvivors() {
    return this.survivors;
  }

  public Collection<Player> getMurderers() {
    return this.murderers;
  }

  public Collection<Player> getParticipants() {
    return this.participants;
  }

  public int getMinimumPlayerCount() {
    return this.min;
  }

  public int getMaximumPlayerCount() {
    return this.max;
  }

  public int getCurrentPlayerCount() {
    return this.participants.size();
  }

  public boolean isQuickJoinable() {
    return this.quickJoinable;
  }

  public boolean isGameFull() {
    final int current = this.getCurrentPlayerCount();
    return current == this.max;
  }

  public LobbyScoreboard getScoreboard() {
    return this.scoreboard;
  }

  public @Nullable LobbyTimeManager getLobbyTimeManager() {
    return this.lobbyTimeManager;
  }

  public CommandSender getLeader() {
    return this.leader;
  }

  public PreGameManager getManager() {
    return this.manager;
  }

  public boolean isLocked() {
    return this.locked;
  }

  public PlayerSelectionManager getSelectionManager() {
    return this.selectionManager;
  }
}
