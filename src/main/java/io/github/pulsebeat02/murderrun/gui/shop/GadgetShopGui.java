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
package io.github.pulsebeat02.murderrun.gui.shop;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetRegistry;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ComponentUtils;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.TradingUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

public final class GadgetShopGui extends ChestGui {

  private static final Collection<ItemStack> SORTED_SURVIVOR_ITEMS = TradingUtils.getShopItems(true);
  private static final Collection<ItemStack> SORTED_KILLER_ITEMS = TradingUtils.getShopItems(false);

  public static void init() {
    // copy ItemStack fields
  }

  private final MurderRun plugin;
  private final PaginatedPane pages;

  public GadgetShopGui(final MurderRun plugin, final boolean isSurvivorGadgets) {
    super(6, ComponentUtils.serializeComponentToLegacyString(Message.SHOP_GUI_TITLE.build()), plugin);
    this.plugin = plugin;
    this.pages = new PaginatedPane(0, 0, 9, 5);
    this.addItems(isSurvivorGadgets);
    this.setOnGlobalClick(event -> {
        final HumanEntity entity = event.getWhoClicked();
        event.setCancelled(true);
        this.playSound(entity);
      });
  }

  private void addItems(final boolean isSurvivorGadgets) {
    this.addPane(this.createPaginatedPane(isSurvivorGadgets));
    this.addPane(this.createBackgroundPane());
    this.addPane(this.createNavigationPane());
  }

  private PaginatedPane createPaginatedPane(final boolean isSurvivorGadgets) {
    final Collection<ItemStack> raw = isSurvivorGadgets ? SORTED_SURVIVOR_ITEMS : SORTED_KILLER_ITEMS;
    final List<ItemStack> items = List.copyOf(raw);
    this.pages.populateWithItemStacks(items, this.plugin);
    this.pages.setOnClick(this::handleClick);
    return this.pages;
  }

  private OutlinePane createBackgroundPane() {
    final OutlinePane background = new OutlinePane(0, 5, 9, 1);
    final GuiItem borderCopy = this.createBorderStack();
    background.addItem(borderCopy);
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

  private GuiItem createBorderStack() {
    return new GuiItem(Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(), this.plugin);
  }

  private void handleClick(final InventoryClickEvent event) {
    final ItemStack stack = event.getCurrentItem();
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    if (stack == null) {
      return;
    }

    final String data = PDCUtils.getPersistentDataAttribute(stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING);
    final Gadget gadget = data != null ? registry.getGadget(data) : null;
    if (gadget == null) {
      return;
    }

    final HumanEntity entity = event.getWhoClicked();
    final PlayerInventory inventory = entity.getInventory();
    final int cost = gadget.getPrice();
    final ItemStack currency = ItemFactory.createCurrency(cost);
    if (!inventory.containsAtLeast(currency, cost)) {
      final UUID uuid = entity.getUniqueId();
      final Component message = Message.SHOP_GUI_ERROR.build();
      final AudienceProvider provider = this.plugin.getAudience();
      final BukkitAudiences bukkitAudiences = provider.retrieve();
      final Audience audience = bukkitAudiences.player(uuid);
      audience.sendMessage(message);
      return;
    }

    final ItemStack actual = gadget.getGadget();
    inventory.removeItem(currency);
    inventory.addItem(actual);
  }

  private GuiItem createCloseStack() {
    return new GuiItem(Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(), this::handleClose, this.plugin);
  }

  private void handleClose(final InventoryClickEvent event) {
    final HumanEntity entity = event.getWhoClicked();
    entity.closeInventory();
  }

  private GuiItem createForwardStack() {
    return new GuiItem(
      Item.builder(Material.GREEN_WOOL).name(Message.SHOP_GUI_FORWARD.build()).build(),
      this::handleForwardOption,
      this.plugin
    );
  }

  private void handleForwardOption(final InventoryClickEvent event) {
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
      this::handleBackwardOption,
      this.plugin
    );
  }

  private void handleBackwardOption(final InventoryClickEvent event) {
    final int current = this.pages.getPage();
    if (current > 0) {
      this.pages.setPage(current - 1);
      this.update();
    }
  }

  private void playSound(final HumanEntity entity) {
    final String raw = GameProperties.SHOP_GUI_SOUND;
    final Key key = key(raw);
    final Source source = Source.MASTER;
    final Sound sound = sound(key, source, 1.0f, 1.0f);
    final UUID uuid = entity.getUniqueId();
    final AudienceProvider provider = this.plugin.getAudience();
    final BukkitAudiences bukkitAudiences = provider.retrieve();
    final Audience audience = bukkitAudiences.player(uuid);
    audience.playSound(sound);
  }

  public void showGUI(final HumanEntity entity) {
    this.show(entity);
  }
}
