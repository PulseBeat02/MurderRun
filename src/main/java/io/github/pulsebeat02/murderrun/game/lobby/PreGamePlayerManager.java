package io.github.pulsebeat02.murderrun.game.lobby;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.Iterables;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.PlayerResourcePackChecker;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ResourcePackProvider;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PreGamePlayerManager {

  private final PreGameManager manager;
  private final Collection<Player> murderers;
  private final Collection<Player> participants;
  private final CommandSender leader;
  private final int min;
  private final int max;
  private final boolean quickJoinable;

  private @Nullable LobbyTimeManager lobbyTimeManager;
  private LobbyScoreboard scoreboard;
  private LobbyBossbar bossbar;

  public PreGamePlayerManager(
      final PreGameManager manager,
      final CommandSender leader,
      final int min,
      final int max,
      final boolean quickJoinable) {
    this.manager = manager;
    this.murderers = Collections.synchronizedSet(new HashSet<>());
    this.participants = Collections.synchronizedSet(new HashSet<>());
    this.leader = leader;
    this.min = min;
    this.max = max;
    this.quickJoinable = quickJoinable;
  }

  public boolean isLeader(final CommandSender sender) {
    return this.leader == sender;
  }

  public void initialize() {
    this.bossbar = new LobbyBossbar(this);
    this.scoreboard = new LobbyScoreboard(this.manager);
    this.scoreboard.updateScoreboard();
  }

  public void forceShutdown() {
    this.shutdown();
    for (final Player player : this.participants) {
      this.clearInventory(player);
      this.removePersistentData(player);
      player.setLevel(0);
    }
  }

  public void shutdown() {
    if (this.lobbyTimeManager != null) {
      this.lobbyTimeManager.shutdown();
    }
    this.scoreboard.shutdown();
    this.bossbar.shutdown();
  }

  public void setPlayerToMurderer(final Player murderer) {
    this.removeParticipantFromLobby(murderer);
    this.addParticipantToLobby(murderer, true);
  }

  private void giveSpecialItems(final Player player) {
    final ItemStack sword = ItemFactory.createKillerSword();
    final ItemStack arrow = ItemFactory.createKillerArrow();
    final ItemStack[] gear = ItemFactory.createKillerGear();
    final PlayerInventory inventory = player.getInventory();
    final PersistentDataContainer container = player.getPersistentDataContainer();
    inventory.addItem(sword, arrow);
    inventory.setArmorContents(gear);
    container.set(Keys.KILLER_ROLE, PersistentDataType.BOOLEAN, true);
  }

  public void setPlayerToInnocent(final Player innocent) {
    this.removeParticipantFromLobby(innocent);
    this.addParticipantToLobby(innocent, false);
  }

  public void removeParticipantFromLobby(final Player player) {
    this.murderers.remove(player);
    this.participants.remove(player);
    this.scoreboard.generatePlayerComponent();
    this.clearInventory(player);
    this.removePersistentData(player);
    this.checkIfEnoughPlayers();
  }

  private void removePersistentData(final Player player) {
    final PersistentDataContainer container = player.getPersistentDataContainer();
    container.remove(Keys.KILLER_ROLE);
  }

  public void loadResourcePack(final Player player) {
    final MurderRun plugin = this.manager.getPlugin();
    final PlayerResourcePackChecker checker = plugin.getPlayerResourcePackChecker();
    if (!checker.isLoaded(player)) {
      this.setResourcePack(player);
      checker.markLoaded(player);
    }
  }

  public void addParticipantToLobby(final Player player, final boolean killer) {
    this.participants.add(player);
    this.bossbar.addPlayer(player);
    this.scoreboard.addPlayer(player);
    this.scoreboard.updateScoreboard();
    this.teleportPlayerToLobby(player);
    this.clearInventory(player);
    this.loadResourcePack(player);
    this.giveItems(player, killer);
    this.checkIfEnoughPlayers();
    if (this.lobbyTimeManager != null) {
      this.lobbyTimeManager.resetTime();
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
    }
    this.addCurrency(player, killer);
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
    final int count = killer
        ? GameProperties.KILLER_STARTING_CURRENCY
        : GameProperties.SURVIVOR_STARTING_CURRENCY;
    final PlayerInventory inventory = player.getInventory();
    final ItemStack stack = ItemFactory.createCurrency(1);
    for (int i = 0; i < count; i++) {
      inventory.addItem(stack);
    }
  }

  private void setResourcePack(final Player player) {
    final MurderRun plugin = this.manager.getPlugin();
    final ResourcePackProvider daemon = plugin.getProvider();
    final CompletableFuture<ResourcePackRequest> requestFuture = daemon.getResourcePackRequest();
    requestFuture.thenAccept(request -> AdventureUtils.sendPacksLegacy(player, request));
  }

  public boolean hasPlayer(final CommandSender player) {
    if (player instanceof final Player p) {
      return this.participants.contains(p);
    }
    return this.isLeader(player);
  }

  public void assignKiller() {
    if (this.murderers.isEmpty()) {
      final int size = this.participants.size();
      final int index = RandomUtils.generateInt(size);
      final Player random = Iterables.get(this.participants, index);
      final Component msg = Message.KILLER_ASSIGN.build();
      final String raw = AdventureUtils.serializeComponentToLegacyString(msg);
      this.setPlayerToMurderer(random);
      random.sendMessage(raw);
    }
  }

  public boolean isEnoughPlayers() {
    final int current = this.getCurrentPlayerCount();
    return current >= 2;
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
}