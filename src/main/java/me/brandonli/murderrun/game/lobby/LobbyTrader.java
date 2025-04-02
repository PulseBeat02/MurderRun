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
