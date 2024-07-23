package io.github.pulsebeat02.murderrun.lobby;

import org.bukkit.Location;
import org.bukkit.inventory.MerchantRecipe;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public final class GameLobby {

  private final Location lobbySpawn;
  private final Collection<VillagerLobbyTrader> traders;

  public GameLobby(final Location lobbySpawn) {
    this.lobbySpawn = lobbySpawn;
    this.traders = new HashSet<>();
  }

  public void addVillagerTrader(final Location location, final List<MerchantRecipe> recipes) {
    final VillagerLobbyTrader trader = new VillagerLobbyTrader(location, recipes);
    this.traders.add(trader);
  }

  public Collection<VillagerLobbyTrader> getTraders() {
    return this.traders;
  }

  public Location getLobbySpawn() {
    return this.lobbySpawn;
  }
}
