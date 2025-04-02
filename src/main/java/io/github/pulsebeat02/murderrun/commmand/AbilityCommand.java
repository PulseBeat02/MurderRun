/*

MIT License

Copyright (c) 2025 Brandon Li

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
import io.github.pulsebeat02.murderrun.gui.ability.AbilityTestingGui;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.InventoryUtils;
import io.github.pulsebeat02.murderrun.utils.TradingUtils;
import java.util.List;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.*;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

public final class AbilityCommand implements AnnotationCommandFeature {

  private MurderRun plugin;

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    this.plugin = plugin;
  }

  @Command("murder ability menu")
  @Permission("murderrun.command.ability.menu")
  @CommandDescription("murderrun.command.ability.menu.info")
  public void openMenu(final Player player) {
    final AbilityTestingGui gui = new AbilityTestingGui(this.plugin);
    gui.update();
    gui.showGUI(player);
  }

  @Command("murder ability retrieve <abilityName>")
  @Permission("murderrun.command.ability.retrieve")
  @CommandDescription("murderrun.command.ability.retrieve.info")
  public void retrieveAbility(final Player sender, @Argument(suggestions = "ability-suggestions") @Quoted final String abilityName) {
    final List<ItemStack> recipes = TradingUtils.parseAbilityRecipes(abilityName);
    if (recipes.isEmpty()) {
      sender.sendMessage(Message.ABILITY_RETRIEVE_ERROR.toString());
      return;
    }

    for (final ItemStack stack : recipes) {
      final ItemStack clone = stack.clone();
      InventoryUtils.addItem(sender, clone);
    }
  }

  @Command("murder ability retrieve-all")
  @Permission("murderrun.command.ability.retrieve-all")
  @CommandDescription("murderrun.command.ability.retrieve.all.info")
  public void retrieveAllAbilities(final Player sender) {
    TradingUtils.getAllAbilityRecipes().forEach(item -> InventoryUtils.addItem(sender, item));
  }

  @Suggestions("ability-suggestions")
  public Stream<String> suggestGadgets(final CommandContext<CommandSender> ctx, final String input) {
    return TradingUtils.getAbilityTradeSuggestions();
  }
}
