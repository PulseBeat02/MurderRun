package io.github.pulsebeat02.murderrun.gui;

import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.gui.arena.ArenaNavigationGui;
import io.github.pulsebeat02.murderrun.gui.game.GameCreationGui;
import io.github.pulsebeat02.murderrun.gui.lobby.LobbyNavigationGui;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class CentralGui extends ChestGui {

  private static final Pattern CENTRAL_GUI_PATTERN =
      new Pattern("111111111", "111345111", "111111111", "111121111");

  private final MurderRun plugin;
  private final HumanEntity watcher;

  public CentralGui(final MurderRun plugin, final HumanEntity watcher) {
    super(4, AdventureUtils.serializeComponentToLegacyString(Message.CENTRAL_GUI_TITLE.build()));
    this.plugin = plugin;
    this.watcher = watcher;
  }

  @Override
  public void update() {
    super.update();
    this.addPane(this.createPane());
    this.setOnGlobalClick(event -> event.setCancelled(true));
  }

  private PatternPane createPane() {
    final PatternPane pane = new PatternPane(0, 0, 9, 4, CENTRAL_GUI_PATTERN);
    pane.bindItem('1', this.createBackground());
    pane.bindItem('2', this.createCloseButton());
    pane.bindItem('3', this.createLobbyButton());
    pane.bindItem('4', this.createArenaButton());
    pane.bindItem('5', this.createGameButton());
    return pane;
  }

  private GuiItem createBackground() {
    return new GuiItem(
        Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build());
  }

  private GuiItem createCloseButton() {
    return new GuiItem(
        Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
        event -> this.watcher.closeInventory());
  }

  private GuiItem createGameButton() {
    return new GuiItem(
        Item.builder(Material.RED_BANNER).name(Message.CENTRAL_GUI_GAME.build()).build(),
        this::handleGameClick);
  }

  private GuiItem createArenaButton() {
    return new GuiItem(
        Item.builder(Material.YELLOW_BANNER)
            .name(Message.CENTRAL_GUI_ARENA.build())
            .build(),
        this::handleArenaClick);
  }

  private void handleGameClick(final InventoryClickEvent event) {
    final ChestGui gui = new GameCreationGui(this.plugin, this.watcher);
    gui.update();
    gui.show(this.watcher);
  }

  private void handleArenaClick(final InventoryClickEvent event) {
    final ChestGui gui = new ArenaNavigationGui(this.plugin, this.watcher);
    gui.update();
    gui.show(this.watcher);
  }

  private GuiItem createLobbyButton() {
    return new GuiItem(
        Item.builder(Material.WHITE_BANNER)
            .name(Message.CENTRAL_GUI_LOBBY.build())
            .build(),
        event -> this.handleLobbyClick());
  }

  private void handleLobbyClick() {
    final ChestGui gui = new LobbyNavigationGui(this.plugin, this.watcher);
    gui.update();
    gui.show(this.watcher);
  }
}
