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

import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.List;
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

public final class CentralGui extends PatternGui {

  private static final List<String> CENTRAL_GUI_PATTERN = List.of("111111111", "111345111", "111111111", "111121111");

  private final MurderRun plugin;
  private final HumanEntity watcher;

  public CentralGui(final MurderRun plugin, final HumanEntity watcher) {
    super(4, ComponentUtils.serializeComponentToLegacyString(Message.CENTRAL_GUI_TITLE.build()), InteractionModifier.VALUES);
    this.plugin = plugin;
    this.watcher = watcher;
  }

  @Override
  public void update() {
    super.update();
    this.createPane();
    this.popularize(CENTRAL_GUI_PATTERN);
  }

  private void createPane() {
    this.map('1', this.createBackground());
    this.map('2', this.createCloseButton());
    this.map('3', this.createLobbyButton());
    this.map('4', this.createArenaButton());
    this.map('5', this.createGameButton());
  }

  private GuiItem createBackground() {
    return new GuiItem(Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build());
  }

  private GuiItem createCloseButton() {
    return new GuiItem(Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(), event -> this.close(this.watcher));
  }

  private GuiItem createGameButton() {
    return new GuiItem(Item.builder(Material.RED_BANNER).name(Message.CENTRAL_GUI_GAME.build()).build(), this::handleGameClick);
  }

  private GuiItem createArenaButton() {
    return new GuiItem(Item.builder(Material.YELLOW_BANNER).name(Message.CENTRAL_GUI_ARENA.build()).build(), this::handleArenaClick);
  }

  private void handleGameClick(final InventoryClickEvent event) {
    final GameCreationGui gui = new GameCreationGui(this.plugin, this.watcher);
    gui.registerEvents();
    gui.update();
    gui.open(this.watcher);
  }

  private void handleArenaClick(final InventoryClickEvent event) {
    final ArenaNavigationGui gui = new ArenaNavigationGui(this.plugin, this.watcher);
    gui.update();
    gui.open(this.watcher);
  }

  private GuiItem createLobbyButton() {
    return new GuiItem(Item.builder(Material.WHITE_BANNER).name(Message.CENTRAL_GUI_LOBBY.build()).build(), this::handleLobbyClick);
  }

  private void handleLobbyClick(final InventoryClickEvent event) {
    final LobbyNavigationGui gui = new LobbyNavigationGui(this.plugin, this.watcher);
    gui.update();
    gui.open(this.watcher);
  }
}
