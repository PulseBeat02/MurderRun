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
package io.github.pulsebeat02.murderrun.gui.arena;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.arena.ArenaSchematic;
import io.github.pulsebeat02.murderrun.game.arena.drops.TerrainDropAnalyzer;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ComponentUtils;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class ArenaModificationGui extends ChestGui implements Listener {

  private static final Pattern CREATE_ARENA_PATTERN = new Pattern("111111111", "123451161", "111111111", "111171111");

  private final MurderRun plugin;
  private final HumanEntity watcher;
  private final Audience audience;
  private final boolean editMode;
  private final PatternPane pane;
  private final AtomicInteger currentMode;

  private WandListener listener;
  private final ArenaCreation creation;
  private volatile boolean listenForBreaks;
  private volatile boolean listenForName;
  private volatile boolean listenForItems;

  public ArenaModificationGui(final MurderRun plugin, final HumanEntity watcher, final boolean editMode) {

    this(
      plugin,
      watcher,
      "None",
      watcher.getLocation(),
      watcher.getLocation(),
      watcher.getLocation(),
      watcher.getLocation(),
      Collections.synchronizedSet(new HashSet<>()),
      editMode
    );
  }

  public ArenaModificationGui(
    final MurderRun plugin,
    final HumanEntity watcher,
    final String arenaName,
    final Location spawn,
    final Location truck,
    final Location first,
    final Location second,
    final Collection<Location> itemLocations,
    final boolean editMode
  ) {
    super(4, ComponentUtils.serializeComponentToLegacyString(Message.CREATE_ARENA_GUI_TITLE.build()), plugin);
    final ArenaCreationManager manager = plugin.getArenaCreationManager();
    final UUID uuid = watcher.getUniqueId();
    final Map<UUID, ArenaCreation> arenas = manager.getArenas();
    this.pane = new PatternPane(0, 0, 9, 4, CREATE_ARENA_PATTERN);
    this.audience = this.getAudience(plugin, watcher);
    this.plugin = plugin;
    this.watcher = watcher;
    this.listenForBreaks = false;
    this.editMode = editMode;
    this.currentMode = new AtomicInteger(0);
    if (!arenas.containsKey(uuid)) {
      manager.addArena(uuid, arenaName, spawn, truck, first, second, itemLocations);
    }
    this.creation = requireNonNull(manager.getArena(uuid));
  }

  public void registerEvents() {
    final Server server = this.plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    final Collection<Location> locations = this.creation.getItemLocations();
    this.listener = new WandListener(this.plugin, locations, this::removeItemLocation, this::addItemLocation);
    this.listener.registerEvents();
    this.listener.runScheduledTask();
    manager.registerEvents(this, this.plugin);
  }

  private Audience getAudience(@UnderInitialization ArenaModificationGui this, final MurderRun plugin, final HumanEntity watcher) {
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

  public void addItemLocation(final Player sender, final Location location) {

    final Block block = location.getBlock();
    final Location blockLoc = block.getLocation();
    final Collection<Location> locations = this.creation.getItemLocations();
    locations.add(blockLoc);
    
    final Component msg = ComponentUtils.createLocationComponent(Message.ARENA_ITEM_ADD, blockLoc);
    this.audience.sendMessage(msg);
  }

  public void removeItemLocation(final Player sender, final Location location) {
    final Block block = location.getBlock();
    final Location blockLoc = block.getLocation();
    final Collection<Location> locations = this.creation.getItemLocations();
    if (locations.remove(blockLoc)) {
      final Component msg = ComponentUtils.createLocationComponent(Message.ARENA_ITEM_REMOVE, blockLoc);
      this.audience.sendMessage(msg);
    } else {
      final Component err = Message.ARENA_ITEM_REMOVE_ERROR.build();
      this.audience.sendMessage(err);
    }
  }

  private PatternPane createPane() {
    this.pane.clear();
    this.pane.bindItem('1', this.createBorderStack());
    this.pane.bindItem('2', this.createEditNameStack());
    this.pane.bindItem('3', this.createEditSpawnStack());
    this.pane.bindItem('4', this.createWandStack());
    this.pane.bindItem('5', this.createDeleteStack());
    this.pane.bindItem('6', this.createApplyStack());
    this.pane.bindItem('7', this.createCloseStack());

    return this.pane;
  }

  private void unregisterEvents(final InventoryCloseEvent event) {
    if (this.listenForBreaks || this.listenForName || this.listenForItems) {
      return;
    }

    final HandlerList list = BlockBreakEvent.getHandlerList();
    final HandlerList list1 = AsyncPlayerChatEvent.getHandlerList();
    list.unregister(this);
    list1.unregister(this);
    this.listener.unregister();
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

    if (this.listenForItems) {
      final String upper = msg.toUpperCase();
      if (upper.equals("DONE")) {
        this.listenForName = false;
        this.listenForItems = false;
        this.showAsync(player);
        return;
      }
    }

    this.creation.setArenaName(msg);
    this.listenForName = false;
    this.showAsync(player);
  }

  private void showAsync(final HumanEntity player) {
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.callSyncMethod(this.plugin, () -> {
      this.update();
      this.show(player);
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

  private GuiItem createWandStack() {
    return new GuiItem(
      Item.builder(Material.ANVIL)
        .name(Message.CREATE_ARENA_GUI_WAND_DISPLAY.build())
        .lore(Message.CREATE_ARENA_GUI_WAND_LORE.build())
        .build(),
      this::giveWandStack,
      this.plugin
    );
  }

  private void giveWandStack(final InventoryClickEvent event) {
    this.listenForName = true;
    this.listenForItems = true;
    this.watcher.closeInventory();

    final Player player = (Player) this.watcher;
    final PlayerInventory inventory = player.getInventory();
    final ItemStack stack = ItemFactory.createItemLocationWand();
    inventory.addItem(stack);

    final Component msg = Message.CREATE_ARENA_GUI_WAND.build();
    this.audience.sendMessage(msg);
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
      Item.builder(Material.GREEN_WOOL).name(Message.CREATE_ARENA_GUI_APPLY.build()).build(),
      this::createNewArena,
      this.plugin
    );
  }

  private void createNewArena(final InventoryClickEvent event) {
    final String name = this.creation.getArenaName();
    if (name.isEmpty() || name.equals("None")) {
      final Component msg = Message.ARENA_NAME_ERROR.build();
      this.audience.sendMessage(msg);
      return;
    }
    this.watcher.closeInventory();
    this.clearWands();

    final Component loadMsg = Message.ARENA_CREATE_LOAD.build();
    this.audience.sendMessage(loadMsg);

    final Location first = this.creation.getFirst();
    final Location second = this.creation.getSecond();
    final Location spawn = this.creation.getSpawn();
    final Location truck = this.creation.getTruck();
    final Collection<Location> locations = this.creation.getItemLocations();
    final Location[] corners = { first, second };
    final Location[] drops = locations.toArray(new Location[0]);

    final CompletableFuture<Location[]> future;
    if (drops.length == 0) {
      final TerrainDropAnalyzer analyzer = new TerrainDropAnalyzer(this.plugin, corners, this.spawn);
      future = analyzer.getRandomDrops();
    } else {
      future = CompletableFuture.completedFuture(drops);
    }

    future.thenAccept(items -> {
      final ArenaManager manager = this.plugin.getArenaManager();
      manager.addArena(name, corners, items, spawn, truck);
      this.plugin.updatePluginData();

      final Component msg1 = Message.ARENA_BUILT.build();
      this.audience.sendMessage(msg1);
    });
  }

  private void clearWands() {
    final Player player = (Player) this.watcher;
    final PlayerInventory inv = player.getInventory();
    final ItemStack[] contents = inv.getContents();
    for (final ItemStack item : contents) {
      if (PDCUtils.isWand(item)) {
        inv.remove(item);
      }
    }
  }

  private GuiItem createDeleteStack() {
    if (this.editMode) {
      return new GuiItem(
        Item.builder(Material.RED_WOOL).name(Message.CREATE_ARENA_GUI_DELETE.build()).build(),
        this::deleteAndCreateArena,
        this.plugin
      );
    } else {
      return this.createBorderStack();
    }
  }

  private void sendProperMessage(final Location location, final boolean skip) {
    final int current = this.currentMode.get();
    if (!skip) {
      switch (current) {
        case 0 -> this.creation.setFirst(location);
        case 1 -> this.creation.setSecond(location);
        case 2 -> this.creation.setTruck(location);
        case 3 -> this.creation.setSpawn(location);
      }
    }

    switch (current) {
      case 0 -> this.audience.sendMessage(Message.CREATE_ARENA_GUI_EDIT_SECOND.build());
      case 1 -> this.audience.sendMessage(Message.CREATE_ARENA_GUI_EDIT_TRUCK.build());
      case 2 -> this.audience.sendMessage(Message.CREATE_ARENA_GUI_EDIT_SPAWN.build());
      case 3 -> {
        this.listenForBreaks = false;
        this.listenForName = false;
        this.showAsync(this.watcher);
      }
    }

    this.currentMode.incrementAndGet();
  }

  private void deleteAndCreateArena(final InventoryClickEvent event) {
    final ArenaManager manager = this.plugin.getArenaManager();
    final String name = this.creation.getArenaName();
    manager.removeArena(name);
    this.watcher.closeInventory();
    final Component msg = Message.ARENA_REMOVE.build(name);
    this.audience.sendMessage(msg);
  }

  private GuiItem createEditSpawnStack() {
    final Location first = this.creation.getFirst();
    final Location second = this.creation.getSecond();
    final Location spawn = this.creation.getSpawn();
    final Location truck = this.creation.getTruck();
    final Component title = Message.CREATE_ARENA_GUI_EDIT_LOCATIONS_DISPLAY.build();
    final Component tooltip = Message.CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE1.build();
    final Component space = empty();
    final Component spawnMsg = ComponentUtils.createLocationComponent(Message.CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE2, spawn);
    final Component truckMsg = ComponentUtils.createLocationComponent(Message.CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE3, truck);
    final Component firstMsg = ComponentUtils.createLocationComponent(Message.CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE4, first);
    final Component secondMsg = ComponentUtils.createLocationComponent(Message.CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE5, second);
    final List<Component> lore = List.of(tooltip, space, spawnMsg, truckMsg, firstMsg, secondMsg);
    return new GuiItem(Item.builder(Material.ANVIL).name(title).lore(lore).build(), this::listenForBlockBreak, this.plugin);
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
    final String name = this.creation.getArenaName();
    return new GuiItem(
      Item.builder(Material.ANVIL)
        .name(Message.CREATE_ARENA_GUI_EDIT_NAME_DISPLAY.build(name))
        .lore(Message.CREATE_ARENA_GUI_EDIT_NAME_LORE.build())
        .build(),
      this::listenForMessage,
      this.plugin
    );
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
      event -> event.setCancelled(true),
      this.plugin
    );
  }
}
