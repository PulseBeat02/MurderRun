/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.gui.gadget;

import static net.kyori.adventure.text.Component.empty;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.components.util.GuiFiller;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import java.util.Collection;
import java.util.List;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.gadget.Gadget;
import me.brandonli.murderrun.game.gadget.GadgetRegistry;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.ComponentUtils;
import me.brandonli.murderrun.utils.InventoryUtils;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.TradingUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class GadgetTestingGui extends PaginatedGui {

  private static final Collection<ItemStack> SORTED_SURVIVOR_ITEMS = TradingUtils.getGadgetShopItems(true);
  private static final Collection<ItemStack> SORTED_KILLER_ITEMS = TradingUtils.getGadgetShopItems(false);
  private static final Iterable<ItemStack> COMBINED_GADGETS = Iterables.concat(SORTED_SURVIVOR_ITEMS, SORTED_KILLER_ITEMS);

  public static void init() {
    // copy ItemStack fields
  }

  private final MurderRun plugin;
  private final HumanEntity viewer;

  public GadgetTestingGui(final MurderRun plugin, final HumanEntity viewer) {
    super(6, 45, ComponentUtils.serializeComponentToLegacyString(Message.GADGET_GUI_TITLE.build()), InteractionModifier.VALUES);
    this.plugin = plugin;
    this.viewer = viewer;
    this.createPaginatedPane();
    this.createNavigationPane();
  }

  private void createPaginatedPane() {
    final List<ItemStack> items = Lists.newArrayList(COMBINED_GADGETS);
    items.stream().map(stack -> new GuiItem(stack, this::handleClick)).forEach(this::addItem);
  }

  private void createNavigationPane() {
    final GuiFiller filler = this.getFiller();
    final GuiItem back = this.createBackStack();
    final GuiItem next = this.createForwardStack();
    final GuiItem close = this.createCloseStack();
    final GuiItem border = this.createBorderStack();
    filler.fillBottom(border);
    this.setItem(6, 1, back);
    this.setItem(6, 9, next);
    this.setItem(6, 5, close);
  }

  private GuiItem createBorderStack() {
    return new GuiItem(Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build());
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
    final ItemStack clone = stack.clone();
    InventoryUtils.addItem(entity, clone);
  }

  private GuiItem createCloseStack() {
    return new GuiItem(Item.builder(Material.BARRIER).name(Message.GADGET_GUI_CANCEL.build()).build(), event -> this.close(this.viewer));
  }

  private GuiItem createForwardStack() {
    return new GuiItem(Item.builder(Material.GREEN_WOOL).name(Message.GADGET_GUI_FORWARD.build()).build(), event -> this.next());
  }

  private GuiItem createBackStack() {
    return new GuiItem(Item.builder(Material.RED_WOOL).name(Message.GADGET_GUI_BACK.build()).build(), event -> this.previous());
  }
}
