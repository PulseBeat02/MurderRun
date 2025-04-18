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
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
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
    bundle.put(MESSAGE_SHOWING_RESULTS_FOR_QUERY, manager.getProperty("murderrun.command.game.help.search_query"));
    bundle.put(MESSAGE_NO_RESULTS_FOR_QUERY, manager.getProperty("murderrun.command.game.help.none_query"));
    bundle.put(MESSAGE_AVAILABLE_COMMANDS, manager.getProperty("murderrun.command.game.help.available_commands"));
    bundle.put(MESSAGE_CLICK_TO_SHOW_HELP, manager.getProperty("murderrun.command.game.help.show_help"));
    bundle.put(MESSAGE_PAGE_OUT_OF_RANGE, manager.getProperty("murderrun.command.game.help.page_invalid"));
    bundle.put(MESSAGE_CLICK_FOR_NEXT_PAGE, manager.getProperty("murderrun.command.game.help.next_page"));
    bundle.put(MESSAGE_CLICK_FOR_PREVIOUS_PAGE, manager.getProperty("murderrun.command.game.help.previous_page"));
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
