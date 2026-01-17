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

import static net.kyori.adventure.text.Component.empty;

import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.GuiItem;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.lobby.LobbyManager;
import me.brandonli.murderrun.gui.PatternGui;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.ComponentUtils;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.map.MapUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class LobbyModificationGui extends PatternGui implements Listener {

  private static final List<String> CREATE_LOBBY_PATTERN =
      List.of("111111111", "123411151", "111111111", "111161111");

  private final MurderRun plugin;
  private final Player watcher;
  private final Audience audience;
  private final boolean editMode;
  private final String originalName;
  private final AtomicInteger currentMode;

  private volatile Location spawn;
  private volatile Location first;
  private volatile Location second;
  private volatile String lobbyName;
  private final AtomicBoolean listenForBreaks;
  private final AtomicBoolean listenForName;

  public LobbyModificationGui(
      final MurderRun plugin, final Player watcher, final boolean editMode) {
    this(
        plugin,
        watcher,
        "None",
        watcher.getLocation(),
        watcher.getLocation(),
        watcher.getLocation(),
        editMode);
  }

  public LobbyModificationGui(
      final MurderRun plugin,
      final Player watcher,
      final String lobbyName,
      final Location first,
      final Location second,
      final Location spawn,
      final boolean editMode) {
    super(Message.CREATE_LOBBY_GUI_TITLE.build(), 4, InteractionModifier.VALUES);
    this.originalName = lobbyName;
    this.audience = this.getAudience(plugin, watcher);
    this.currentMode = new AtomicInteger(0);
    this.plugin = plugin;
    this.first = first;
    this.second = second;
    this.watcher = watcher;
    this.spawn = spawn;
    this.lobbyName = lobbyName;
    this.editMode = editMode;
    this.listenForBreaks = new AtomicBoolean(false);
    this.listenForName = new AtomicBoolean(false);
  }

  public void registerEvents() {
    final Server server = this.plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    manager.registerEvents(this, this.plugin);
  }

  private Audience getAudience(
      @UnderInitialization LobbyModificationGui this,
      final MurderRun plugin,
      final HumanEntity watcher) {
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final UUID uuid = watcher.getUniqueId();
    return audiences.player(uuid);
  }

  @Override
  public void update() {
    super.update();
    this.createPane();
    this.popularize(CREATE_LOBBY_PATTERN);
    this.setCloseGuiAction(this::unregisterEvents);
  }

  private void createPane() {
    this.map('1', this.createBorderStack());
    this.map('2', this.createEditNameStack());
    this.map('3', this.createEditSpawnStack());
    this.map('4', this.createDeleteStack());
    this.map('5', this.createApplyStack());
    this.map('6', this.createCloseStack());
  }

  private void unregisterEvents(final InventoryCloseEvent event) {
    if (this.listenForBreaks.get() || this.listenForName.get()) {
      return;
    }
    final HandlerList list = BlockBreakEvent.getHandlerList();
    list.unregister(this);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerChat(final AsyncChatEvent event) {
    final Player player = event.getPlayer();
    if (player != this.watcher) {
      return;
    }

    if (!this.listenForName.get()) {
      return;
    }
    event.setCancelled(true);

    final Component component = event.message();
    final String msg = ComponentUtils.serializeComponentToPlain(component);
    if (this.listenForBreaks.get()) {
      final String upper = msg.toUpperCase();
      final Location location = player.getLocation();
      if (upper.equals("SKIP")) {
        this.sendProperMessage(location, true);
        return;
      }
    }

    this.lobbyName = msg;
    this.listenForName.set(false);
    this.showAsync();
  }

  private void showAsync() {
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.callSyncMethod(this.plugin, () -> {
      this.update();
      this.open(this.watcher);
      return null;
    });
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerInteract(final BlockBreakEvent event) {
    final Player player = event.getPlayer();
    if (player != this.watcher) {
      return;
    }

    if (!this.listenForBreaks.get()) {
      return;
    }
    event.setCancelled(true);

    final Block block = event.getBlock();
    final Location location = block.getLocation();
    this.sendProperMessage(location, false);
  }

  private void sendProperMessage(final Location location, final boolean skip) {
    final int current = this.currentMode.get();
    if (!skip) {
      switch (current) {
        case 0 -> this.first = location;
        case 1 -> this.second = location;
        case 2 -> this.spawn = location;
        default -> throw new IllegalStateException();
      }
    }

    switch (current) {
      case 0 -> this.audience.sendMessage(Message.CREATE_LOBBY_GUI_EDIT_SECOND.build());
      case 1 -> this.audience.sendMessage(Message.CREATE_LOBBY_GUI_EDIT_SPAWN.build());
      case 2 -> {
        this.listenForBreaks.set(false);
        this.listenForName.set(false);
        this.showAsync();
      }
      default -> throw new IllegalStateException();
    }

    this.currentMode.incrementAndGet();
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
        Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
        event -> this.close(this.watcher));
  }

  private GuiItem createApplyStack() {
    return new GuiItem(
        Item.builder(Material.GREEN_WOOL)
            .name(Message.CREATE_LOBBY_GUI_APPLY.build())
            .build(),
        this::createNewLobby);
  }

  private void createNewLobby(final InventoryClickEvent event) {
    if (this.lobbyName.isEmpty() || this.lobbyName.equals("None")) {
      final Component msg = Message.LOBBY_NAME_ERROR.build();
      this.audience.sendMessage(msg);
      return;
    }
    this.watcher.closeInventory();

    final Component msg = Message.LOBBY_CREATE_LOAD.build();
    this.audience.sendMessage(msg);

    final Location[] corners = {this.first, this.second};
    final LobbyManager manager = this.plugin.getLobbyManager();
    final Location actual = MapUtils.getSafeSpawn(this.spawn);
    manager.addLobby(this.lobbyName, corners, actual);

    if (!this.lobbyName.equals(this.originalName)) {
      manager.removeLobby(this.originalName);
    }

    this.plugin.updatePluginData();

    final Component msgAfter = Message.LOBBY_BUILT.build();
    this.audience.sendMessage(msgAfter);
  }

  private GuiItem createDeleteStack() {
    if (this.editMode) {
      return new GuiItem(
          Item.builder(Material.RED_WOOL)
              .name(Message.CREATE_LOBBY_GUI_DELETE.build())
              .build(),
          this::deleteAndCreateLobby);
    } else {
      return this.createBorderStack();
    }
  }

  private void deleteAndCreateLobby(final InventoryClickEvent event) {
    final LobbyManager manager = this.plugin.getLobbyManager();
    manager.removeLobby(this.lobbyName);
    this.watcher.closeInventory();
    final Component msg = Message.LOBBY_REMOVE.build(this.lobbyName);
    this.audience.sendMessage(msg);
  }

  private GuiItem createEditSpawnStack() {
    final Component title = Message.CREATE_LOBBY_GUI_EDIT_LOCATIONS_DISPLAY.build();
    final Component tooltip = Message.CREATE_LOBBY_GUI_EDIT_LOCATIONS_LORE1.build();
    final Component space = empty();
    final Component spawnMsg = ComponentUtils.createLocationComponent(
        Message.CREATE_LOBBY_GUI_EDIT_LOCATIONS_LORE2, this.spawn);
    final Component firstMsg = ComponentUtils.createLocationComponent(
        Message.CREATE_LOBBY_GUI_EDIT_LOCATIONS_LORE3, this.first);
    final Component secondMsg = ComponentUtils.createLocationComponent(
        Message.CREATE_LOBBY_GUI_EDIT_LOCATIONS_LORE4, this.second);
    final List<Component> lore = List.of(tooltip, space, spawnMsg, firstMsg, secondMsg);
    return new GuiItem(
        Item.builder(Material.ANVIL).name(title).lore(lore).build(), this::listenForBlockBreak);
  }

  private void listenForBlockBreak(final InventoryClickEvent event) {
    this.listenForBreaks.set(true);
    this.watcher.closeInventory();
    final Component msg = Message.CREATE_LOBBY_GUI_EDIT_FIRST.build();
    this.audience.sendMessage(msg);
  }

  private GuiItem createEditNameStack() {
    return new GuiItem(
        Item.builder(Material.ANVIL)
            .name(Message.CREATE_LOBBY_GUI_EDIT_NAME_DISPLAY.build(this.lobbyName))
            .lore(Message.CREATE_LOBBY_GUI_EDIT_NAME_LORE.build())
            .build(),
        this::listenForMessage);
  }

  private void listenForMessage(final InventoryClickEvent event) {
    this.listenForName.set(true);
    this.watcher.closeInventory();
    final Component msg = Message.CREATE_LOBBY_GUI_EDIT_NAME.build();
    this.audience.sendMessage(msg);
  }

  private GuiItem createBorderStack() {
    return new GuiItem(
        Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build());
  }
}
