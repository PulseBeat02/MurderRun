package io.github.pulsebeat02.murderrun.commmand;

import static org.incendo.cloud.minecraft.extras.MinecraftHelp.*;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.LocaleTools;
import io.github.pulsebeat02.murderrun.locale.TranslationManager;
import java.util.HashMap;
import java.util.Map;
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
    bundle.put(
        MESSAGE_DESCRIPTION, manager.getProperty("murderrun.command.game.help.description"));
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
        MESSAGE_PAGE_OUT_OF_RANGE,
        manager.getProperty("murderrun.command.game.help.page_invalid"));
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
