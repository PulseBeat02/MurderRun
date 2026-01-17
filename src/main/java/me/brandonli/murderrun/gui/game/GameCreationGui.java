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
package me.brandonli.murderrun.gui.game;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import com.google.common.primitives.Ints;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.GuiItem;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.arena.ArenaManager;
import me.brandonli.murderrun.game.lobby.Lobby;
import me.brandonli.murderrun.game.lobby.LobbyManager;
import me.brandonli.murderrun.gui.PatternGui;
import me.brandonli.murderrun.gui.arena.ArenaListGui;
import me.brandonli.murderrun.gui.lobby.LobbyListGui;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.ComponentUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.Item;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class GameCreationGui extends PatternGui implements Listener {

  private static final List<String> CREATE_GAME_PATTERN =
      List.of("000000000", "012345670", "000000000", "000809000");

  private final MurderRun plugin;
  private final Player watcher;
  private final Audience audience;

  private volatile Lobby lobby;
  private volatile Arena arena;

  private String id;
  private int min;
  private int max;
  private boolean quickJoin;
  private int mode;

  private final AtomicBoolean noCancel;
  private final AtomicBoolean listenForId;
  private final AtomicBoolean listenForMin;
  private final AtomicBoolean listenForMax;

  public GameCreationGui(final MurderRun plugin, final Player watcher) {
    super(Message.CREATE_GAME_GUI_TITLE.build(), 4, InteractionModifier.VALUES);
    this.plugin = plugin;
    this.watcher = watcher;
    this.audience = this.getAudience(plugin, watcher);
    this.noCancel = new AtomicBoolean(true);
    this.listenForId = new AtomicBoolean(false);
    this.listenForMin = new AtomicBoolean(false);
    this.listenForMax = new AtomicBoolean(false);
  }

  public void registerEvents() {
    final Server server = this.plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    manager.registerEvents(this, this.plugin);
  }

  private Audience getAudience(
      @UnderInitialization GameCreationGui this,
      final MurderRun plugin,
      final HumanEntity watcher) {
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final UUID uuid = watcher.getUniqueId();
    return audiences.player(uuid);
  }

  private void unregisterEvents(final InventoryCloseEvent event) {
    if (this.noCancel.get()) {
      return;
    }

    final HandlerList list = AsyncChatEvent.getHandlerList();
    list.unregister(this);
  }

  @Override
  public void update() {
    super.update();
    this.createPane();
    this.popularize(CREATE_GAME_PATTERN);
    this.setCloseGuiAction(this::unregisterEvents);
  }

  private void createPane() {
    this.map('0', this.createBorderStack());
    this.map('1', this.createLobbyStack());
    this.map('2', this.createArenaStack());
    this.map('3', this.createEditIdStack());
    this.map('4', this.createEditModeStack());
    this.map('5', this.createEditMinStack());
    this.map('6', this.createEditMaxStack());
    this.map('7', this.createQuickJoinStack());
    this.map('8', this.createApplyStack());
    this.map('9', this.createCloseStack());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerChat(final AsyncChatEvent event) {
    final Player player = event.getPlayer();
    if (player != this.watcher) {
      return;
    }

    if (!this.listenForId.get() && !this.listenForMin.get() && !this.listenForMax.get()) {
      return;
    }
    event.setCancelled(true);

    final Component component = event.message();
    final String msg = ComponentUtils.serializeComponentToPlain(component);
    if (this.listenForId.get()) {
      this.id = msg;
      this.listenForId.set(false);
    }

    if (this.listenForMin.get()) {
      this.listenForMin.set(this.parsePlayerCount(msg, true));
    }

    if (this.listenForMax.get()) {
      this.listenForMax.set(this.parsePlayerCount(msg, false));
    }

    this.showAsync(player);
  }

  private boolean parsePlayerCount(final String msg, final boolean isMin) {
    final Integer wrapped = Ints.tryParse(msg);
    if (wrapped == null || wrapped < 2) {
      final Component message = Message.GAME_CREATE_EDIT_COUNT_ERROR.build();
      this.audience.sendMessage(message);
      return true;
    }

    if (isMin) {
      this.min = wrapped;
    } else {
      this.max = wrapped;
    }

    return false;
  }

  private void showAsync(final Player player) {
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.callSyncMethod(this.plugin, () -> {
      this.update();
      this.open(player);
      return null;
    });
  }

  private GuiItem createEditModeStack() {
    final Material type = this.getEditModeMaterial();
    return new GuiItem(
        Item.builder(type)
            .name(Message.GAME_CREATE_EDIT_MODE_DISPLAY.build())
            .lore(Message.GAME_CREATE_EDIT_MODE_LORE.build())
            .build(),
        this::handleGameModeClick);
  }

  private void handleGameModeClick(final InventoryClickEvent event) {
    if (this.mode + 1 > 2) {
      this.mode = 0;
    } else {
      this.mode++;
    }
    final ItemStack stack = requireNonNull(event.getCurrentItem());
    final Material type = this.getEditModeMaterial();
    final ItemStack dupe = stack.withType(type);
    event.setCurrentItem(dupe);
  }

  private Material getEditModeMaterial() {
    return switch (this.mode) {
      case 0 -> Material.FIREWORK_STAR; // DEFAULT
      case 1 -> Material.BLUE_ICE; // FREEZE TAG
      case 2 -> Material.DIAMOND_SWORD; // ONE BOUNCE
      default -> throw new IllegalStateException("Unexpected value");
    };
  }

  private GuiItem createQuickJoinStack() {
    final Material type = this.getQuickJoinMaterial();
    return new GuiItem(
        Item.builder(type)
            .name(Message.GAME_CREATE_EDIT_QUICK_JOIN_DISPLAY.build())
            .lore(Message.GAME_CREATE_EDIT_QUICK_JOIN_LORE.build())
            .build(),
        this::handleQuickJoinClick);
  }

  private void handleQuickJoinClick(final InventoryClickEvent event) {
    this.quickJoin = !this.quickJoin;
    final ItemStack stack = requireNonNull(event.getCurrentItem());
    final Material type = this.getQuickJoinMaterial();
    final ItemStack dupe = stack.withType(type);
    event.setCurrentItem(dupe);
  }

  private Material getQuickJoinMaterial() {
    return this.quickJoin ? Material.LIME_WOOL : Material.RED_WOOL;
  }

  private GuiItem createEditMinStack() {
    return new GuiItem(
        Item.builder(Material.ANVIL)
            .name(Message.GAME_CREATE_EDIT_MIN_DISPLAY.build(this.min))
            .lore(Message.GAME_CREATE_EDIT_MIN_LORE.build())
            .build(),
        this::listenForMin);
  }

  private GuiItem createEditMaxStack() {
    return new GuiItem(
        Item.builder(Material.ANVIL)
            .name(Message.GAME_CREATE_EDIT_MAX_DISPLAY.build(this.max))
            .lore(Message.GAME_CREATE_EDIT_MAX_LORE.build())
            .build(),
        this::listenForMax);
  }

  private GuiItem createEditIdStack() {
    return new GuiItem(
        Item.builder(Material.ANVIL)
            .name(Message.GAME_CREATE_EDIT_ID_DISPLAY.build(this.id))
            .lore(Message.GAME_CREATE_EDIT_ID_LORE.build())
            .build(),
        this::listenForIdMessage);
  }

  private void listenForMax(final InventoryClickEvent event) {
    this.listenForMax.set(true);
    this.watcher.closeInventory();
    final Component msg = Message.GAME_CREATE_EDIT_MAX.build();
    this.audience.sendMessage(msg);
  }

  private void listenForMin(final InventoryClickEvent event) {
    this.listenForMin.set(true);
    this.watcher.closeInventory();
    final Component msg = Message.GAME_CREATE_EDIT_MIN.build();
    this.audience.sendMessage(msg);
  }

  private void listenForIdMessage(final InventoryClickEvent event) {
    this.listenForId.set(true);
    this.watcher.closeInventory();
    final Component msg = Message.GAME_CREATE_EDIT_ID.build();
    this.audience.sendMessage(msg);
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
        Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
        event -> this.close(this.watcher));
  }

  private GuiItem createApplyStack() {
    return new GuiItem(
        Item.builder(Material.GREEN_WOOL)
            .name(Message.CREATE_GAME_GUI_APPLY.build())
            .build(),
        this::createNewGame);
  }

  private void createNewGame(final InventoryClickEvent event) {
    if (this.checkMissingProperty()) {
      final Component msg = Message.CREATE_GAME_GUI_ERROR.build();
      this.audience.sendMessage(msg);
      return;
    }
    this.watcher.closeInventory();

    final Player player = (Player) this.watcher;
    final String cmd = this.constructCommand();

    player.performCommand(cmd);

    this.noCancel.set(false);
  }

  private String constructCommand() {
    final String lobbyName = this.lobby.getName();
    final String arenaName = this.arena.getName();
    final String mode =
        switch (this.mode) {
          case 0 -> "default";
          case 1 -> "freeze_tag";
          case 2 -> "one_bounce";
          default -> throw new IllegalStateException("Unexpected value");
        };
    return "murder game create %s %s %s %s %s %s %s"
        .formatted(arenaName, lobbyName, this.id, mode, this.min, this.max, this.quickJoin);
  }

  private boolean checkMissingProperty() {
    return (this.lobby == null
        || this.arena == null
        || this.id == null
        || this.min < 2
        || this.max < 2
        || this.min > this.max);
  }

  private GuiItem createArenaStack() {
    final String name = this.arena == null ? "" : this.arena.getName();
    return new GuiItem(
        Item.builder(Material.ANVIL)
            .name(Message.CREATE_GAME_GUI_ARENA_DISPLAY.build(name))
            .lore(Message.CREATE_GAME_GUI_ARENA_LORE.build())
            .build(),
        this::chooseArena);
  }

  private GuiItem createLobbyStack() {
    final String name = this.lobby == null ? "" : this.lobby.getName();
    return new GuiItem(
        Item.builder(Material.ANVIL)
            .name(Message.CREATE_GAME_GUI_LOBBY_DISPLAY.build(name))
            .lore(Message.CREATE_GAME_GUI_LOBBY_LORE.build())
            .build(),
        this::chooseLobby);
  }

  private void chooseLobby(final InventoryClickEvent event) {
    final LobbyListGui gui =
        new LobbyListGui(this.plugin, this.watcher, this::handleLobbyClickEvent);
    gui.update();
    gui.open(this.watcher);
  }

  private void handleLobbyClickEvent(final InventoryClickEvent event) {
    final ItemStack item = event.getCurrentItem();
    if (item == null) {
      return;
    }

    final ItemMeta meta = requireNonNull(item.getItemMeta());
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final String name = requireNonNull(container.get(Keys.LOBBY_NAME, PersistentDataType.STRING));
    final LobbyManager manager = this.plugin.getLobbyManager();
    this.lobby = requireNonNull(manager.getLobby(name));
    this.update();
    this.open(this.watcher);
  }

  private void chooseArena(final InventoryClickEvent event) {
    final ArenaListGui gui =
        new ArenaListGui(this.plugin, this.watcher, this::handleArenaClickEvent);
    gui.update();
    gui.open(this.watcher);
  }

  private void handleArenaClickEvent(final InventoryClickEvent event) {
    final ItemStack item = event.getCurrentItem();
    if (item == null) {
      return;
    }

    final ItemMeta meta = requireNonNull(item.getItemMeta());
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final String name = requireNonNull(container.get(Keys.ARENA_NAME, PersistentDataType.STRING));
    final ArenaManager manager = this.plugin.getArenaManager();
    this.arena = requireNonNull(manager.getArena(name));
    this.update();
    this.open(this.watcher);
  }

  private GuiItem createBorderStack() {
    return new GuiItem(
        Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build());
  }
}
