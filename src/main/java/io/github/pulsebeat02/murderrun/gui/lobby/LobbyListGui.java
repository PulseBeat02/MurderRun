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
package io.github.pulsebeat02.murderrun.gui.lobby;

import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ComponentUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class LobbyListGui extends ChestGui {

  private final MurderRun plugin;
  private final HumanEntity watcher;
  private final Consumer<InventoryClickEvent> consumer;

  private PaginatedPane pages;

  public LobbyListGui(final MurderRun plugin, final HumanEntity watcher, final Consumer<InventoryClickEvent> consumer) {
    super(6, ComponentUtils.serializeComponentToLegacyString(Message.CHOOSE_LOBBY_GUI_TITLE.build()), plugin);
    this.plugin = plugin;
    this.watcher = watcher;
    this.consumer = consumer;
  }

  @Override
  public void update() {
    super.update();
    this.addPane(this.updatePane());
    this.addPane(this.createBackgroundPane());
    this.addPane(this.createNavigationPane());
    this.setOnGlobalClick(event -> event.setCancelled(true));
  }

  private PaginatedPane updatePane() {
    if (this.pages != null) {
      this.pages.clear();
    }

    this.pages = new PaginatedPane(0, 0, 9, 3);
    this.pages.populateWithItemStacks(this.getLobbies(), this.plugin);
    this.pages.setOnClick(this.consumer);

    return this.pages;
  }

  private OutlinePane createBackgroundPane() {
    final GuiItem border = new GuiItem(Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(), this.plugin);

    final OutlinePane background = new OutlinePane(0, 5, 9, 1);
    background.addItem(border);
    background.setRepeat(true);
    background.setPriority(Pane.Priority.LOWEST);

    return background;
  }

  private StaticPane createNavigationPane() {
    final StaticPane navigation = new StaticPane(0, 5, 9, 1);
    navigation.addItem(this.createBackStack(), 0, 0);
    navigation.addItem(this.createForwardStack(), 8, 0);
    navigation.addItem(this.createCloseStack(), 4, 0);
    return navigation;
  }

  private List<ItemStack> getLobbies() {
    final LobbyManager manager = this.plugin.getLobbyManager();
    final Map<String, Lobby> lobbies = manager.getLobbies();
    final List<ItemStack> items = new ArrayList<>();
    for (final Entry<String, Lobby> entry : lobbies.entrySet()) {
      final String name = entry.getKey();
      final Lobby lobby = entry.getValue();
      final Location spawn = lobby.getLobbySpawn();
      final ItemStack item = this.constructLobbyItem(name, spawn);
      items.add(item);
    }
    return items;
  }

  private ItemStack constructLobbyItem(final String name, final Location spawn) {
    final Component title = Message.CHOOSE_LOBBY_GUI_LOBBY_DISPLAY.build(name);
    final Component lore = ComponentUtils.createLocationComponent(Message.CHOOSE_LOBBY_GUI_LOBBY_LORE, spawn);
    return Item.builder(Material.WHITE_BANNER).name(title).lore(lore).pdc(Keys.LOBBY_NAME, PersistentDataType.STRING, name).build();
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
      Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
      event -> this.watcher.closeInventory(),
      this.plugin
    );
  }

  private GuiItem createForwardStack() {
    return new GuiItem(
      Item.builder(Material.GREEN_WOOL).name(Message.SHOP_GUI_FORWARD.build()).build(),
      this::handleForwardPage,
      this.plugin
    );
  }

  private void handleForwardPage(final InventoryClickEvent event) {
    final int current = this.pages.getPage();
    final int max = this.pages.getPages() - 1;
    if (current < max) {
      this.pages.setPage(current + 1);
      this.update();
    }
  }

  private GuiItem createBackStack() {
    return new GuiItem(Item.builder(Material.RED_WOOL).name(Message.SHOP_GUI_BACK.build()).build(), this::handleBackPage, this.plugin);
  }

  private void handleBackPage(final InventoryClickEvent event) {
    final int current = this.pages.getPage();
    if (current > 0) {
      this.pages.setPage(current - 1);
      this.update();
    }
  }
}
