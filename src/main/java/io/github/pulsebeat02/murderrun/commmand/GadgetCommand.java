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
package io.github.pulsebeat02.murderrun.commmand;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.TradingUtils;
import java.util.*;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

public final class GadgetCommand implements AnnotationCommandFeature, Listener {

  private static final int ITEMS_PER_PAGE = 45;
  private static final int CONTROL_ROW_START = 45;
  private static final String GUI_TITLE = "Page %d/%d";

  private static final ItemStack PREV_PAGE_ITEM = createNavItem(Material.ARROW, "Previous Page");
  private static final ItemStack NEXT_PAGE_ITEM = createNavItem(Material.ARROW, "Next Page");
  private static final ItemStack CLOSE_ITEM = createNavItem(Material.BARRIER, "Close Menu");
  private static final ItemStack PAGE_INFO = createNavItem(Material.BOOK, "Page Info");

  private final Map<UUID, Integer> pageStates = new HashMap<>();

  private MurderRun plugin;

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final PluginManager manager = Bukkit.getPluginManager();
    this.plugin = plugin;
    manager.registerEvents(this, plugin);
  }

  @Command("murder gadget menu")
  @Permission("murderrun.command.gadget.menu")
  public void openMenu(final Player player) {
    final UUID uuid = player.getUniqueId();
    this.pageStates.put(uuid, 0);
    this.openPaginatedMenu(player, 0);
  }

  private void openPaginatedMenu(final Player player, int page) {
    final List<MerchantRecipe> allRecipes = TradingUtils.getAllRecipes();
    final int size = allRecipes.size();
    final int totalPages = (int) Math.ceil(size / (double) ITEMS_PER_PAGE);
    page = Math.max(0, Math.min(page, totalPages - 1));

    final String title = String.format(GUI_TITLE, page + 1, totalPages);
    final GadgetHolder holder = new GadgetHolder();
    final Inventory inventory = Bukkit.createInventory(holder, 54, title);

    final int start = page * ITEMS_PER_PAGE;
    final int end = Math.min(start + ITEMS_PER_PAGE, size);
    for (int i = start; i < end; i++) {
      final MerchantRecipe recipe = allRecipes.get(i);
      final ItemStack stack = recipe.getResult();
      final ItemStack clone = stack.clone();
      inventory.setItem(i - start, clone);
    }

    final ItemStack pageInfo = updatePageInfo(page + 1, totalPages);
    final ItemStack prevPageItem = page > 0 ? PREV_PAGE_ITEM : null;
    final ItemStack nextPageItem = page < totalPages - 1 ? NEXT_PAGE_ITEM : null;

    inventory.setItem(49, pageInfo);
    inventory.setItem(45, prevPageItem);
    inventory.setItem(53, nextPageItem);
    inventory.setItem(48, CLOSE_ITEM);

    player.openInventory(inventory);
  }

  @EventHandler
  public void onInventoryClick(final InventoryClickEvent event) {
    final Inventory inventory = event.getInventory();
    final InventoryHolder holder = inventory.getHolder();
    if (!(holder instanceof GadgetHolder)) {
      return;
    }

    event.setCancelled(true);

    final ItemStack item = event.getCurrentItem();
    if (item == null || item.getType() == Material.AIR) {
      return;
    }

    final Player player = (Player) event.getWhoClicked();
    final UUID uuid = player.getUniqueId();
    int page = this.pageStates.getOrDefault(uuid, 0);

    switch (event.getRawSlot()) {
      case 45 -> page = Math.max(0, page - 1);
      case 53 -> page++;
      case 48 -> {
        player.closeInventory();
        return;
      }
      default -> {
        final Optional<MerchantRecipe> optional = TradingUtils.getRecipeByResult(item);
        optional.ifPresent(recipe -> {
          final ItemStack result = recipe.getResult();
          this.giveItem(player, result);
        });
        return;
      }
    }

    this.pageStates.put(uuid, page);
    this.openPaginatedMenu(player, page);
  }

  private void giveItem(final Player player, final ItemStack item) {
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final ItemStack clonedItem = item.clone();
    final Map<Integer, ItemStack> leftoverItems = player.getInventory().addItem(clonedItem);
    leftoverItems.values().forEach(leftover -> world.dropItemNaturally(location, leftover));
  }

  private static ItemStack updatePageInfo(final int current, final int total) {
    final ItemStack clonedPageInfo = PAGE_INFO.clone();
    final ItemMeta meta = requireNonNull(clonedPageInfo.getItemMeta());
    meta.setDisplayName(ChatColor.GOLD + "Current: Page " + current + "/" + total);
    clonedPageInfo.setItemMeta(meta);
    return clonedPageInfo;
  }

  private static ItemStack createNavItem(final Material material, final String name) {
    final ItemStack item = new ItemStack(material);
    final ItemMeta meta = requireNonNull(item.getItemMeta());
    meta.setDisplayName(ChatColor.GREEN + name);
    item.setItemMeta(meta);
    return item;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  private static class GadgetHolder implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
      return Bukkit.createInventory(null, 54);
    }
  }

  @Command("murder gadget retrieve <gadgetName>")
  @Permission("murderrun.command.gadget.retrieve")
  @CommandDescription("murderrun.command.gadget.retrieve.info")
  public void retrieveGadget(final Player sender, @Argument(suggestions = "gadget-suggestions") @Quoted final String gadgetName) {
    final List<MerchantRecipe> recipes = TradingUtils.parseRecipes(gadgetName);
    if (recipes.isEmpty()) {
      sender.sendMessage(Message.GADGET_RETRIEVE_ERROR.toString());
      return;
    }

    this.giveItem(sender, recipes.getFirst().getResult());
  }

  @Command("murder gadget retrieve-all")
  @Permission("murderrun.command.gadget.retrieve-all")
  @CommandDescription("murderrun.command.gadget.retrieve.all.info")
  public void retrieveAllGadgets(final Player sender) {
    TradingUtils.getAllRecipes().stream().map(MerchantRecipe::getResult).forEach(item -> this.giveItem(sender, item));
  }

  @Suggestions("gadget-suggestions")
  public Stream<String> suggestGadgets(final CommandContext<CommandSender> ctx, final String input) {
    return TradingUtils.getTradeSuggestions();
  }
}
