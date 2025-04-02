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
package me.brandonli.murderrun.gui.lobby;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.List;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.lobby.Lobby;
import me.brandonli.murderrun.game.lobby.LobbyManager;
import me.brandonli.murderrun.gui.PatternGui;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class LobbyNavigationGui extends PatternGui {

  private static final List<String> NAVIGATION_LOBBY_PATTERN = List.of("111111111", "111314111", "111111111", "111121111");

  private final MurderRun plugin;
  private final Player watcher;

  public LobbyNavigationGui(final MurderRun plugin, final Player clicker) {
    super(Message.MANAGE_LOBBY_GUI_TITLE.build(), 4, InteractionModifier.VALUES);
    this.plugin = plugin;
    this.watcher = clicker;
  }

  @Override
  public void update() {
    super.update();
    this.createPane();
    this.popularize(NAVIGATION_LOBBY_PATTERN);
  }

  private void createPane() {
    this.map('1', this.createBorderStack());
    this.map('2', this.createCloseStack());
    this.map('3', this.createLobbyStack());
    this.map('4', this.createModifyStack());
  }

  private GuiItem createModifyStack() {
    return new GuiItem(Item.builder(Material.YELLOW_BANNER).name(Message.MANAGE_LOBBY_GUI_EDIT.build()).build(), this::createListingsMenu);
  }

  private void createListingsMenu(final InventoryClickEvent event) {
    final LobbyListGui gui = new LobbyListGui(this.plugin, this.watcher, this::handleLobbyClickEvent);
    gui.update();
    gui.open(this.watcher);
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
    gui.open(this.watcher);
  }

  private GuiItem createLobbyStack() {
    return new GuiItem(Item.builder(Material.GREEN_BANNER).name(Message.MANAGE_LOBBY_GUI_CREATE.build()).build(), this::createLobbyMenu);
  }

  private void createLobbyMenu(final InventoryClickEvent event) {
    final LobbyModificationGui gui = new LobbyModificationGui(this.plugin, this.watcher, false);
    gui.registerEvents();
    gui.update();
    gui.open(this.watcher);
  }

  private GuiItem createCloseStack() {
    return new GuiItem(Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(), event -> this.close(this.watcher));
  }

  private GuiItem createBorderStack() {
    return new GuiItem(Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build());
  }
}
