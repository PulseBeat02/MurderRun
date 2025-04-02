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

  public Optional<Item.Builder> getCurrency() {
    return this.getNexoItem(GameProperties.NEXO_CURRENCY);
  }

  public Optional<Item.Builder> getGhostBone() {
    return this.getNexoItem(GameProperties.NEXO_GHOST_BONE);
  }

  public Optional<Item.Builder> getKillerArrow() {
    return this.getNexoItem(GameProperties.NEXO_KILLER_ARROW);
  }

  public Optional<Item.Builder> getKillerSword() {
    return this.getNexoItem(GameProperties.NEXO_KILLER_SWORD);
  }

  public Optional<Item.Builder> getKillerHelmet() {
    return this.getNexoItem(GameProperties.NEXO_KILLER_HELMET);
  }

  public Optional<Item.Builder> getKillerChestplate() {
    return this.getNexoItem(GameProperties.NEXO_KILLER_CHESTPLATE);
  }

  public Optional<Item.Builder> getKillerLeggings() {
    return this.getNexoItem(GameProperties.NEXO_KILLER_LEGGINGS);
  }

  public Optional<Item.Builder> getKillerBoots() {
    return this.getNexoItem(GameProperties.NEXO_KILLER_BOOTS);
  }

  public Optional<Item.Builder> getSurvivorHelmet() {
    return this.getNexoItem(GameProperties.NEXO_SURVIVOR_HELMET);
  }

  public Optional<Item.Builder> getSurvivorChestplate() {
    return this.getNexoItem(GameProperties.NEXO_SURVIVOR_CHESTPLATE);
  }

  public Optional<Item.Builder> getSurvivorLeggings() {
    return this.getNexoItem(GameProperties.NEXO_SURVIVOR_LEGGINGS);
  }

  public Optional<Item.Builder> getSurvivorBoots() {
    return this.getNexoItem(GameProperties.NEXO_SURVIVOR_BOOTS);
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
