/*

MIT License

Copyright (c) 2025 Brandon Li

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
package me.brandonli.murderrun.gui.ability;

import static net.kyori.adventure.text.Component.empty;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.components.util.GuiFiller;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import java.util.Collection;
import java.util.List;
import me.brandonli.murderrun.game.ability.Ability;
import me.brandonli.murderrun.game.ability.AbilityRegistry;
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

public final class AbilityTestingGui extends PaginatedGui {

  private static final Collection<ItemStack> SORTED_SURVIVOR_ITEMS = TradingUtils.getAbilityShopItems(true);
  private static final Collection<ItemStack> SORTED_KILLER_ITEMS = TradingUtils.getAbilityShopItems(false);
  private static final Iterable<ItemStack> COMBINED_ABILITIES = Iterables.concat(SORTED_SURVIVOR_ITEMS, SORTED_KILLER_ITEMS);

  public static void init() {
    // copy ItemStack fields
  }

  private final HumanEntity viewer;

  public AbilityTestingGui(final HumanEntity viewer) {
    super(6, 45, ComponentUtils.serializeComponentToLegacyString(Message.ABILITY_GUI_TITLE.build()), InteractionModifier.VALUES);
    this.viewer = viewer;
    this.createPaginatedPane();
    this.createNavigationPane();
  }

  private void createPaginatedPane() {
    final List<ItemStack> items = Lists.newArrayList(COMBINED_ABILITIES);
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
    final AbilityRegistry registry = AbilityRegistry.getRegistry();
    if (stack == null) {
      return;
    }

    final String data = PDCUtils.getPersistentDataAttribute(stack, Keys.ABILITY_KEY_NAME, PersistentDataType.STRING);
    final Ability ability = data != null ? registry.getAbility(data) : null;
    if (ability == null) {
      return;
    }

    final HumanEntity entity = event.getWhoClicked();
    final ItemStack clone = stack.clone();
    InventoryUtils.addItem(entity, clone);
  }

  private GuiItem createCloseStack() {
    return new GuiItem(Item.builder(Material.BARRIER).name(Message.ABILITY_GUI_CANCEL.build()).build(), event -> this.close(this.viewer));
  }

  private GuiItem createForwardStack() {
    return new GuiItem(Item.builder(Material.GREEN_WOOL).name(Message.ABILITY_GUI_FORWARD.build()).build(), event -> this.next());
  }

  private GuiItem createBackStack() {
    return new GuiItem(Item.builder(Material.RED_WOOL).name(Message.ABILITY_GUI_BACK.build()).build(), event -> this.previous());
  }
}
