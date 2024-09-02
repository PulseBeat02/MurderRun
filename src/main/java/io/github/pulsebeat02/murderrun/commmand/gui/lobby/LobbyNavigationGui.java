package io.github.pulsebeat02.murderrun.commmand.gui.lobby;

import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

public final class LobbyNavigationGui extends ChestGui {

  private static final Pattern CENTRAL_GUI_PATTERN =
      new Pattern("111111111", "111314111", "111121111");

  private final MurderRun plugin;
  private final HumanEntity watcher;

  public LobbyNavigationGui(final MurderRun plugin, final HumanEntity clicker) {
    super(
        3, AdventureUtils.serializeComponentToLegacyString(Message.MANAGE_LOBBY_GUI_TITLE.build()));
    this.plugin = plugin;
    this.watcher = clicker;
  }

  public void updateItems() {
    final PatternPane pane = new PatternPane(0, 0, 9, 3, CENTRAL_GUI_PATTERN);
    pane.bindItem('1', this.createBorderStack());
    pane.bindItem('2', this.createCloseStack());
    pane.bindItem('3', this.createLobbyStack());
    pane.bindItem('4', this.createModifyStack());
    this.addPane(pane);
    this.show(this.watcher);
  }

  private GuiItem createModifyStack() {
    return new GuiItem(
        Item.builder(Material.YELLOW_BANNER)
            .name(Message.MANAGE_LOBBY_GUI_EDIT.build())
            .build(),
        event -> {
          final ChestGui gui = new LobbyListGui(this.plugin, this.watcher);
          gui.update();
          gui.show(this.watcher);
        });
  }

  private GuiItem createLobbyStack() {
    return new GuiItem(
        Item.builder(Material.GREEN_BANNER)
            .name(Message.MANAGE_LOBBY_GUI_CREATE.build())
            .build(),
        event -> {
          final ChestGui gui = new LobbyModificationGui(this.plugin, this.watcher, false, this);
          gui.update();
          gui.show(this.watcher);
        });
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
        Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
        event -> this.watcher.closeInventory());
  }

  private GuiItem createBorderStack() {
    return new GuiItem(
        Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(),
        event -> event.setCancelled(true));
  }
}
