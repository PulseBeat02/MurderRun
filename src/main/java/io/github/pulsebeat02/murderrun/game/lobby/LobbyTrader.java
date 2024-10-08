package io.github.pulsebeat02.murderrun.game.lobby;

import static java.util.Objects.requireNonNull;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

public final class LobbyTrader {

  private final Location location;
  private final List<MerchantRecipe> trades;

  public LobbyTrader(final Location location, final List<MerchantRecipe> trades) {
    this.location = location;
    this.trades = trades;
  }

  public Location getLocation() {
    return this.location;
  }

  public List<MerchantRecipe> getTrades() {
    return this.trades;
  }

  public void spawnVillager() {
    final World world = requireNonNull(this.location.getWorld());
    final Entity entity = world.spawnEntity(this.location, EntityType.VILLAGER);
    if (entity instanceof final Villager villager) {
      villager.setAI(false);
      villager.setInvulnerable(true);
      villager.setGravity(false);
      villager.setProfession(Villager.Profession.NONE);
      villager.setAdult();
      villager.setCanPickupItems(false);
      villager.setVillagerLevel(5);
      villager.setRecipes(this.trades);
    }
  }
}
