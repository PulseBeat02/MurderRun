/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
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
