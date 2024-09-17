package io.github.pulsebeat02.murderrun.game;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.Iterables;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.event.PreGameEvents;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyTimeManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ResourcePackProvider;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class GameManager {

  private final MurderRun plugin;
  private final Game game;
  private final Collection<Player> murderers;
  private final Collection<Player> participants;
  private final GameSettings settings;
  private final GameEndCallback callback;
  private final Consumer<GameManager> onGameStart;

  private LobbyTimeManager lobbyTimeManager;
  private PreGameEvents events;

  public GameManager(
      final MurderRun plugin,
      final GameEndCallback callback,
      final Consumer<GameManager> onGameStart) {
    this.plugin = plugin;
    this.callback = callback;
    this.onGameStart = onGameStart;
    this.game = new Game(plugin);
    this.murderers = new HashSet<>();
    this.participants = new HashSet<>();
    this.settings = new GameSettings();
  }

  public void initialize() {
    this.lobbyTimeManager = new LobbyTimeManager(this);
    this.events = new PreGameEvents(this);
    this.events.registerEvents();
    this.lobbyTimeManager.startTimer();
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
    inventory.addItem(sword, arrow);
    inventory.setArmorContents(gear);
  }

  public void setPlayerToInnocent(final Player innocent) {
    this.removeParticipantFromLobby(innocent);
    this.addParticipantToLobby(innocent, false);
  }

  public void removeParticipantFromLobby(final Player player) {
    this.murderers.remove(player);
    this.participants.remove(player);
    this.clearInventory(player);
  }

  public void loadResourcePack(final Player player) {
    final PlayerResourcePackChecker checker = this.plugin.getPlayerResourcePackChecker();
    if (!checker.isLoaded(player)) {
      this.setResourcePack(player);
      checker.markLoaded(player);
    }
  }

  public void addParticipantToLobby(final Player player, final boolean killer) {
    this.participants.add(player);
    this.lobbyTimeManager.resetTime();
    this.teleportPlayerToLobby(player);
    this.clearInventory(player);
    this.loadResourcePack(player);
    this.giveItems(player, killer);
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
    final Lobby lobby = requireNonNull(this.settings.getLobby());
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
    final ResourcePackProvider daemon = this.plugin.getProvider();
    final CompletableFuture<ResourcePackRequest> requestFuture = daemon.getResourcePackRequest();
    requestFuture.thenAccept(request -> AdventureUtils.sendPacksLegacy(player, request));
  }

  private void assignKiller() {
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

  public Game startGame() {
    this.assignKiller();
    this.onGameStart.accept(this);
    this.game.startGame(this.settings, this.murderers, this.participants, this.callback);
    this.shutdown();
    return this.game;
  }

  public void shutdown() {
    this.events.unregisterEvents();
    this.lobbyTimeManager.shutdown();
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public Game getGame() {
    return this.game;
  }

  public GameSettings getSettings() {
    return this.settings;
  }

  public Collection<Player> getMurderers() {
    return this.murderers;
  }

  public Collection<Player> getParticipants() {
    return this.participants;
  }
}
