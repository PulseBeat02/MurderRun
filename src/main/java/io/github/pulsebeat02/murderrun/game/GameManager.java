package io.github.pulsebeat02.murderrun.game;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ResourcePackProvider;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import java.util.Collection;
import java.util.HashSet;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.resource.ResourcePackRequest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class GameManager {

  private final MurderRun plugin;
  private final Game game;
  private final Collection<Player> murderers;
  private final Collection<Player> participants;
  private final GameSettings settings;

  public GameManager(final MurderRun plugin) {
    this.plugin = plugin;
    this.game = new Game(plugin);
    this.murderers = new HashSet<>();
    this.participants = new HashSet<>();
    this.settings = new GameSettings();
  }

  public void setPlayerToMurderer(final Player murderer) {
    this.murderers.add(murderer);
    this.giveSpecialSword(murderer);
  }

  private void giveSpecialSword(final Player player) {
    final ItemStack stack = ItemUtils.createKillerSword();
    final PlayerInventory inventory = player.getInventory();
    inventory.addItem(stack);
  }

  public void setPlayerToInnocent(final Player innocent) {
    this.removeParticipantFromLobby(innocent);
    this.addParticipantToLobby(innocent);
  }

  public void removeParticipantFromLobby(final Player player) {
    this.murderers.remove(player);
    this.participants.remove(player);
    this.clearInventory(player);
  }

  public void addParticipantToLobby(final Player player) {
    this.participants.add(player);
    this.teleportPlayerToLobby(player);
    this.addCurrency(player);
    this.setResourcePack(player);
  }

  private void clearInventory(final Player player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack[] slots = inventory.getContents();
    for (final ItemStack slot : slots) {
      inventory.remove(slot);
    }
  }

  private void teleportPlayerToLobby(final Player player) {
    final Lobby lobby = requireNonNull(this.settings.getLobby());
    final Location spawn = lobby.getLobbySpawn();
    player.teleport(spawn);
  }

  private void addCurrency(final Player player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack stack = new ItemStack(Material.NETHER_STAR, 64);
    for (int i = 0; i < 4; i++) {
      inventory.addItem(stack);
    }
  }

  private void setResourcePack(final Player player) {
    final ResourcePackProvider daemon = this.plugin.getProvider();
    final ResourcePackRequest request = daemon.getResourcePackRequest();
    final AudienceProvider handler = this.plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    final Audience audience = audiences.player(player);
    audience.sendResourcePacks(request);
  }

  public void startGame() {
    this.setMurdererCount(this.murderers);
    this.game.startGame(this.settings, this.murderers, this.participants);
  }

  private void setMurdererCount(final Collection<Player> murderers) {
    final GameSettings settings = this.getSettings();
    final int count = murderers.size();
    settings.setMurdererCount(count);
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
