package io.github.pulsebeat02.murderrun.gui.game;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.gui.arena.ArenaListGui;
import io.github.pulsebeat02.murderrun.gui.lobby.LobbyListGui;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class GameCreationGui extends ChestGui {

  private static final Pattern CREATE_GAME_PATTERN =
      new Pattern("111111111", "123111141", "111111111", "111151111");

  private final MurderRun plugin;
  private final HumanEntity watcher;
  private final PatternPane pane;

  private volatile Lobby lobby;
  private volatile Arena arena;

  public GameCreationGui(final MurderRun plugin, final HumanEntity watcher) {
    super(
        4, AdventureUtils.serializeComponentToLegacyString(Message.CREATE_GAME_GUI_TITLE.build()));
    this.plugin = plugin;
    this.watcher = watcher;
    this.pane = new PatternPane(0, 0, 9, 4, CREATE_GAME_PATTERN);
  }

  @Override
  public void update() {
    super.update();
    this.addPane(this.createPane());
    this.setOnGlobalClick(event -> event.setCancelled(true));
  }

  private PatternPane createPane() {

    this.pane.clear();
    this.pane.bindItem('1', this.createBorderStack());
    this.pane.bindItem('2', this.createLobbyStack());
    this.pane.bindItem('3', this.createArenaStack());
    this.pane.bindItem('4', this.createApplyStack());
    this.pane.bindItem('5', this.createCloseStack());

    return this.pane;
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
        Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(),
        event -> this.watcher.closeInventory());
  }

  private GuiItem createApplyStack() {
    return new GuiItem(
        Item.builder(Material.GREEN_WOOL)
            .name(Message.CREATE_GAME_GUI_APPLY.build())
            .build(),
        this::createNewGame);
  }

  private void createNewGame(final InventoryClickEvent event) {

    final AudienceProvider provider = this.plugin.getAudience();
    final BukkitAudiences bukkitAudiences = provider.retrieve();
    final Audience audience = bukkitAudiences.player(this.watcher.getUniqueId());
    if (this.lobby == null || this.arena == null) {
      final Component msg = Message.CREATE_GAME_GUI_ERROR.build();
      audience.sendMessage(msg);
      return;
    }

    final Player player = (Player) this.watcher;
    final String lobbyName = this.lobby.getName();
    final String arenaName = this.arena.getName();
    player.performCommand("murder game create %s %s".formatted(arenaName, lobbyName));
    this.watcher.closeInventory();
  }

  private GuiItem createArenaStack() {
    final String name = this.arena == null ? "" : this.arena.getName();
    return new GuiItem(
        Item.builder(Material.ANVIL)
            .name(Message.CREATE_GAME_GUI_ARENA_DISPLAY.build(name))
            .lore(Message.CREATE_GAME_GUI_ARENA_LORE.build())
            .build(),
        this::chooseArena);
  }

  private GuiItem createLobbyStack() {
    final String name = this.lobby == null ? "" : this.lobby.getName();
    return new GuiItem(
        Item.builder(Material.ANVIL)
            .name(Message.CREATE_GAME_GUI_LOBBY_DISPLAY.build(name))
            .lore(Message.CREATE_GAME_GUI_LOBBY_LORE.build())
            .build(),
        this::chooseLobby);
  }

  private void chooseLobby(final InventoryClickEvent event) {
    final ChestGui gui = new LobbyListGui(this.plugin, this.watcher, this::handleLobbyClickEvent);
    gui.update();
    gui.show(this.watcher);
  }

  private void handleLobbyClickEvent(final InventoryClickEvent event) {
    final ItemStack item = requireNonNull(event.getCurrentItem());
    final ItemMeta meta = requireNonNull(item.getItemMeta());
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final String name = requireNonNull(container.get(Keys.LOBBY_NAME, PersistentDataType.STRING));
    final LobbyManager manager = this.plugin.getLobbyManager();
    this.lobby = requireNonNull(manager.getLobby(name));
    this.update();
    this.show(this.watcher);
  }

  private void chooseArena(final InventoryClickEvent event) {
    final ChestGui gui = new ArenaListGui(this.plugin, this.watcher, this::handleArenaClickEvent);
    gui.update();
    gui.show(this.watcher);
  }

  private void handleArenaClickEvent(final InventoryClickEvent event) {
    final ItemStack item = requireNonNull(event.getCurrentItem());
    final ItemMeta meta = requireNonNull(item.getItemMeta());
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final String name = requireNonNull(container.get(Keys.ARENA_NAME, PersistentDataType.STRING));
    final ArenaManager manager = this.plugin.getArenaManager();
    this.arena = requireNonNull(manager.getArena(name));
    this.update();
    this.show(this.watcher);
  }

  private GuiItem createBorderStack() {
    return new GuiItem(
        Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build(),
        event -> event.setCancelled(true));
  }
}
