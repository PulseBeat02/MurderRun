package io.github.pulsebeat02.murderrun.gui.lobby;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class LobbyNavigationGui extends ChestGui {

  private static final Pattern NAVIGATION_LOBBY_PATTERN =
      new Pattern("111111111", "111314111", "111111111", "111121111");

  private final MurderRun plugin;
  private final HumanEntity watcher;

  public LobbyNavigationGui(final MurderRun plugin, final HumanEntity clicker) {
    super(
        4,
        AdventureUtils.serializeComponentToLegacyString(Message.MANAGE_LOBBY_GUI_TITLE.build()),
        plugin);
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
    final PatternPane pane = new PatternPane(0, 0, 9, 4, NAVIGATION_LOBBY_PATTERN);
    pane.bindItem('1', this.createBorderStack());
    pane.bindItem('2', this.createCloseStack());
    pane.bindItem('3', this.createLobbyStack());
    pane.bindItem('4', this.createModifyStack());
    return pane;
  }

  private GuiItem createModifyStack() {
    return new GuiItem(
        Item.builder(Material.YELLOW_BANNER)
            .name(Message.MANAGE_LOBBY_GUI_EDIT.build())
            .build(),
        this::createListingsMenu,
        this.plugin);
  }

  private void createListingsMenu(final InventoryClickEvent event) {
    final ChestGui gui = new LobbyListGui(this.plugin, this.watcher, this::handleLobbyClickEvent);
    gui.update();
    gui.show(this.watcher);
  }

  public void handleLobbyClickEvent(final InventoryClickEvent event) {

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

  private GuiItem createLobbyStack() {
    return new GuiItem(
        Item.builder(Material.GREEN_BANNER)
            .name(Message.MANAGE_LOBBY_GUI_CREATE.build())
            .build(),
        this::createLobbyMenu,
        this.plugin);
  }

  private void createLobbyMenu(final InventoryClickEvent event) {
    final ChestGui gui = new LobbyModificationGui(this.plugin, this.watcher, false);
    gui.update();
    gui.show(this.watcher);
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
        Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
        event -> this.watcher.closeInventory(),
        this.plugin);
  }

  private GuiItem createBorderStack() {
    return new GuiItem(
        Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(), this.plugin);
  }
}
