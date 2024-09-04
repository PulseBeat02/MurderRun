package io.github.pulsebeat02.murderrun.gui.lobby;

import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.UUID;
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

public final class LobbyModificationGui extends ChestGui implements Listener {

  private static final Pattern CREATE_LOBBY_PATTERN =
      new Pattern("111111111", "123411151", "111111111", "111161111");

  private final MurderRun plugin;
  private final HumanEntity watcher;
  private final Audience audience;
  private final String original;
  private final boolean editMode;
  private final PatternPane pane;

  private volatile Location spawn;
  private volatile String lobbyName;
  private volatile boolean listenForSpawn;
  private volatile boolean listenForName;

  public LobbyModificationGui(
      final MurderRun plugin, final HumanEntity watcher, final boolean editMode) {
    this(plugin, watcher, "None", watcher.getLocation(), editMode);
  }

  public LobbyModificationGui(
      final MurderRun plugin,
      final HumanEntity watcher,
      final String lobbyName,
      final Location spawn,
      final boolean editMode) {
    super(
        4, AdventureUtils.serializeComponentToLegacyString(Message.CREATE_LOBBY_GUI_TITLE.build()));
    final Server server = plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final UUID uuid = watcher.getUniqueId();
    this.plugin = plugin;
    this.watcher = watcher;
    this.audience = audiences.player(uuid);
    this.spawn = spawn;
    this.original = lobbyName;
    this.lobbyName = lobbyName;
    this.listenForSpawn = false;
    this.editMode = editMode;
    this.pane = new PatternPane(0, 0, 9, 4, CREATE_LOBBY_PATTERN);
    manager.registerEvents(this, plugin);
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

    if (this.listenForSpawn || this.listenForName) {
      return;
    }
    final HandlerList list = BlockBreakEvent.getHandlerList();
    list.unregister(this);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerChat(final AsyncPlayerChatEvent event) {

    if (!this.listenForName) {
      return;
    }

    final Player player = event.getPlayer();
    if (player != this.watcher) {
      return;
    }

    this.lobbyName = event.getMessage();
    this.listenForName = false;

    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.callSyncMethod(this.plugin, () -> {
      this.update();
      this.show(this.watcher);
      return null;
    });

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerInteract(final BlockBreakEvent event) {

    if (!this.listenForSpawn) {
      return;
    }

    final Player player = event.getPlayer();
    if (player != this.watcher) {
      return;
    }

    final Block block = event.getBlock();
    this.spawn = block.getLocation();
    this.listenForSpawn = false;
    this.update();
    this.show(this.watcher);

    event.setCancelled(true);
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
        Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
        event -> this.watcher.closeInventory());
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

    final LobbyManager manager = this.plugin.getLobbyManager();
    if (this.editMode) {
      manager.removeLobby(this.original);
    }
    manager.addLobby(this.lobbyName, this.spawn);

    this.plugin.updatePluginData();
    this.watcher.closeInventory();

    final Component msg1 = Message.LOBBY_BUILT.build();
    this.audience.sendMessage(msg1);
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
    final Component message = AdventureUtils.createLocationComponent(
        Message.CREATE_LOBBY_GUI_EDIT_SPAWN_DISPLAY, this.spawn);
    return new GuiItem(
        Item.builder(Material.ANVIL)
            .name(message)
            .lore(Message.CREATE_LOBBY_GUI_EDIT_SPAWN_LORE.build())
            .build(),
        this::listenForBlockBreak);
  }

  private void listenForBlockBreak(final InventoryClickEvent event) {
    this.listenForSpawn = true;
    this.watcher.closeInventory();
    final Component msg = Message.CREATE_LOBBY_GUI_EDIT_SPAWN.build();
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
    this.listenForName = true;
    this.watcher.closeInventory();
    final Component msg = Message.CREATE_LOBBY_GUI_EDIT_NAME.build();
    this.audience.sendMessage(msg);
  }

  private GuiItem createBorderStack() {
    return new GuiItem(
        Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(),
        event -> event.setCancelled(true));
  }
}
