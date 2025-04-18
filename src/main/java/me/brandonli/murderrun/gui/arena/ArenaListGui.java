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
package me.brandonli.murderrun.gui.arena;

import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.arena.ArenaManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.ComponentUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.Item;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class ArenaListGui extends ChestGui {

  private final MurderRun plugin;
  private final HumanEntity watcher;
  private final PaginatedPane pages;
  private final Consumer<InventoryClickEvent> consumer;

  public ArenaListGui(final MurderRun plugin, final HumanEntity watcher, final Consumer<InventoryClickEvent> consumer) {
    super(6, ComponentUtils.serializeComponentToLegacyString(Message.CHOOSE_ARENA_GUI_TITLE.build()), plugin);
    this.plugin = plugin;
    this.watcher = watcher;
    this.consumer = consumer;
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
    this.pages.populateWithItemStacks(this.getArenas(), this.plugin);
    this.pages.setOnClick(this.consumer);

    return this.pages;
  }

  private OutlinePane createBackgroundPane() {
    final GuiItem border = new GuiItem(Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(), this.plugin);
    final OutlinePane background = new OutlinePane(0, 5, 9, 1);
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
    final Component lore = ComponentUtils.createLocationComponent(Message.CHOOSE_ARENA_GUI_ARENA_LORE, spawn);
    return Item.builder(Material.WHITE_BANNER).name(title).lore(lore).pdc(Keys.ARENA_NAME, PersistentDataType.STRING, name).build();
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
      Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
      event -> this.watcher.closeInventory(),
      this.plugin
    );
  }

  private GuiItem createForwardStack() {
    return new GuiItem(
      Item.builder(Material.GREEN_WOOL).name(Message.SHOP_GUI_FORWARD.build()).build(),
      this::handleForwardPage,
      this.plugin
    );
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
    return new GuiItem(Item.builder(Material.RED_WOOL).name(Message.SHOP_GUI_BACK.build()).build(), this::handleBackPage, this.plugin);
  }

  private void handleBackPage(final InventoryClickEvent event) {
    final int current = this.pages.getPage();
    if (current > 0) {
      this.pages.setPage(current - 1);
      this.update();
    }
  }
}
