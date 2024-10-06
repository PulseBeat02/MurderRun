package io.github.pulsebeat02.murderrun.gui.game;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import com.google.common.primitives.Ints;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.gui.arena.ArenaListGui;
import io.github.pulsebeat02.murderrun.gui.lobby.LobbyListGui;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.UUID;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class GameCreationGui extends ChestGui implements Listener {

  private static final Pattern CREATE_GAME_PATTERN =
      new Pattern("111111111", "123456781", "111111111", "111191111");

  private final MurderRun plugin;
  private final HumanEntity watcher;
  private final PatternPane pane;
  private final Audience audience;

  private volatile Lobby lobby;
  private volatile Arena arena;

  private String id;
  private int min;
  private int max;
  private boolean quickJoin;

  private volatile boolean noCancel;
  private volatile boolean listenForId;
  private volatile boolean listenForMin;
  private volatile boolean listenForMax;

  @SuppressWarnings("all")
  public GameCreationGui(final MurderRun plugin, final HumanEntity watcher) {
    super(
        4,
        AdventureUtils.serializeComponentToLegacyString(Message.CREATE_GAME_GUI_TITLE.build()),
        plugin);
    this.plugin = plugin;
    this.watcher = watcher;
    this.pane = new PatternPane(0, 0, 9, 4, CREATE_GAME_PATTERN);
    final Server server = plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final UUID uuid = watcher.getUniqueId();
    this.audience = audiences.player(uuid);
    noCancel = true;
    manager.registerEvents(this, plugin);
  }

  private void unregisterEvents(final InventoryCloseEvent event) {

    if (this.noCancel) {
      return;
    }

    final HandlerList list = AsyncPlayerChatEvent.getHandlerList();
    list.unregister(this);
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
    this.pane.bindItem('2', this.createLobbyStack());
    this.pane.bindItem('3', this.createArenaStack());
    this.pane.bindItem('4', this.createEditIdStack());
    this.pane.bindItem('5', this.createEditMinStack());
    this.pane.bindItem('6', this.createEditMaxStack());
    this.pane.bindItem('7', this.createQuickJoinStack());
    this.pane.bindItem('8', this.createApplyStack());
    this.pane.bindItem('9', this.createCloseStack());

    return this.pane;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerChat(final AsyncPlayerChatEvent event) {

    if (!this.listenForId && !this.listenForMin && !this.listenForMax) {
      return;
    }
    event.setCancelled(true);

    final Player player = event.getPlayer();
    if (player != this.watcher) {
      return;
    }

    final String msg = event.getMessage();
    if (this.listenForId) {
      this.id = msg;
      this.listenForId = false;
    }

    if (this.listenForMin) {
      this.listenForMin = this.parsePlayerCount(msg, true);
    }

    if (this.listenForMax) {
      this.listenForMax = this.parsePlayerCount(msg, false);
    }

    this.showInventory(player);
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
      this.listenForMin = false;
    } else {
      this.max = wrapped;
      this.listenForMax = false;
    }

    return false;
  }

  private void showInventory(final HumanEntity player) {
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.callSyncMethod(this.plugin, () -> {
      this.update();
      this.show(player);
      return null;
    });
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
    stack.setType(type);
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
    this.listenForMax = true;
    this.watcher.closeInventory();
    final Component msg = Message.GAME_CREATE_EDIT_MAX.build();
    this.audience.sendMessage(msg);
  }

  private void listenForMin(final InventoryClickEvent event) {
    this.listenForMin = true;
    this.watcher.closeInventory();
    final Component msg = Message.GAME_CREATE_EDIT_MIN.build();
    this.audience.sendMessage(msg);
  }

  private void listenForIdMessage(final InventoryClickEvent event) {
    this.listenForId = true;
    this.watcher.closeInventory();
    final Component msg = Message.GAME_CREATE_EDIT_ID.build();
    this.audience.sendMessage(msg);
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
        Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
        event -> this.watcher.closeInventory());
  }

  private GuiItem createApplyStack() {
    return new GuiItem(
        Item.builder(Material.GREEN_WOOL)
            .name(Message.CREATE_GAME_GUI_APPLY.build())
            .build(),
        this::createNewGame);
  }

  private void createNewGame(final InventoryClickEvent event) {

    final AudienceProvider provider = this.plugin.getAudience();
    final BukkitAudiences bukkitAudiences = provider.retrieve();
    final Audience audience = bukkitAudiences.player(this.watcher.getUniqueId());
    if (this.lobby == null
        || this.arena == null
        || this.id == null
        || this.min < 2
        || this.max < 2
        || this.min > this.max) {
      final Component msg = Message.CREATE_GAME_GUI_ERROR.build();
      audience.sendMessage(msg);
      return;
    }

    final Player player = (Player) this.watcher;
    final String lobbyName = this.lobby.getName();
    final String arenaName = this.arena.getName();
    player.performCommand("murder game create %s %s %s %s %s %s"
        .formatted(arenaName, lobbyName, this.id, this.min, this.max, this.quickJoin));
    this.noCancel = false;
    this.watcher.closeInventory();
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
    final ChestGui gui = new LobbyListGui(this.plugin, this.watcher, this::handleLobbyClickEvent);
    gui.update();
    gui.show(this.watcher);
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
    this.show(this.watcher);
  }

  private void chooseArena(final InventoryClickEvent event) {
    final ChestGui gui = new ArenaListGui(this.plugin, this.watcher, this::handleArenaClickEvent);
    gui.update();
    gui.show(this.watcher);
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
    this.show(this.watcher);
  }

  private GuiItem createBorderStack() {
    return new GuiItem(
        Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(),
        event -> event.setCancelled(true));
  }
}
