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
package io.github.pulsebeat02.murderrun.gui.ability.selection;

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
import io.github.pulsebeat02.murderrun.game.ability.Ability;
import io.github.pulsebeat02.murderrun.game.ability.AbilityRegistry;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ComponentUtils;
import io.github.pulsebeat02.murderrun.utils.InventoryUtils;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.TradingUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

public final class AbilitySelectGui extends ChestGui {

  private static final Collection<ItemStack> SORTED_SURVIVOR_ITEMS = TradingUtils.getAbilityShopItems(true);
  private static final Collection<ItemStack> SORTED_KILLER_ITEMS = TradingUtils.getAbilityShopItems(false);

  public static void init() {
    // copy ItemStack fields
  }

  private final MurderRun plugin;
  private final PaginatedPane pages;

  public AbilitySelectGui(final MurderRun plugin, final boolean isSurvivorAbilities) {
    super(6, ComponentUtils.serializeComponentToLegacyString(Message.SELECT_GUI_TITLE.build()), plugin);
    this.plugin = plugin;
    this.pages = new PaginatedPane(0, 0, 9, 5);
    this.addItems(isSurvivorAbilities);
    this.setOnGlobalClick(event -> {
        final HumanEntity entity = event.getWhoClicked();
        event.setCancelled(true);
        this.playSound(entity);
      });
  }

  private void addItems(final boolean isSurvivorAbilities) {
    this.addPane(this.createPaginatedPane(isSurvivorAbilities));
    this.addPane(this.createBackgroundPane());
    this.addPane(this.createNavigationPane());
  }

  private PaginatedPane createPaginatedPane(final boolean isSurvivorAbilities) {
    final Collection<ItemStack> raw = isSurvivorAbilities ? SORTED_SURVIVOR_ITEMS : SORTED_KILLER_ITEMS;
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
    final PlayerInventory inventory = entity.getInventory();
    final ItemStack[] contents = inventory.getContents();
    for (final ItemStack content : contents) {
      if (!PDCUtils.isAbility(content)) {
        continue;
      }
      inventory.removeItem(content);
    }

    final Item.Builder actual = ability.getStackBuilder();
    final ItemStack actualStack = actual.build();
    final ItemStack clone = actualStack.clone();
    InventoryUtils.addItem(entity, clone);
  }

  private GuiItem createCloseStack() {
    return new GuiItem(Item.builder(Material.BARRIER).name(Message.SELECT_GUI_CANCEL.build()).build(), this::handleClose, this.plugin);
  }

  private void handleClose(final InventoryClickEvent event) {
    final HumanEntity entity = event.getWhoClicked();
    entity.closeInventory();
  }

  private GuiItem createForwardStack() {
    return new GuiItem(
      Item.builder(Material.GREEN_WOOL).name(Message.SELECT_GUI_FORWARD.build()).build(),
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
      Item.builder(Material.RED_WOOL).name(Message.SELECT_GUI_BACK.build()).build(),
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
    final String raw = GameProperties.ABILITY_GUI_SOUND;
    final Key key = key(raw);
    final Sound.Source source = Sound.Source.MASTER;
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
