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
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class ArenaNavigationGui extends ChestGui {

  private static final Pattern NAVIGTATION_ARENA_PATTERN =
      new Pattern("111111111", "111314111", "111111111", "111121111");

  private final MurderRun plugin;
  private final HumanEntity watcher;

  public ArenaNavigationGui(final MurderRun plugin, final HumanEntity clicker) {
    super(
        4, AdventureUtils.serializeComponentToLegacyString(Message.MANAGE_ARENA_GUI_TITLE.build()));
    this.plugin = plugin;
    this.watcher = clicker;
  }

  @Override
  public void update() {
    super.update();
    this.addPane(this.createPane());
    this.setOnGlobalClick(event -> event.setCancelled(true));
  }

  private PatternPane createPane() {
    final PatternPane pane = new PatternPane(0, 0, 9, 4, NAVIGTATION_ARENA_PATTERN);
    pane.bindItem('1', this.createBorderStack());
    pane.bindItem('2', this.createCloseStack());
    pane.bindItem('3', this.createArenaStack());
    pane.bindItem('4', this.createModifyStack());
    return pane;
  }

  private GuiItem createModifyStack() {
    return new GuiItem(
        Item.builder(Material.YELLOW_BANNER)
            .name(Message.MANAGE_ARENA_GUI_EDIT.build())
            .build(),
        this::createListingsMenu);
  }

  private void createListingsMenu(final InventoryClickEvent event) {
    final ChestGui gui = new ArenaListGui(this.plugin, this.watcher, this::handleArenaClickEvent);
    gui.update();
    gui.show(this.watcher);
  }

  private void handleArenaClickEvent(final InventoryClickEvent event) {
    final ItemStack item = requireNonNull(event.getCurrentItem());
    final ItemMeta meta = requireNonNull(item.getItemMeta());
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final String name = requireNonNull(container.get(Keys.ARENA_NAME, PersistentDataType.STRING));
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
        this.plugin,
        ArenaNavigationGui.this.watcher,
        name,
        spawn,
        truck,
        first,
        second,
        copy,
        true);
    gui.update();
    gui.show(this.watcher);
  }

  private GuiItem createArenaStack() {
    return new GuiItem(
        Item.builder(Material.GREEN_BANNER)
            .name(Message.MANAGE_ARENA_GUI_CREATE.build())
            .build(),
        this::createArenaMenu);
  }

  private void createArenaMenu(final InventoryClickEvent event) {
    final ChestGui gui = new ArenaModificationGui(this.plugin, this.watcher, false);
    gui.update();
    gui.show(this.watcher);
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
        Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
        event -> this.watcher.closeInventory());
  }

  private GuiItem createBorderStack() {
    return new GuiItem(
        Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build());
  }
}
