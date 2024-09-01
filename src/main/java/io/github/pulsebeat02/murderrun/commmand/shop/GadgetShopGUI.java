package io.github.pulsebeat02.murderrun.commmand.shop;

import static java.util.Objects.requireNonNull;
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
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GlobalGadgetRegistry;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerApparatus;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorApparatus;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.ArrayList;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class GadgetShopGUI {

  private static final GuiItem OUTLINE_STACK =
      new GuiItem(Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build());

  private static final GuiItem BACK_STACK = new GuiItem(
      Item.builder(Material.RED_WOOL).name(Message.SHOP_GUI_BACK.build()).build());

  private static final GuiItem FORWARD_STACK = new GuiItem(
      Item.builder(Material.GREEN_WOOL).name(Message.SHOP_GUI_FORWARD.build()).build());

  private static final GuiItem CLOSE_STACK = new GuiItem(
      Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build());

  private final MurderRun plugin;
  private final ChestGui gui;

  public GadgetShopGUI(final MurderRun plugin, final boolean isSurvivorGadgets) {
    final Component comp = Message.SHOP_GUI_TITLE.build();
    final String legacy = AdventureUtils.serializeComponentToLegacyString(comp);
    this.plugin = plugin;
    this.gui = new ChestGui(6, legacy);
    this.addItems(isSurvivorGadgets);
  }

  private List<ItemStack> getShopItems(final boolean isSurvivorGadgets) {
    final GlobalGadgetRegistry registry = GlobalGadgetRegistry.getRegistry();
    final Collection<Gadget> gadgets = registry.getGadgets();
    final List<ItemStack> items = new ArrayList<>();
    for (final Gadget gadget : gadgets) {
      if (isSurvivorGadgets) {
        if (gadget instanceof SurvivorApparatus) {
          final ItemStack stack = this.getModifiedLoreWithCost(gadget);
          items.add(stack);
        }
      } else {
        if (gadget instanceof KillerApparatus) {
          final ItemStack stack = this.getModifiedLoreWithCost(gadget);
          items.add(stack);
        }
      }
    }
    return items;
  }

  private ItemStack getModifiedLoreWithCost(final Gadget gadget) {

    final ItemStack stack = gadget.getGadget();
    final ItemStack clone = stack.clone();
    final ItemMeta meta = requireNonNull(clone.getItemMeta());
    List<String> lore = meta.getLore();
    if (lore == null) {
      lore = new ArrayList<>();
    }

    final List<String> copy = new ArrayList<>(lore);
    final String raw = this.getLegacyComponent(gadget);
    copy.addFirst("");
    copy.addFirst(raw);
    meta.setLore(copy);
    clone.setItemMeta(meta);

    return clone;
  }

  private String getLegacyComponent(final Gadget gadget) {
    final int cost = gadget.getPrice();
    final Component full = Message.SHOP_GUI_COST_LORE.build(cost);
    return AdventureUtils.serializeComponentToLegacyString(full);
  }

  private void addItems(final boolean isSurvivorGadgets) {

    final List<ItemStack> items = this.getShopItems(isSurvivorGadgets);
    final PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);
    pages.populateWithItemStacks(items);
    pages.setOnClick(this::handleClick);
    this.gui.addPane(pages);

    final OutlinePane background = new OutlinePane(0, 5, 9, 1);
    final GuiItem borderCopy = OUTLINE_STACK.copy();
    borderCopy.setAction(event -> event.setCancelled(true));
    background.addItem(borderCopy);
    background.setRepeat(true);
    background.setPriority(Pane.Priority.LOWEST);
    this.gui.addPane(background);

    final StaticPane navigation = new StaticPane(0, 5, 9, 1);
    this.addBackOption(pages, navigation);
    this.addForwardOption(pages, navigation);
    this.addCloseOption(navigation);
    this.gui.addPane(navigation);
  }

  private void handleClick(final InventoryClickEvent event) {

    event.setCancelled(true);

    final ItemStack stack = event.getCurrentItem();
    final GlobalGadgetRegistry registry = GlobalGadgetRegistry.getRegistry();
    if (stack == null) {
      return;
    }

    final String data =
        PDCUtils.getPersistentDataAttribute(stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING);
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

    this.playSound(entity);
  }

  private void addCloseOption(final StaticPane navigation) {
    final GuiItem closeCopy = CLOSE_STACK.copy();
    closeCopy.setAction(event -> {
      final HumanEntity entity = event.getWhoClicked();
      entity.closeInventory();
      event.setCancelled(true);
      this.playSound(entity);
    });
    navigation.addItem(closeCopy, 4, 0);
  }

  private void addForwardOption(final PaginatedPane pages, final StaticPane navigation) {
    final GuiItem forwardCopy = FORWARD_STACK.copy();
    forwardCopy.setAction(event -> {
      final HumanEntity entity = event.getWhoClicked();
      final int current = pages.getPage();
      final int max = pages.getPages() - 1;
      if (current < max) {
        pages.setPage(current + 1);
        this.gui.update();
      }
      event.setCancelled(true);
      this.playSound(entity);
    });
    navigation.addItem(forwardCopy, 8, 0);
  }

  private void addBackOption(final PaginatedPane pages, final StaticPane navigation) {
    final GuiItem backCopy = BACK_STACK.copy();
    backCopy.setAction(event -> {
      final HumanEntity entity = event.getWhoClicked();
      final int current = pages.getPage();
      if (current > 0) {
        pages.setPage(current - 1);
        this.gui.update();
      }
      event.setCancelled(true);
      this.playSound(entity);
    });
    navigation.addItem(backCopy, 0, 0);
  }

  private void playSound(final HumanEntity entity) {
    final Key key = key("block.note_block.xylophone");
    final Source source = Source.MASTER;
    final Sound sound = sound(key, source, 1.0f, 1.0f);
    final UUID uuid = entity.getUniqueId();
    final AudienceProvider provider = this.plugin.getAudience();
    final BukkitAudiences bukkitAudiences = provider.retrieve();
    final Audience audience = bukkitAudiences.player(uuid);
    audience.playSound(sound);
  }

  public void showGUI(final HumanEntity entity) {
    this.gui.show(entity);
  }
}
