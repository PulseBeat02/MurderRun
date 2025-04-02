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
package me.brandonli.murderrun.gui;

import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.gui.arena.ArenaNavigationGui;
import me.brandonli.murderrun.gui.game.GameCreationGui;
import me.brandonli.murderrun.gui.lobby.LobbyNavigationGui;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.ComponentUtils;
import me.brandonli.murderrun.utils.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class CentralGui extends ChestGui {

  private static final Pattern CENTRAL_GUI_PATTERN = new Pattern("111111111", "111345111", "111111111", "111121111");

  private final MurderRun plugin;
  private final HumanEntity watcher;

  public CentralGui(final MurderRun plugin, final HumanEntity watcher) {
    super(4, ComponentUtils.serializeComponentToLegacyString(Message.CENTRAL_GUI_TITLE.build()), plugin);
    this.plugin = plugin;
    this.watcher = watcher;
  }

  @Override
  public void update() {
    super.update();
    this.addPane(this.createPane());
    this.setOnGlobalClick(event -> event.setCancelled(true));
  }

  private PatternPane createPane() {
    final PatternPane pane = new PatternPane(0, 0, 9, 4, CENTRAL_GUI_PATTERN);
    pane.bindItem('1', this.createBackground());
    pane.bindItem('2', this.createCloseButton());
    pane.bindItem('3', this.createLobbyButton());
    pane.bindItem('4', this.createArenaButton());
    pane.bindItem('5', this.createGameButton());
    return pane;
  }

  private GuiItem createBackground() {
    return new GuiItem(Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(), this.plugin);
  }

  private GuiItem createCloseButton() {
    return new GuiItem(
      Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
      event -> this.watcher.closeInventory(),
      this.plugin
    );
  }

  private GuiItem createGameButton() {
    return new GuiItem(
      Item.builder(Material.RED_BANNER).name(Message.CENTRAL_GUI_GAME.build()).build(),
      this::handleGameClick,
      this.plugin
    );
  }

  private GuiItem createArenaButton() {
    return new GuiItem(
      Item.builder(Material.YELLOW_BANNER).name(Message.CENTRAL_GUI_ARENA.build()).build(),
      this::handleArenaClick,
      this.plugin
    );
  }

  private void handleGameClick(final InventoryClickEvent event) {
    final GameCreationGui gui = new GameCreationGui(this.plugin, this.watcher);
    gui.registerEvents();
    gui.update();
    gui.show(this.watcher);
  }

  private void handleArenaClick(final InventoryClickEvent event) {
    final ChestGui gui = new ArenaNavigationGui(this.plugin, this.watcher);
    gui.update();
    gui.show(this.watcher);
  }

  private GuiItem createLobbyButton() {
    return new GuiItem(
      Item.builder(Material.WHITE_BANNER).name(Message.CENTRAL_GUI_LOBBY.build()).build(),
      this::handleLobbyClick,
      this.plugin
    );
  }

  private void handleLobbyClick(final InventoryClickEvent event) {
    final ChestGui gui = new LobbyNavigationGui(this.plugin, this.watcher);
    gui.update();
    gui.show(this.watcher);
  }
}
