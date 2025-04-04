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
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.TradingUtils;
import java.util.List;
import java.util.stream.Stream;
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
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

public final class DebugCommand implements AnnotationCommandFeature {

  private BukkitAudiences audiences;

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
  }

  @Permission("murderrun.command.debug.start")
  @Command(value = "murder debug start", requiredSender = Player.class)
  public void startDebugGame(final Player sender) {
    final Player other = requireNonNull(Bukkit.getPlayer("Player1"));
    sender.performCommand("party create Party");
    sender.performCommand("party invite Player1");
    other.performCommand("party accept");
    sender.performCommand("murder game party Arena Lobby");
  }

  @Permission("murderrun.command.debug.start-multiple")
  @Command(value = "murder debug start-multiple", requiredSender = Player.class)
  public void startMultipleDebugGame(final Player sender) {
    final Player other = requireNonNull(Bukkit.getPlayer("Player1"));
    final Player other1 = requireNonNull(Bukkit.getPlayer("Player2"));
    sender.performCommand("party create Party");
    sender.performCommand("party invite Player1");
    sender.performCommand("party invite Player2");
    other.performCommand("party accept");
    other1.performCommand("party accept");
    sender.performCommand("murder game party Arena Lobby");
  }

  @Permission("murderrun.command.debug.gadget")
  @Command(value = "murder debug gadget <gadgetName>", requiredSender = Player.class)
  public void debugGadget(final Player sender, @Argument(suggestions = "gadget-suggestions") @Quoted final String gadgetName) {
    final Audience audience = this.audiences.player(sender);
    final List<MerchantRecipe> allGadgets = TradingUtils.parseRecipes(gadgetName);
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
    return TradingUtils.getTradeSuggestions();
  }
}
