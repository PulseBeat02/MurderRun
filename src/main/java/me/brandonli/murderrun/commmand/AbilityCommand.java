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

import java.util.List;
import java.util.stream.Stream;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.gui.ability.AbilityTestingGui;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.InventoryUtils;
import me.brandonli.murderrun.utils.TradingUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.*;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

public final class AbilityCommand implements AnnotationCommandFeature {

  private BukkitAudiences audiences;
  private MurderRun plugin;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider audienceProvider = plugin.getAudience();
    this.plugin = plugin;
    this.audiences = audienceProvider.retrieve();
  }

  @Command("murder ability menu")
  @Permission("murderrun.command.ability.menu")
  @CommandDescription("murderrun.command.ability.menu.info")
  public void openMenu(final Player player) {
    final AbilityTestingGui gui = new AbilityTestingGui(player);
    gui.update();
    gui.open(player);
  }

  @Command("murder ability retrieve <abilityName>")
  @Permission("murderrun.command.ability.retrieve")
  @CommandDescription("murderrun.command.ability.retrieve.info")
  public void retrieveAbility(
      final Player sender,
      @Argument(suggestions = "ability-suggestions") @Quoted final String abilityName) {
    final List<ItemStack> recipes = TradingUtils.parseAbilityRecipes(abilityName);
    final Audience audience = this.audiences.player(sender);
    if (recipes.isEmpty()) {
      audience.sendMessage(Message.ABILITY_RETRIEVE_ERROR.build());
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
  public Stream<String> suggestGadgets(
      final CommandContext<CommandSender> ctx, final String input) {
    return TradingUtils.getAbilityTradeSuggestions();
  }
}
