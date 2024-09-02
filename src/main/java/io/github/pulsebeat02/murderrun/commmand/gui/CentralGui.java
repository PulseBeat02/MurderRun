package io.github.pulsebeat02.murderrun.commmand.gui;

import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.commmand.gui.lobby.LobbyNavigationGui;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

public final class CentralGui {

  /*

  - Central GUI
    - Manage Arenas
      - Create an arena
      - Edit an arena
        - Choose an arena to edit

    - Manage Lobbies
      - Create a lobby (done)
      - Edit a lobby
        - Choose a lobby to edit (done)

   */

  private static final Pattern CENTRAL_GUI_PATTERN =
      new Pattern("111111111", "100345001", "111121111");

  private final MurderRun plugin;
  private final ChestGui gui;
  private final HumanEntity watcher;

  public CentralGui(final MurderRun plugin, final HumanEntity watcher) {
    final Component component = Message.CENTRAL_GUI_TITLE.build();
    final String legacy = AdventureUtils.serializeComponentToLegacyString(component);
    this.plugin = plugin;
    this.gui = new ChestGui(3, legacy);
    this.watcher = watcher;
  }

  public void updateItems() {

    final PatternPane pane = new PatternPane(0, 0, 9, 3, CENTRAL_GUI_PATTERN);

    final GuiItem outer = new GuiItem(
        Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(),
        event -> event.setCancelled(true));
    pane.bindItem('1', outer);

    final GuiItem close = new GuiItem(
        Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(), event -> {
          final HumanEntity clicker = event.getWhoClicked();
          clicker.closeInventory();
          event.setCancelled(true);
        });
    pane.bindItem('2', close);

    final GuiItem lobby = new GuiItem(
        Item.builder(Material.WHITE_BANNER)
            .name(Message.CENTRAL_GUI_LOBBY.build())
            .build(),
        event -> {
          final HumanEntity clicker = event.getWhoClicked();
          final LobbyNavigationGui gui = new LobbyNavigationGui(this.plugin, clicker);
          gui.updateItems();
          event.setCancelled(true);
        });
    pane.bindItem('3', lobby);
    pane.bindItem('4', new GuiItem(Item.AIR_STACK));
    pane.bindItem('5', new GuiItem(Item.AIR_STACK));

    this.gui.addPane(pane);
    this.gui.show(this.watcher);
  }
}
