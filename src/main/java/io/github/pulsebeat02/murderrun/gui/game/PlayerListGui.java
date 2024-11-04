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
package io.github.pulsebeat02.murderrun.gui.game;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.lobby.GameManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGameManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGamePlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ComponentUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class PlayerListGui extends ChestGui {

  private final HumanEntity watcher;
  private final GameManager manager;

  private PaginatedPane pages;

  public PlayerListGui(final MurderRun plugin, final HumanEntity watcher, final GameManager manager) {
    super(6, ComponentUtils.serializeComponentToLegacyString(Message.CHOOSE_LOBBY_GUI_TITLE.build()), plugin);
    this.pages = new PaginatedPane(0, 0, 9, 3);
    this.watcher = watcher;
    this.manager = manager;
  }

  @Override
  public void update() {
    super.update();
    this.addPane(this.updatePane());
    this.addPane(this.createBackgroundPane());
    this.addPane(this.createNavigationPane());
    this.setOnGlobalClick(event -> event.setCancelled(true));
  }

  public PaginatedPane updatePane() {
    this.pages.clear();
    this.pages = new PaginatedPane(0, 0, 9, 3);
    this.pages.populateWithItemStacks(this.getPlayerStacks(), this.plugin);
    this.pages.setOnClick(this::handlePlayerClick);

    return this.pages;
  }

  private void handlePlayerClick(final InventoryClickEvent event) {
    final ItemStack item = event.getCurrentItem();
    if (item == null) {
      return;
    }

    final ItemMeta meta = requireNonNull(item.getItemMeta());
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final String raw = requireNonNull(container.get(Keys.PLAYER_UUID, PersistentDataType.STRING));
    final UUID uuid = UUID.fromString(raw);
    final Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      return;
    }

    final PreGameManager manager = this.manager.getGame(player);
    final String name = player.getName();
    final Player owner = (Player) this.watcher;
    if (manager == null) {
      owner.performCommand("murder game invite %s".formatted(name));
      return;
    }

    final PreGamePlayerManager playerManager = manager.getPlayerManager();
    final Collection<Player> killers = playerManager.getMurderers();
    final String phrase = killers.contains(player) ? "innocent" : "murderer";
    owner.performCommand("murder game set %s %s".formatted(phrase, name));
  }

  private OutlinePane createBackgroundPane() {
    final OutlinePane background = new OutlinePane(0, 5, 9, 1);
    final GuiItem border = new GuiItem(Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(), this.plugin);
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

  private List<ItemStack> getPlayerStacks() {
    final Collection<? extends Player> online = Bukkit.getOnlinePlayers();
    final List<ItemStack> items = new ArrayList<>();
    for (final Player player : online) {
      final PreGameManager preGameManager = this.manager.getGame(player);
      final ItemStack stack = preGameManager == null ? this.createNormalStack(player) : this.getInGameStack(player, preGameManager);
      items.add(stack);
    }
    return items;
  }

  private ItemStack getInGameStack(final Player player, final PreGameManager triplet) {
    final PreGamePlayerManager manager = triplet.getPlayerManager();
    final Collection<Player> killers = manager.getMurderers();
    return killers.contains(player) ? this.createKillerStack(player) : this.createSurvivorStack(player);
  }

  private ItemStack createNormalStack(final Player player) {
    final String name = player.getDisplayName();
    final UUID uuid = player.getUniqueId();
    final String raw = uuid.toString();
    return Item.builder(Material.PLAYER_HEAD)
      .name(Message.INVITE_PLAYER_GUI_DISPLAY.build(name))
      .lore(Message.INVITE_PLAYER_GUI_LORE_NORMAL.build())
      .pdc(Keys.PLAYER_UUID, PersistentDataType.STRING, raw)
      .head(player)
      .build();
  }

  private ItemStack createKillerStack(final Player player) {
    final String name = player.getDisplayName();
    final UUID uuid = player.getUniqueId();
    final String raw = uuid.toString();
    return Item.builder(Material.PLAYER_HEAD)
      .name(Message.INVITE_PLAYER_GUI_KILLER.build(name))
      .lore(Message.INVITE_PLAYER_GUI_LORE.build())
      .pdc(Keys.PLAYER_UUID, PersistentDataType.STRING, raw)
      .head(player)
      .build();
  }

  private ItemStack createSurvivorStack(final Player player) {
    final String name = player.getDisplayName();
    final UUID uuid = player.getUniqueId();
    final String raw = uuid.toString();
    return Item.builder(Material.PLAYER_HEAD)
      .name(Message.INVITE_PLAYER_GUI_SURVIVOR.build(name))
      .lore(Message.INVITE_PLAYER_GUI_LORE.build())
      .pdc(Keys.PLAYER_UUID, PersistentDataType.STRING, raw)
      .head(player)
      .build();
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
      Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
      event -> this.watcher.closeInventory(),
      this.plugin
    );
  }

  private GuiItem createForwardStack() {
    return new GuiItem(
      Item.builder(Material.GREEN_WOOL).name(Message.SHOP_GUI_FORWARD.build()).build(),
      this::handleForwardPage,
      this.plugin
    );
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
    return new GuiItem(Item.builder(Material.RED_WOOL).name(Message.SHOP_GUI_BACK.build()).build(), this::handleBackPage, this.plugin);
  }

  private void handleBackPage(final InventoryClickEvent event) {
    final int current = this.pages.getPage();
    if (current > 0) {
      this.pages.setPage(current - 1);
      this.update();
    }
  }
}
