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

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.gui.gadget.GadgetTestingGui;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.InventoryUtils;
import io.github.pulsebeat02.murderrun.utils.TradingUtils;
import java.util.*;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

public final class GadgetCommand implements AnnotationCommandFeature {

  private MurderRun plugin;

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    this.plugin = plugin;
  }

  @Command("murder gadget menu")
  @Permission("murderrun.command.gadget.menu")
  @CommandDescription("murderrun.command.gadget.menu.info")
  public void openMenu(final Player player) {
    final GadgetTestingGui gui = new GadgetTestingGui(this.plugin);
    gui.update();
    gui.showGUI(player);
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

    for (final MerchantRecipe recipe : recipes) {
      final ItemStack original = recipe.getResult();
      final ItemStack clone = original.clone();
      InventoryUtils.addItem(sender, clone);
    }
  }

  @Command("murder gadget retrieve-all")
  @Permission("murderrun.command.gadget.retrieve-all")
  @CommandDescription("murderrun.command.gadget.retrieve.all.info")
  public void retrieveAllGadgets(final Player sender) {
    TradingUtils.getAllRecipes().stream().map(MerchantRecipe::getResult).forEach(item -> InventoryUtils.addItem(sender, item));
  }

  @Suggestions("gadget-suggestions")
  public Stream<String> suggestGadgets(final CommandContext<CommandSender> ctx, final String input) {
    return TradingUtils.getTradeSuggestions();
  }
}
