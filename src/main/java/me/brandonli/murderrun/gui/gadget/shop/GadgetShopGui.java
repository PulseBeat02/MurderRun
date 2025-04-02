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
package me.brandonli.murderrun.gui.gadget.shop;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.text.Component.empty;

import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.components.util.GuiFiller;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.Gadget;
import me.brandonli.murderrun.game.gadget.GadgetRegistry;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.ComponentUtils;
import me.brandonli.murderrun.utils.InventoryUtils;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.TradingUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.item.ItemFactory;
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

public final class GadgetShopGui extends PaginatedGui {

  private final MurderRun plugin;
  private final HumanEntity viewer;

  public GadgetShopGui(final MurderRun plugin, final HumanEntity viewer, final List<String> gadgets) {
    super(6, 45, ComponentUtils.serializeComponentToLegacyString(Message.SHOP_GUI_TITLE.build()), InteractionModifier.VALUES);
    this.plugin = plugin;
    this.viewer = viewer;
    this.createPaginatedPane(gadgets);
    this.createNavigationPane();
  }

  @SuppressWarnings("all") // checker
  private void createPaginatedPane(final List<String> gadgets) {
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    gadgets
      .stream()
      .map(registry::getGadget)
      .filter(Objects::nonNull)
      .map(TradingUtils::getModifiedLoreWithCost)
      .map(stack -> new GuiItem(stack, this::handleClick))
      .forEach(this::addItem);
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

    final Item.Builder actual = gadget.getStackBuilder();
    final ItemStack actualStack = actual.build();
    final ItemStack clone = actualStack.clone();
    inventory.removeItem(currency);
    InventoryUtils.addItem(entity, clone);
  }

  private GuiItem createCloseStack() {
    return new GuiItem(Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(), event -> this.close(this.viewer));
  }

  private GuiItem createForwardStack() {
    return new GuiItem(Item.builder(Material.GREEN_WOOL).name(Message.SHOP_GUI_FORWARD.build()).build(), event -> this.next());
  }

  private GuiItem createBackStack() {
    return new GuiItem(Item.builder(Material.RED_WOOL).name(Message.SHOP_GUI_BACK.build()).build(), event -> this.previous());
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
}
