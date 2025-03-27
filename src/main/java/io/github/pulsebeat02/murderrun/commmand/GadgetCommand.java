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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

  // GUI配置
  private static final int ITEMS_PER_PAGE = 45;
  private static final int CONTROL_ROW_START = 45;
  private static final String GUI_TITLE = "道具选择 - 第%d/%d页";

  // 分页物品
  private static final ItemStack PREV_PAGE_ITEM = createNavItem(Material.ARROW, "上一页");
  private static final ItemStack NEXT_PAGE_ITEM = createNavItem(Material.ARROW, "下一页");
  private static final ItemStack CLOSE_ITEM = createNavItem(Material.BARRIER, "关闭菜单");
  private static final ItemStack PAGE_INFO = createNavItem(Material.BOOK, "页码");

  // 分页状态
  private final Map<UUID, Integer> pageStates = new HashMap<>();
  private MurderRun plugin;

  @Override
  public void registerFeature(MurderRun plugin, AnnotationParser<CommandSender> parser) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Command("murder gadget menu")
  @Permission("murderrun.command.gadget.menu")
  @CommandDescription("打开道具GUI")
  public void openMenu(Player player) {
    pageStates.put(player.getUniqueId(), 0);
    openPaginatedMenu(player, 0);
  }

  private void openPaginatedMenu(Player player, int page) {
    final List<MerchantRecipe> all = TradingUtils.getAllRecipes();
    final int totalPages = (int) Math.ceil(all.size() / (double) ITEMS_PER_PAGE);
    page = Math.max(0, Math.min(page, totalPages - 1));

    final String title = String.format(GUI_TITLE, page + 1, totalPages);
    final Inventory inv = Bukkit.createInventory(new GadgetHolder(), 54, title);

    // 填充物品
    final int start = page * ITEMS_PER_PAGE;
    final int end = Math.min(start + ITEMS_PER_PAGE, all.size());
    for (int i = start; i < end; i++) {
      inv.setItem(i - start, all.get(i).getResult().clone());
    }

    // 底部控制栏
    inv.setItem(49, updatePageInfo(page + 1, totalPages));
    inv.setItem(45, page > 0 ? PREV_PAGE_ITEM : null);
    inv.setItem(53, page < totalPages - 1 ? NEXT_PAGE_ITEM : null);
    inv.setItem(48, CLOSE_ITEM);

    player.openInventory(inv);
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getInventory().getHolder() instanceof GadgetHolder)) return;

    event.setCancelled(true);
    final ItemStack item = event.getCurrentItem();
    if (item == null || item.getType() == Material.AIR) return;

    final Player player = (Player) event.getWhoClicked();
    final UUID uuid = player.getUniqueId();
    int page = pageStates.getOrDefault(uuid, 0);

    switch (event.getRawSlot()) {
      case 45 -> page = Math.max(0, page - 1);
      case 53 -> page++;
      case 48 -> {
        player.closeInventory();
        return;
      }
      default -> {
        final MerchantRecipe recipe = TradingUtils.getRecipeByResult(item);
        if (recipe != null) giveItem(player, recipe.getResult());
        return;
      }
    }

    pageStates.put(uuid, page);
    openPaginatedMenu(player, page);
  }

  private void giveItem(Player player, ItemStack item) {
    final Location loc = player.getLocation();
    final World world = requireNonNull(loc.getWorld(), "World is null");
    final ItemStack clone = item.clone();
    final Map<Integer, ItemStack> leftover = player.getInventory().addItem(clone);
    leftover.values().forEach(left -> world.dropItemNaturally(loc, left));
  }

  private static ItemStack updatePageInfo(int current, int total) {
    final ItemStack clone = PAGE_INFO.clone();
    final ItemMeta meta = clone.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(ChatColor.GOLD + "当前: 第 " + current + "/" + total + " 页");
    }
    clone.setItemMeta(meta);
    return clone;
  }

  private static ItemStack createNavItem(Material material, String name) {
    final ItemStack item = new ItemStack(material);
    final ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(ChatColor.GREEN + name);
    }
    item.setItemMeta(meta);
    return item;
  }

  public MurderRun getPlugin() {
    return plugin;
  }

  private static class GadgetHolder implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
      return Bukkit.createInventory(null, 54);
    }
  }

  @Command("murder gadget retrieve <gadgetName>")
  @Permission("murderrun.command.gadget.retrieve")
  @CommandDescription("获取指定道具")
  public void retrieveGadget(Player sender, @Argument(suggestions = "gadget-suggestions") @Quoted String gadgetName) {
    final List<MerchantRecipe> recipes = TradingUtils.parseRecipes(gadgetName);
    if (recipes.isEmpty()) {
      sender.sendMessage(Message.GADGET_RETRIEVE_ERROR.toString());
      return;
    }

    giveItem(sender, recipes.getFirst().getResult());
  }

  @Command("murder gadget retrieve-all")
  @Permission("murderrun.command.gadget.retrieve-all")
  @CommandDescription("获取全部道具")
  public void retrieveAllGadgets(Player sender) {
    TradingUtils.getAllRecipes().stream().map(MerchantRecipe::getResult).forEach(item -> giveItem(sender, item));
  }

  @Suggestions("gadget-suggestions")
  public Stream<String> suggestGadgets(CommandContext<CommandSender> ctx, String input) {
    return TradingUtils.getTradeSuggestions();
  }
}
