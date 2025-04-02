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

import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.lobby.LobbyManager;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class LobbyModificationGui extends ChestGui implements Listener {

  private static final Pattern CREATE_LOBBY_PATTERN = new Pattern("111111111", "123411151", "111111111", "111161111");

  private final MurderRun plugin;
  private final HumanEntity watcher;
  private final Audience audience;
  private final boolean editMode;
  private final PatternPane pane;
  private final String originalName;
  private final AtomicInteger currentMode;

  private volatile Location spawn;
  private volatile Location first;
  private volatile Location second;
  private volatile String lobbyName;
  private volatile boolean listenForBreaks;
  private volatile boolean listenForName;

  public LobbyModificationGui(final MurderRun plugin, final HumanEntity watcher, final boolean editMode) {
    this(plugin, watcher, "None", watcher.getLocation(), watcher.getLocation(), watcher.getLocation(), editMode);
  }

  public LobbyModificationGui(
    final MurderRun plugin,
    final HumanEntity watcher,
    final String lobbyName,
    final Location first,
    final Location second,
    final Location spawn,
    final boolean editMode
  ) {
    super(4, ComponentUtils.serializeComponentToLegacyString(Message.CREATE_LOBBY_GUI_TITLE.build()), plugin);
    this.originalName = lobbyName;
    this.pane = new PatternPane(0, 0, 9, 4, CREATE_LOBBY_PATTERN);
    this.audience = this.getAudience(plugin, watcher);
    this.currentMode = new AtomicInteger(0);
    this.plugin = plugin;
    this.first = first;
    this.second = second;
    this.watcher = watcher;
    this.spawn = spawn;
    this.lobbyName = lobbyName;
    this.editMode = editMode;
  }

  public void registerEvents() {
    final Server server = this.plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    manager.registerEvents(this, this.plugin);
  }

  private Audience getAudience(@UnderInitialization LobbyModificationGui this, final MurderRun plugin, final HumanEntity watcher) {
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final UUID uuid = watcher.getUniqueId();
    return audiences.player(uuid);
  }

  @Override
  public void update() {
    super.update();
    this.addPane(this.createPane());
    this.setOnClose(this::unregisterEvents);
    this.setOnGlobalClick(event -> event.setCancelled(true));
  }

  private PatternPane createPane() {
    this.pane.clear();
    this.pane.bindItem('1', this.createBorderStack());
    this.pane.bindItem('2', this.createEditNameStack());
    this.pane.bindItem('3', this.createEditSpawnStack());
    this.pane.bindItem('4', this.createDeleteStack());
    this.pane.bindItem('5', this.createApplyStack());
    this.pane.bindItem('6', this.createCloseStack());

    return this.pane;
  }

  private void unregisterEvents(final InventoryCloseEvent event) {
    if (this.listenForBreaks || this.listenForName) {
      return;
    }
    final HandlerList list = BlockBreakEvent.getHandlerList();
    list.unregister(this);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerChat(final AsyncPlayerChatEvent event) {
    final Player player = event.getPlayer();
    if (player != this.watcher) {
      return;
    }

    if (!this.listenForName) {
      return;
    }
    event.setCancelled(true);

    final String msg = event.getMessage();
    if (this.listenForBreaks) {
      final String upper = msg.toUpperCase();
      final Location location = player.getLocation();
      if (upper.equals("SKIP")) {
        this.sendProperMessage(location, true);
        return;
      }
    }

    this.lobbyName = event.getMessage();
    this.listenForName = false;
    this.showAsync();
  }

  private void showAsync() {
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.callSyncMethod(this.plugin, () -> {
      this.update();
      this.show(this.watcher);
      return null;
    });
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerInteract(final BlockBreakEvent event) {
    final Player player = event.getPlayer();
    if (player != this.watcher) {
      return;
    }

    if (!this.listenForBreaks) {
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
        default -> throw new AssertionError("Invalid mode!");
      }
    }

    switch (current) {
      case 0 -> this.audience.sendMessage(Message.CREATE_LOBBY_GUI_EDIT_SECOND.build());
      case 1 -> this.audience.sendMessage(Message.CREATE_LOBBY_GUI_EDIT_SPAWN.build());
      case 2 -> {
        this.listenForBreaks = false;
        this.listenForName = false;
        this.showAsync();
      }
      default -> throw new AssertionError("Invalid mode!");
    }

    this.currentMode.incrementAndGet();
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
      Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
      event -> this.watcher.closeInventory(),
      this.plugin
    );
  }

  private GuiItem createApplyStack() {
    return new GuiItem(
      Item.builder(Material.GREEN_WOOL).name(Message.CREATE_LOBBY_GUI_APPLY.build()).build(),
      this::createNewLobby,
      this.plugin
    );
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

    final Location[] corners = { this.first, this.second };
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
        Item.builder(Material.RED_WOOL).name(Message.CREATE_LOBBY_GUI_DELETE.build()).build(),
        this::deleteAndCreateLobby,
        this.plugin
      );
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
    final Component spawnMsg = ComponentUtils.createLocationComponent(Message.CREATE_LOBBY_GUI_EDIT_LOCATIONS_LORE2, this.spawn);
    final Component firstMsg = ComponentUtils.createLocationComponent(Message.CREATE_LOBBY_GUI_EDIT_LOCATIONS_LORE3, this.first);
    final Component secondMsg = ComponentUtils.createLocationComponent(Message.CREATE_LOBBY_GUI_EDIT_LOCATIONS_LORE4, this.second);
    final List<Component> lore = List.of(tooltip, space, spawnMsg, firstMsg, secondMsg);
    return new GuiItem(Item.builder(Material.ANVIL).name(title).lore(lore).build(), this::listenForBlockBreak, this.plugin);
  }

  private void listenForBlockBreak(final InventoryClickEvent event) {
    this.listenForBreaks = true;
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
      this::listenForMessage,
      this.plugin
    );
  }

  private void listenForMessage(final InventoryClickEvent event) {
    this.listenForName = true;
    this.watcher.closeInventory();
    final Component msg = Message.CREATE_LOBBY_GUI_EDIT_NAME.build();
    this.audience.sendMessage(msg);
  }

  private GuiItem createBorderStack() {
    return new GuiItem(
      Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(),
      event -> event.setCancelled(true),
      this.plugin
    );
  }
}
