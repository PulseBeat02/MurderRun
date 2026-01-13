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
package me.brandonli.murderrun.game.extension.nexo;

import static java.util.Objects.requireNonNull;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.pack.server.NexoPackServer;
import java.util.Optional;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.utils.item.Item;
import net.kyori.adventure.resource.ResourcePackInfo;
import org.bukkit.inventory.ItemStack;

public final class NexoManager {

  public ResourcePackInfo getPackInfo() {
    final NexoPlugin plugin = NexoPlugin.instance();
    final NexoPackServer server = plugin.packServer();
    return requireNonNull(server.packInfo());
  }

  public Optional<Item.Builder> getCurrency(final GameProperties properties) {
    return this.getNexoItem(properties.getNexoCurrency());
  }

  public Optional<Item.Builder> getGhostBone(final GameProperties properties) {
    return this.getNexoItem(properties.getNexoGhostBone());
  }

  public Optional<Item.Builder> getKillerArrow(final GameProperties properties) {
    return this.getNexoItem(properties.getNexoKillerArrow());
  }

  public Optional<Item.Builder> getKillerSword(final GameProperties properties) {
    return this.getNexoItem(properties.getNexoKillerSword());
  }

  public Optional<Item.Builder> getKillerHelmet(final GameProperties properties) {
    return this.getNexoItem(properties.getNexoKillerHelmet());
  }

  public Optional<Item.Builder> getKillerChestplate(final GameProperties properties) {
    return this.getNexoItem(properties.getNexoKillerChestplate());
  }

  public Optional<Item.Builder> getKillerLeggings(final GameProperties properties) {
    return this.getNexoItem(properties.getNexoKillerLeggings());
  }

  public Optional<Item.Builder> getKillerBoots(final GameProperties properties) {
    return this.getNexoItem(properties.getNexoKillerBoots());
  }

  public Optional<Item.Builder> getSurvivorHelmet(final GameProperties properties) {
    return this.getNexoItem(properties.getNexoSurvivorHelmet());
  }

  public Optional<Item.Builder> getSurvivorChestplate(final GameProperties properties) {
    return this.getNexoItem(properties.getNexoSurvivorChestplate());
  }

  public Optional<Item.Builder> getSurvivorLeggings(final GameProperties properties) {
    return this.getNexoItem(properties.getNexoSurvivorLeggings());
  }

  public Optional<Item.Builder> getSurvivorBoots(final GameProperties properties) {
    return this.getNexoItem(properties.getNexoSurvivorBoots());
  }

  private Optional<Item.Builder> getNexoItem(final String property) {
    if (this.hasNexoItem(property)) {
      final ItemBuilder builder = NexoItems.itemFromId(property);
      if (builder == null) {
        return Optional.empty();
      }
      final ItemStack item = builder.build();
      final Item.Builder builder1 = Item.builder(item);
      return Optional.of(builder1);
    }
    return Optional.empty();
  }

  private boolean hasNexoItem(final String id) {
    return !id.equals("none");
  }
}
