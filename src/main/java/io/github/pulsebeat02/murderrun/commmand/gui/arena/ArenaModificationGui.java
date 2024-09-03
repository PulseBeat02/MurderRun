package io.github.pulsebeat02.murderrun.commmand.gui.arena;

import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
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

public final class ArenaModificationGui extends ChestGui implements Listener {

  private static final Pattern CREATE_ARENA_PATTERN =
      new Pattern("111111111", "123451161", "111111111", "111171111");

  private final MurderRun plugin;
  private final HumanEntity watcher;
  private final Audience audience;
  private final String original;
  private final boolean editMode;
  private final PatternPane pane;
  private final AtomicInteger currentMode;

  private volatile String arenaName;
  private volatile Location spawn;
  private volatile Location truck;
  private volatile Location first;
  private volatile Location second;
  private volatile Collection<Location> itemLocations;

  private volatile boolean listenForBreaks;
  private volatile boolean listenForName;

  public ArenaModificationGui(
      final MurderRun plugin, final HumanEntity watcher, final boolean editMode) {
    this(plugin, watcher, "None", watcher.getLocation(), editMode);
  }

  public ArenaModificationGui(
      final MurderRun plugin,
      final HumanEntity watcher,
      final String arenaName,
      final Location spawn,
      final boolean editMode) {
    super(
        4, AdventureUtils.serializeComponentToLegacyString(Message.CREATE_ARENA_GUI_TITLE.build()));
    final Server server = plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final UUID uuid = watcher.getUniqueId();
    this.plugin = plugin;
    this.watcher = watcher;
    this.audience = audiences.player(uuid);
    this.spawn = spawn;
    this.original = arenaName;
    this.arenaName = arenaName;
    this.listenForBreaks = false;
    this.editMode = editMode;
    this.pane = new PatternPane(0, 0, 9, 4, CREATE_ARENA_PATTERN);
    this.currentMode = new AtomicInteger(0);
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
    this.pane.bindItem('3', this.createEditSpawnStack()); // edit location
    this.pane.bindItem('4', null); // item drop locations
    this.pane.bindItem('5', this.createDeleteStack());
    this.pane.bindItem('6', this.createApplyStack());
    this.pane.bindItem('7', this.createCloseStack());

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

    if (!this.listenForName) {
      return;
    }

    final Player player = event.getPlayer();
    if (player != this.watcher) {
      return;
    }

    event.setCancelled(true);

    if (this.listenForBreaks) {
      final String msg = event.getMessage();
      final String upper = msg.toUpperCase();
      final Location location = player.getLocation();
      if (upper.equals("SKIP")) {
        this.sendProperMessage(location, true);
        return;
      }
    }

    this.arenaName = event.getMessage();
    this.listenForName = false;
    this.update();
    this.show(this.watcher);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerInteract(final BlockBreakEvent event) {

    if (!this.listenForBreaks) {
      return;
    }

    final Player player = event.getPlayer();
    if (player != this.watcher) {
      return;
    }

    event.setCancelled(true);

    final Block block = event.getBlock();
    final Location location = block.getLocation();
    this.sendProperMessage(location, false);
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
        this::createNewArena);
  }

  private void createNewArena(final InventoryClickEvent event) {

    if (this.arenaName.isEmpty() || this.arenaName.equals("None")) {
      final Component msg = Message.ARENA_NAME_ERROR.build();
      this.audience.sendMessage(msg);
      return;
    }

    final ArenaManager manager = this.plugin.getArenaManager();
    if (this.editMode) {
      manager.removeArena(this.original);
    }

    final Location[] corners = {this.first, this.second};
    final Location[] drops = this.itemLocations.toArray(new Location[0]);
    manager.addArena(this.arenaName, corners, drops, this.spawn, this.truck);

    this.plugin.updatePluginData();
    this.watcher.closeInventory();

    final Component msg1 = Message.ARENA_BUILT.build();
    this.audience.sendMessage(msg1);
  }

  private GuiItem createDeleteStack() {
    if (this.editMode) {
      return new GuiItem(
          Item.builder(Material.RED_WOOL)
              .name(Message.CREATE_LOBBY_GUI_DELETE.build())
              .build(),
          this::deleteAndCreateArena);
    } else {
      return this.createBorderStack();
    }
  }

  private void sendProperMessage(final Location location, final boolean skip) {

    final int current = this.currentMode.get();
    if (!skip) {
      switch (current) {
        case 0 -> this.first = location;
        case 1 -> this.second = location;
        case 2 -> this.truck = location;
        case 3 -> this.spawn = location;
      }
    }

    switch (current) {
      case 0 -> this.audience.sendMessage(Message.CREATE_ARENA_GUI_EDIT_SECOND.build());
      case 1 -> this.audience.sendMessage(Message.CREATE_ARENA_GUI_EDIT_SPAWN.build());
      case 2 -> this.audience.sendMessage(Message.CREATE_ARENA_GUI_EDIT_TRUCK.build());
      case 3 -> {
        this.listenForBreaks = false;
        this.listenForName = false;
        this.update();
        this.show(this.watcher);
      }
    }

    this.currentMode.incrementAndGet();
  }

  private void deleteAndCreateArena(final InventoryClickEvent event) {
    final ArenaManager manager = this.plugin.getArenaManager();
    manager.removeArena(this.arenaName);
    this.watcher.closeInventory();
    final Component msg = Message.ARENA_REMOVE.build(this.arenaName);
    this.audience.sendMessage(msg);
  }

  private GuiItem createEditSpawnStack() {
    final Component title = Message.CREATE_ARENA_GUI_EDIT_LOCATIONS_DISPLAY.build();
    final Component tooltip = Message.CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE1.build();
    final Component space = empty();
    final Component spawnMsg = AdventureUtils.createLocationComponent(
        Message.CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE2, this.spawn);
    final Component truckMsg = AdventureUtils.createLocationComponent(
        Message.CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE3, this.truck);
    final Component firstMsg = AdventureUtils.createLocationComponent(
        Message.CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE4, this.first);
    final Component secondMsg = AdventureUtils.createLocationComponent(
        Message.CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE5, this.second);
    final List<Component> lore = List.of(tooltip, space, spawnMsg, truckMsg, firstMsg, secondMsg);
    return new GuiItem(
        Item.builder(Material.ANVIL).name(title).lore(lore).build(), this::listenForBlockBreak);
  }

  private void listenForBlockBreak(final InventoryClickEvent event) {
    this.currentMode.set(0);
    this.listenForBreaks = true;
    this.listenForName = true;
    this.watcher.closeInventory();
    final Component msg = Message.CREATE_ARENA_GUI_EDIT_FIRST.build();
    this.audience.sendMessage(msg);
  }

  private GuiItem createEditNameStack() {
    return new GuiItem(
        Item.builder(Material.ANVIL)
            .name(Message.CREATE_ARENA_GUI_EDIT_NAME_DISPLAY.build(this.arenaName))
            .lore(Message.CREATE_ARENA_GUI_EDIT_NAME_LORE.build())
            .build(),
        this::listenForMessage);
  }

  private void listenForMessage(final InventoryClickEvent event) {
    this.listenForName = true;
    this.watcher.closeInventory();
    final Component msg = Message.CREATE_ARENA_GUI_EDIT_NAME.build();
    this.audience.sendMessage(msg);
  }

  private GuiItem createBorderStack() {
    return new GuiItem(
        Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(),
        event -> event.setCancelled(true));
  }
}
