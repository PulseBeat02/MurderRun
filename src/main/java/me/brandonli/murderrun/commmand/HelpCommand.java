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

import static org.incendo.cloud.minecraft.extras.MinecraftHelp.*;

import java.util.HashMap;
import java.util.Map;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.LocaleTools;
import me.brandonli.murderrun.locale.TranslationManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;

public final class HelpCommand implements AnnotationCommandFeature {

  private CommandManager<CommandSender> manager;
  private MinecraftHelp<CommandSender> minecraftHelp;
  private BukkitAudiences bukkitAudiences;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.bukkitAudiences = handler.retrieve();
    this.manager = parser.manager();
    this.setupHelp();
  }

  private Map<String, String> constructHelpMap() {
    final TranslationManager manager = LocaleTools.MANAGER;
    final Map<String, String> bundle = new HashMap<>();
    bundle.put(MESSAGE_HELP_TITLE, manager.getProperty("murderrun.command.game.help.command"));
    bundle.put(MESSAGE_DESCRIPTION, manager.getProperty("murderrun.command.game.help.description"));
    bundle.put(MESSAGE_ARGUMENTS, manager.getProperty("murderrun.command.game.help.arguments"));
    bundle.put(MESSAGE_OPTIONAL, manager.getProperty("murderrun.command.game.help.optional"));
    bundle.put(
        MESSAGE_SHOWING_RESULTS_FOR_QUERY,
        manager.getProperty("murderrun.command.game.help.search_query"));
    bundle.put(
        MESSAGE_NO_RESULTS_FOR_QUERY,
        manager.getProperty("murderrun.command.game.help.none_query"));
    bundle.put(
        MESSAGE_AVAILABLE_COMMANDS,
        manager.getProperty("murderrun.command.game.help.available_commands"));
    bundle.put(
        MESSAGE_CLICK_TO_SHOW_HELP, manager.getProperty("murderrun.command.game.help.show_help"));
    bundle.put(
        MESSAGE_PAGE_OUT_OF_RANGE, manager.getProperty("murderrun.command.game.help.page_invalid"));
    bundle.put(
        MESSAGE_CLICK_FOR_NEXT_PAGE, manager.getProperty("murderrun.command.game.help.next_page"));
    bundle.put(
        MESSAGE_CLICK_FOR_PREVIOUS_PAGE,
        manager.getProperty("murderrun.command.game.help.previous_page"));
    return bundle;
  }

  private void setupHelp() {
    this.minecraftHelp = MinecraftHelp.<CommandSender>builder()
        .commandManager(this.manager)
        .audienceProvider(this.bukkitAudiences::sender)
        .commandPrefix("/murder help")
        .messages(this.constructHelpMap())
        .build();
  }

  public CommandManager<CommandSender> getManager() {
    return this.manager;
  }

  public MinecraftHelp<CommandSender> getMinecraftHelp() {
    return this.minecraftHelp;
  }

  public BukkitAudiences getBukkitAudiences() {
    return this.bukkitAudiences;
  }

  @Permission("murderrun.command.help")
  @CommandDescription("murderrun.command.help.info")
  @Command("murder help [query]")
  public void commandHelp(final CommandSender sender, @Greedy final String query) {
    this.minecraftHelp.queryCommands(query == null ? "" : query, sender);
  }
}
