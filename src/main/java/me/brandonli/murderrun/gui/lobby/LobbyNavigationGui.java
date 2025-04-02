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
package me.brandonli.murderrun.gui.lobby;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.lobby.Lobby;
import me.brandonli.murderrun.game.lobby.LobbyManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.ComponentUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class LobbyNavigationGui extends ChestGui {

  private static final Pattern NAVIGATION_LOBBY_PATTERN = new Pattern("111111111", "111314111", "111111111", "111121111");

  private final MurderRun plugin;
  private final HumanEntity watcher;

  public LobbyNavigationGui(final MurderRun plugin, final HumanEntity clicker) {
    super(4, ComponentUtils.serializeComponentToLegacyString(Message.MANAGE_LOBBY_GUI_TITLE.build()), plugin);
    this.plugin = plugin;
    this.watcher = clicker;
  }

  @Override
  public void update() {
    super.update();
    this.addPane(this.createPane());
    this.setOnGlobalClick(event -> event.setCancelled(true));
  }

  private PatternPane createPane() {
    final PatternPane pane = new PatternPane(0, 0, 9, 4, NAVIGATION_LOBBY_PATTERN);
    pane.bindItem('1', this.createBorderStack());
    pane.bindItem('2', this.createCloseStack());
    pane.bindItem('3', this.createLobbyStack());
    pane.bindItem('4', this.createModifyStack());
    return pane;
  }

  private GuiItem createModifyStack() {
    return new GuiItem(
      Item.builder(Material.YELLOW_BANNER).name(Message.MANAGE_LOBBY_GUI_EDIT.build()).build(),
      this::createListingsMenu,
      this.plugin
    );
  }

  private void createListingsMenu(final InventoryClickEvent event) {
    final ChestGui gui = new LobbyListGui(this.plugin, this.watcher, this::handleLobbyClickEvent);
    gui.update();
    gui.show(this.watcher);
  }

  public void handleLobbyClickEvent(final InventoryClickEvent event) {
    final ItemStack item = event.getCurrentItem();
    if (item == null) {
      return;
    }

    final ItemMeta meta = item.getItemMeta();
    if (meta == null) {
      return;
    }

    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final String name = container.get(Keys.LOBBY_NAME, PersistentDataType.STRING);
    if (name == null) {
      return;
    }

    final LobbyManager manager = this.plugin.getLobbyManager();
    final Lobby lobby = requireNonNull(manager.getLobby(name));
    final Location spawn = lobby.getLobbySpawn();
    final Location[] corners = lobby.getCorners();
    final Location first = corners[0];
    final Location second = corners[1];
    final LobbyModificationGui gui = new LobbyModificationGui(this.plugin, this.watcher, name, first, second, spawn, true);
    gui.registerEvents();
    gui.update();
    gui.show(this.watcher);
  }

  private GuiItem createLobbyStack() {
    return new GuiItem(
      Item.builder(Material.GREEN_BANNER).name(Message.MANAGE_LOBBY_GUI_CREATE.build()).build(),
      this::createLobbyMenu,
      this.plugin
    );
  }

  private void createLobbyMenu(final InventoryClickEvent event) {
    final LobbyModificationGui gui = new LobbyModificationGui(this.plugin, this.watcher, false);
    gui.registerEvents();
    gui.update();
    gui.show(this.watcher);
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
      Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
      event -> this.watcher.closeInventory(),
      this.plugin
    );
  }

  private GuiItem createBorderStack() {
    return new GuiItem(Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(), this.plugin);
  }
}
