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
import io.github.pulsebeat02.murderrun.utils.ComponentUtils;
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
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class LobbyModificationGui extends ChestGui implements Listener {

  private static final Pattern CREATE_LOBBY_PATTERN = new Pattern("111111111", "123411151", "111111111", "111161111");

  private final MurderRun plugin;
  private final HumanEntity watcher;
  private final Audience audience;
  private final boolean editMode;
  private final PatternPane pane;

  private volatile Location spawn;
  private volatile String lobbyName;
  private volatile boolean listenForSpawn;
  private volatile boolean listenForName;

  public LobbyModificationGui(final MurderRun plugin, final HumanEntity watcher, final boolean editMode) {
    this(plugin, watcher, "None", watcher.getLocation(), editMode);
  }

  public LobbyModificationGui(
    final MurderRun plugin,
    final HumanEntity watcher,
    final String lobbyName,
    final Location spawn,
    final boolean editMode
  ) {
    super(4, ComponentUtils.serializeComponentToLegacyString(Message.CREATE_LOBBY_GUI_TITLE.build()), plugin);
    this.pane = new PatternPane(0, 0, 9, 4, CREATE_LOBBY_PATTERN);
    this.audience = this.getAudience(plugin, watcher);
    this.plugin = plugin;
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
    if (this.listenForSpawn || this.listenForName) {
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

    if (!this.listenForSpawn) {
      return;
    }
    event.setCancelled(true);

    final Block block = event.getBlock();
    this.spawn = block.getLocation();

    this.listenForSpawn = false;
    this.update();
    this.show(this.watcher);
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

    final LobbyManager manager = this.plugin.getLobbyManager();
    manager.addLobby(this.lobbyName, this.spawn);
    this.plugin.updatePluginData();

    final Component msg = Message.LOBBY_BUILT.build();
    this.audience.sendMessage(msg);
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
    return new GuiItem(
      Item.builder(Material.ANVIL)
        .name(ComponentUtils.createLocationComponent(Message.CREATE_LOBBY_GUI_EDIT_SPAWN_DISPLAY, this.spawn))
        .lore(Message.CREATE_LOBBY_GUI_EDIT_SPAWN_LORE.build())
        .build(),
      this::listenForBlockBreak,
      this.plugin
    );
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
