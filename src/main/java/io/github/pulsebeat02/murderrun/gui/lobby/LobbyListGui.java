package io.github.pulsebeat02.murderrun.gui.lobby;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.ArrayList;
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

public final class LobbyListGui extends ChestGui {

  private final MurderRun plugin;
  private final HumanEntity watcher;

  private PaginatedPane pages;

  public LobbyListGui(final MurderRun plugin, final HumanEntity watcher) {
    super(
        6, AdventureUtils.serializeComponentToLegacyString(Message.CHOOSE_LOBBY_GUI_TITLE.build()));
    this.plugin = plugin;
    this.watcher = watcher;
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

    if (this.pages != null) {
      this.pages.clear();
    }

    this.pages = new PaginatedPane(0, 0, 9, 3);
    this.pages.populateWithItemStacks(this.getLobbies());
    this.pages.setOnClick(this::handleLobbyItemClick);

    return this.pages;
  }

  private void handleLobbyItemClick(final InventoryClickEvent event) {

    final ItemStack item = event.getCurrentItem();
    if (item == null) {
      return;
    }

    final ItemMeta meta = item.getItemMeta();
    if (meta == null) {
      return;
    }

    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final String name = container.get(Keys.LOBBY_NAME, PersistentDataType.STRING);
    if (name == null) {
      return;
    }

    final LobbyManager manager = this.plugin.getLobbyManager();
    final Lobby lobby = requireNonNull(manager.getLobby(name));
    final Location spawn = lobby.getLobbySpawn();
    final ChestGui gui = new LobbyModificationGui(this.plugin, this.watcher, name, spawn, true);
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

  private List<ItemStack> getLobbies() {
    final LobbyManager manager = this.plugin.getLobbyManager();
    final Map<String, Lobby> lobbies = manager.getLobbies();
    final List<ItemStack> items = new ArrayList<>();
    for (final Entry<String, Lobby> entry : lobbies.entrySet()) {
      final String name = entry.getKey();
      final Lobby lobby = entry.getValue();
      final Location spawn = lobby.getLobbySpawn();
      final ItemStack item = this.constructLobbyItem(name, spawn);
      items.add(item);
    }
    return items;
  }

  private ItemStack constructLobbyItem(final String name, final Location spawn) {
    final Component title = Message.CHOOSE_LOBBY_GUI_LOBBY_DISPLAY.build(name);
    final Component lore =
        AdventureUtils.createLocationComponent(Message.CHOOSE_LOBBY_GUI_LOBBY_LORE, spawn);
    return Item.builder(Material.WHITE_BANNER)
        .name(title)
        .lore(lore)
        .pdc(Keys.LOBBY_NAME, PersistentDataType.STRING, name)
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
