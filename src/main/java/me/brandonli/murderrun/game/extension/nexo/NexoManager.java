/*

MIT License

Copyright (c) 2025 Brandon Li

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
