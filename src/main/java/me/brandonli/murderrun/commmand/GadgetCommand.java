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

import java.util.*;
import java.util.stream.Stream;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.gui.gadget.GadgetTestingGui;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.InventoryUtils;
import me.brandonli.murderrun.utils.TradingUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
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

  private BukkitAudiences audiences;
  private MurderRun plugin;

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider audienceProvider = plugin.getAudience();
    this.plugin = plugin;
    this.audiences = audienceProvider.retrieve();
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
    final List<MerchantRecipe> recipes = TradingUtils.parseGadgetRecipes(gadgetName);
    final Audience audience = this.audiences.player(sender);
    if (recipes.isEmpty()) {
      audience.sendMessage(Message.GADGET_RETRIEVE_ERROR.build());
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
    TradingUtils.getAllGadgetRecipes().stream().map(MerchantRecipe::getResult).forEach(item -> InventoryUtils.addItem(sender, item));
  }

  @Suggestions("gadget-suggestions")
  public Stream<String> suggestGadgets(final CommandContext<CommandSender> ctx, final String input) {
    return TradingUtils.getGadgetTradeSuggestions();
  }
}
