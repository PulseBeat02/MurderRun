package io.github.pulsebeat02.murderrun.gui.game;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import io.github.pulsebeat02.murderrun.game.lobby.PreGameManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
import org.incendo.cloud.type.tuple.Triplet;

public final class PlayerListGui extends ChestGui {

  private final HumanEntity watcher;
  private final Map<Player, Triplet<PreGameManager, Boolean, Boolean>> games;

  private PaginatedPane pages;

  public PlayerListGui(
      final HumanEntity watcher,
      final Map<Player, Triplet<PreGameManager, Boolean, Boolean>> games) {
    super(
        6, AdventureUtils.serializeComponentToLegacyString(Message.CHOOSE_LOBBY_GUI_TITLE.build()));
    this.watcher = watcher;
    this.games = games;
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

    if (this.pages != null) {
      this.pages.clear();
    }

    this.pages = new PaginatedPane(0, 0, 9, 3);
    this.pages.populateWithItemStacks(this.getPlayerStacks());
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

    final Triplet<PreGameManager, Boolean, Boolean> triplet = this.games.get(player);
    final String name = player.getName();
    final Player owner = (Player) this.watcher;
    if (triplet == null) {
      owner.performCommand("murder game invite %s".formatted(name));
      return;
    }

    final PreGameManager manager = triplet.first();
    final Collection<Player> killers = manager.getMurderers();
    final String phrase = killers.contains(player) ? "innocent" : "murderer";
    owner.performCommand("murder game set %s %s".formatted(phrase, name));
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

  private List<ItemStack> getPlayerStacks() {
    final Collection<? extends Player> online = Bukkit.getOnlinePlayers();
    final List<ItemStack> items = new ArrayList<>();
    for (final Player player : online) {
      final Triplet<PreGameManager, Boolean, Boolean> triplet = this.games.get(player);
      final ItemStack stack =
          triplet == null ? this.createNormalStack(player) : this.getInGameStack(player, triplet);
      items.add(stack);
    }
    return items;
  }

  private ItemStack getInGameStack(
      final Player player, final Triplet<PreGameManager, Boolean, Boolean> triplet) {
    final ItemStack stack;
    final PreGameManager manager = triplet.first();
    final Collection<Player> killers = manager.getMurderers();
    if (killers.contains(player)) {
      stack = this.createKillerStack(player);
    } else {
      stack = this.createSurvivorStack(player);
    }
    return stack;
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
