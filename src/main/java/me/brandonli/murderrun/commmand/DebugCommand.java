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
package me.brandonli.murderrun.commmand;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.TradingUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.scheduler.BukkitScheduler;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

public final class DebugCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.plugin = plugin;
  }

  @Permission("murderrun.command.debug.start")
  @Command(value = "murder debug start", requiredSender = Player.class)
  public void startDebugGame(final Player sender) {
    if (!MurderRun.isDevelopmentToolsEnabled()) {
      return;
    }
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    final Player other = requireNonNull(Bukkit.getPlayer("Player1"));
    sender.performCommand("murder game quick-join");
    scheduler.runTaskLater(
      this.plugin,
      () -> {
        other.performCommand("murder game quick-join");
        sender.performCommand("murder game set murderer %s".formatted(sender.getName()));
        sender.performCommand("murder game start");
      },
      5 * 20L
    );
  }

  @Permission("murderrun.command.debug.start-multiple")
  @Command(value = "murder debug start-multiple", requiredSender = Player.class)
  public void startMultipleDebugGame(final Player sender) {
    if (!MurderRun.isDevelopmentToolsEnabled()) {
      return;
    }
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    final Player other = requireNonNull(Bukkit.getPlayer("Player1"));
    final Player other1 = requireNonNull(Bukkit.getPlayer("Player2"));
    sender.performCommand("murder game quick-join");
    scheduler.runTaskLater(
      this.plugin,
      () -> {
        other.performCommand("murder game quick-join");
        other1.performCommand("murder game quick-join");
        sender.performCommand("murder game set murderer %s".formatted(sender.getName()));
        sender.performCommand("murder game start");
      },
      5 * 20L
    );
  }

  @Permission("murderrun.command.debug.gadget")
  @Command(value = "murder debug gadget <gadgetName>", requiredSender = Player.class)
  public void debugGadget(final Player sender, @Argument(suggestions = "gadget-suggestions") @Quoted final String gadgetName) {
    if (!MurderRun.isDevelopmentToolsEnabled()) {
      return;
    }
    final Audience audience = this.audiences.player(sender);
    final List<MerchantRecipe> allGadgets = TradingUtils.parseGadgetRecipes(gadgetName);
    if (this.checkIfInvalidGadget(audience, allGadgets)) {
      return;
    }

    final MerchantRecipe recipe = allGadgets.getFirst();
    final ItemStack result = recipe.getResult();
    final Location location = sender.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.dropItem(location, result);
  }

  public boolean checkIfInvalidGadget(final Audience audience, final List<MerchantRecipe> recipes) {
    if (recipes.isEmpty()) {
      final Component msg = Message.GADGET_RETRIEVE_ERROR.build();
      audience.sendMessage(msg);
      return true;
    }
    return false;
  }

  @Suggestions("gadget-suggestions")
  public Stream<String> suggestTrades(final CommandContext<CommandSender> context, final String input) {
    return TradingUtils.getGadgetTradeSuggestions();
  }
}
