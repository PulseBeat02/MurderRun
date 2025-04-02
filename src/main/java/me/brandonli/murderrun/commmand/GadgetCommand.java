/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
    final GadgetTestingGui gui = new GadgetTestingGui(this.plugin, player);
    gui.update();
    gui.open(player);
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
