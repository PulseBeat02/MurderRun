package io.github.pulsebeat02.murderrun.commmand.gui.arena;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class ArenaListGui extends ChestGui {

  private final MurderRun plugin;
  private final HumanEntity watcher;
  private final PaginatedPane pages;

  public ArenaListGui(final MurderRun plugin, final HumanEntity watcher) {
    super(
        6, AdventureUtils.serializeComponentToLegacyString(Message.CHOOSE_ARENA_GUI_TITLE.build()));
    this.plugin = plugin;
    this.watcher = watcher;
    this.pages = new PaginatedPane(0, 0, 9, 3);
  }

  @Override
  public void update() {
    super.update();
    this.addPane(this.updatePane());
    this.addPane(this.createBackgroundPane());
    this.addPane(this.createNavigationPane());
    this.setOnGlobalClick(event -> event.setCancelled(true));
  }

  private PaginatedPane updatePane() {

    this.pages.clear();
    this.pages.populateWithItemStacks(this.getArenas());
    this.pages.setOnClick(this::handleArenaItemClick);

    return this.pages;
  }

  private void handleArenaItemClick(final InventoryClickEvent event) {

    final ItemStack item = event.getCurrentItem();
    if (item == null) {
      return;
    }

    final ItemMeta meta = item.getItemMeta();
    if (meta == null) {
      return;
    }

    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final String name = container.get(Keys.ARENA_NAME, PersistentDataType.STRING);
    if (name == null) {
      return;
    }

    final ArenaManager manager = this.plugin.getArenaManager();
    final Arena arena = requireNonNull(manager.getArena(name));
    final Location spawn = arena.getSpawn();
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final Location truck = arena.getTruck();
    final Location[] items = arena.getCarPartLocations();
    final Collection<Location> locations = Arrays.asList(items);
    final Collection<Location> copy = new ArrayList<>(locations);
    final ChestGui gui = new ArenaModificationGui(
        this.plugin, this.watcher, name, spawn, truck, first, second, copy, true);
    gui.update();
    gui.show(this.watcher);
  }

  private OutlinePane createBackgroundPane() {
    final OutlinePane background = new OutlinePane(0, 5, 9, 1);
    final GuiItem border =
        new GuiItem(Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build());
    background.addItem(border);
    background.setRepeat(true);
    background.setPriority(Pane.Priority.LOWEST);
    return background;
  }

  private StaticPane createNavigationPane() {
    final StaticPane navigation = new StaticPane(0, 5, 9, 1);
    navigation.addItem(this.createBackStack(), 0, 0);
    navigation.addItem(this.createForwardStack(), 8, 0);
    navigation.addItem(this.createCloseStack(), 4, 0);
    return navigation;
  }

  private List<ItemStack> getArenas() {
    final ArenaManager manager = this.plugin.getArenaManager();
    final Map<String, Arena> arenas = manager.getArenas();
    final List<ItemStack> items = new ArrayList<>();
    for (final Entry<String, Arena> entry : arenas.entrySet()) {
      final String name = entry.getKey();
      final Arena arena = entry.getValue();
      final Location spawn = arena.getSpawn();
      final ItemStack item = this.constructArenaItem(name, spawn);
      items.add(item);
    }
    return items;
  }

  private ItemStack constructArenaItem(final String name, final Location spawn) {
    final Component title = Message.CHOOSE_ARENA_GUI_ARENA_DISPLAY.build(name);
    final Component lore =
        AdventureUtils.createLocationComponent(Message.CHOOSE_ARENA_GUI_ARENA_LORE, spawn);
    return Item.builder(Material.WHITE_BANNER)
        .name(title)
        .lore(lore)
        .pdc(Keys.ARENA_NAME, PersistentDataType.STRING, name)
        .build();
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
        Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
        event -> this.watcher.closeInventory());
  }

  private GuiItem createForwardStack() {
    return new GuiItem(
        Item.builder(Material.GREEN_WOOL).name(Message.SHOP_GUI_FORWARD.build()).build(),
        this::handleForwardPage);
  }

  private void handleForwardPage(final InventoryClickEvent event) {
    final int current = this.pages.getPage();
    final int max = this.pages.getPages() - 1;
    if (current < max) {
      this.pages.setPage(current + 1);
      this.update();
    }
  }

  private GuiItem createBackStack() {
    return new GuiItem(
        Item.builder(Material.RED_WOOL).name(Message.SHOP_GUI_BACK.build()).build(),
        this::handleBackPage);
  }

  private void handleBackPage(final InventoryClickEvent event) {
    final int current = this.pages.getPage();
    if (current > 0) {
      this.pages.setPage(current - 1);
      this.update();
    }
  }
}
