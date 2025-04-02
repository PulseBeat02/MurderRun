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
import me.brandonli.murderrun.utils.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class CentralGui extends PatternGui {

  private static final List<String> CENTRAL_GUI_PATTERN = List.of("111111111", "111345111", "111111111", "111121111");

  private final MurderRun plugin;
  private final Player watcher;

  public CentralGui(final MurderRun plugin, final Player watcher) {
    super(Message.CENTRAL_GUI_TITLE.build(), 4, InteractionModifier.VALUES);
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
