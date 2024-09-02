package io.github.pulsebeat02.murderrun.commmand.gui.lobby;

import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

@SuppressWarnings("all")
public final class LobbyModificationGui extends ChestGui implements Listener {

  private static final Pattern CREATE_LOBBY_PATTERN =
      new Pattern("111111111", "123411151", "111161111");

  private final MurderRun plugin;
  private final HumanEntity watcher;
  private final Audience audience;
  private final boolean editMode;

  private Location spawn;
  private String lobbyName;
  private boolean listenForSpawn;
  private boolean listenForName;

  public LobbyModificationGui(
      final MurderRun plugin,
      final HumanEntity watcher,
      final boolean editMode,
      final Gui previous) {
    this(plugin, watcher, "", watcher.getLocation(), editMode, previous);
  }

  public LobbyModificationGui(
      final MurderRun plugin,
      final HumanEntity watcher,
      final String lobbyName,
      final Location spawn,
      final boolean editMode,
      final Gui previous) {
    super(
        3, AdventureUtils.serializeComponentToLegacyString(Message.CREATE_LOBBY_GUI_TITLE.build()));
    final Server server = plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final UUID uuid = watcher.getUniqueId();
    this.plugin = plugin;
    this.watcher = watcher;
    this.audience = audiences.player(uuid);
    this.spawn = spawn;
    this.lobbyName = lobbyName;
    this.listenForSpawn = false;
    this.editMode = editMode;
    manager.registerEvents(this, plugin);
  }

  @Override
  public void update() {
    super.update();
    final PatternPane pane = new PatternPane(0, 0, 9, 3, CREATE_LOBBY_PATTERN);
    pane.bindItem('1', this.createBorderStack());
    pane.bindItem('2', this.createEditNameStack());
    pane.bindItem('3', this.createEditSpawnStack());
    pane.bindItem('4', this.createDeleteStack());
    pane.bindItem('5', this.createApplyStack());
    pane.bindItem('6', this.createCloseStack());
    this.addPane(pane);
    this.setOnClose(event -> {
      if (this.listenForSpawn || this.listenForName) {
        return;
      }
      final HandlerList list = BlockBreakEvent.getHandlerList();
      list.unregister(this);
    });
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
        event -> {
          event.setCancelled(true);

          if (this.lobbyName.isEmpty()) {
            final Component msg = Message.LOBBY_NAME_ERROR.build();
            this.audience.sendMessage(msg);
            return;
          }

          final LobbyManager manager = this.plugin.getLobbyManager();
          manager.addLobby(this.lobbyName, this.spawn);
          this.plugin.updatePluginData();

          final Component msg1 = Message.LOBBY_BUILT.build();
          this.audience.sendMessage(msg1);
        });
  }

  private GuiItem createDeleteStack() {
    if (this.editMode) {
      return new GuiItem(
          Item.builder(Material.RED_WOOL)
              .name(Message.CREATE_LOBBY_GUI_DELETE.build())
              .build(),
          event -> {
            final LobbyManager manager = this.plugin.getLobbyManager();
            manager.removeLobby(this.lobbyName);

            final Component msg = Message.LOBBY_REMOVE.build(this.lobbyName);
            this.audience.sendMessage(msg);
            event.setCancelled(true);
          });
    } else {
      return this.createBorderStack();
    }
  }

  private GuiItem createEditSpawnStack() {
    final Component message = AdventureUtils.createLocationComponent(
        Message.CREATE_LOBBY_GUI_EDIT_SPAWN_DISPLAY, this.spawn);
    return new GuiItem(
        Item.builder(Material.ANVIL)
            .name(message)
            .lore(Message.CREATE_LOBBY_GUI_EDIT_SPAWN_LORE.build())
            .build(),
        event -> {
          this.watcher.closeInventory();
          this.listenForSpawn = true;

          final Component msg = Message.CREATE_LOBBY_GUI_EDIT_SPAWN.build();
          this.audience.sendMessage(msg);
          event.setCancelled(true);
        });
  }

  private GuiItem createEditNameStack() {
    return new GuiItem(
        Item.builder(Material.ANVIL)
            .name(Message.CREATE_LOBBY_GUI_EDIT_NAME_DISPLAY.build(this.lobbyName))
            .lore(Message.CREATE_LOBBY_GUI_EDIT_NAME_LORE.build())
            .build(),
        event -> {
          event.setCancelled(true);
          this.listenForName = true;
          this.watcher.closeInventory();
          final Component msg = Message.CREATE_LOBBY_GUI_EDIT_NAME.build();
          this.audience.sendMessage(msg);
        });
  }

  private GuiItem createBorderStack() {
    return new GuiItem(
        Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(),
        event -> event.setCancelled(true));
  }
}
