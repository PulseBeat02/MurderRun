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
package me.brandonli.murderrun.game.extension.craftengine;

import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.utils.item.Item;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.momirealms.craftengine.bukkit.item.BukkitItemManager;
import net.momirealms.craftengine.core.item.CustomItem;
import net.momirealms.craftengine.core.pack.PackManager;
import net.momirealms.craftengine.core.pack.host.ResourcePackDownloadData;
import net.momirealms.craftengine.core.pack.host.ResourcePackHost;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.inventory.ItemStack;

public final class CraftEngineManager {

  public Collection<ResourcePackInfo> getPackInfo() {
    final CraftEngine engine = CraftEngine.instance();
    final PackManager manager = engine.packManager();
    final ResourcePackHost host = manager.resourcePackHost();
    if (host == null) {
      return List.of(); // must wait for CraftEngine to load first
      // even then, CraftEngine handles pack loading on their own, and we leave them to figure it out
    }

    final ResourcePackHost newHost = manager.resourcePackHost();
    final UUID random = UUID.randomUUID(); // create a fake player since we only use once
    final CompletableFuture<List<ResourcePackDownloadData>> future = newHost.requestResourcePackDownloadLink(random);
    final List<ResourcePackDownloadData> downloadDataList = future.join();
    final Collection<ResourcePackInfo> infos = new ArrayList<>();
    for (final ResourcePackDownloadData downloadData : downloadDataList) {
      final String url = downloadData.url();
      final URI uri = URI.create(url);
      final String hash = downloadData.sha1();
      final UUID uuid = downloadData.uuid();
      final ResourcePackInfo info = ResourcePackInfo.resourcePackInfo(uuid, uri, hash);
      infos.add(info);
    }
    return infos;
  }

  public Optional<Item.Builder> getCurrency(final GameProperties properties) {
    return this.getCraftEngineItem(properties.getCraftEngineCurrency());
  }

  public Optional<Item.Builder> getGhostBone(final GameProperties properties) {
    return this.getCraftEngineItem(properties.getCraftEngineGhostBone());
  }

  public Optional<Item.Builder> getKillerArrow(final GameProperties properties) {
    return this.getCraftEngineItem(properties.getCraftEngineKillerArrow());
  }

  public Optional<Item.Builder> getKillerSword(final GameProperties properties) {
    return this.getCraftEngineItem(properties.getCraftEngineKillerSword());
  }

  public Optional<Item.Builder> getKillerHelmet(final GameProperties properties) {
    return this.getCraftEngineItem(properties.getCraftEngineKillerHelmet());
  }

  public Optional<Item.Builder> getKillerChestplate(final GameProperties properties) {
    return this.getCraftEngineItem(properties.getCraftEngineKillerChestplate());
  }

  public Optional<Item.Builder> getKillerLeggings(final GameProperties properties) {
    return this.getCraftEngineItem(properties.getCraftEngineKillerLeggings());
  }

  public Optional<Item.Builder> getKillerBoots(final GameProperties properties) {
    return this.getCraftEngineItem(properties.getCraftEngineKillerBoots());
  }

  public Optional<Item.Builder> getSurvivorHelmet(final GameProperties properties) {
    return this.getCraftEngineItem(properties.getCraftEngineSurvivorHelmet());
  }

  public Optional<Item.Builder> getSurvivorChestplate(final GameProperties properties) {
    return this.getCraftEngineItem(properties.getCraftEngineSurvivorChestplate());
  }

  public Optional<Item.Builder> getSurvivorLeggings(final GameProperties properties) {
    return this.getCraftEngineItem(properties.getCraftEngineSurvivorLeggings());
  }

  public Optional<Item.Builder> getSurvivorBoots(final GameProperties properties) {
    return this.getCraftEngineItem(properties.getCraftEngineSurvivorBoots());
  }

  private Optional<Item.Builder> getCraftEngineItem(final String property) {
    if (this.hasCraftEngineItem(property)) {
      final String[] split = property.split(":");
      final String namespace = split[0];
      final String path = split[1];
      final BukkitItemManager manager = BukkitItemManager.instance();
      final Key key = Key.of(namespace, path);
      final Optional<CustomItem<ItemStack>> optional = manager.getCustomItem(key);
      if (optional.isEmpty()) {
        return Optional.empty();
      }
      final CustomItem<ItemStack> customItem = optional.get();
      final ItemStack item = customItem.buildItemStack();
      final Item.Builder builder1 = Item.builder(item);
      return Optional.of(builder1);
    }
    return Optional.empty();
  }

  private boolean hasCraftEngineItem(final String id) {
    return !id.equals("none");
  }
}
