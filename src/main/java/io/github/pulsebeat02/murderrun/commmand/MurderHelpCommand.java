package io.github.pulsebeat02.murderrun.commmand;

import static org.incendo.cloud.minecraft.extras.MinecraftHelp.*;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.locale.LocaleParent;
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
import org.incendo.cloud.minecraft.extras.MinecraftHelp;

public final class MurderHelpCommand implements AnnotationCommandFeature {

  private CommandManager<CommandSender> manager;
  private MinecraftHelp<CommandSender> minecraftHelp;
  private BukkitAudiences bukkitAudiences;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceHandler handler = plugin.getAudience();
    this.bukkitAudiences = handler.retrieve();
    this.manager = parser.manager();
    this.setupHelp();
  }

  private Map<String, String> constructHelpMap() {
    final TranslationManager manager = LocaleParent.MANAGER;
    final Map<String, String> bundle = new HashMap<>();
    bundle.put(MESSAGE_HELP_TITLE, manager.getProperty("murder_run.command.game.help.command"));
    bundle.put(
        MESSAGE_DESCRIPTION, manager.getProperty("murder_run.command.game.help.description"));
    bundle.put(MESSAGE_ARGUMENTS, manager.getProperty("murder_run.command.game.help.arguments"));
    bundle.put(MESSAGE_OPTIONAL, manager.getProperty("murder_run.command.game.help.optional"));
    bundle.put(
        MESSAGE_SHOWING_RESULTS_FOR_QUERY,
        manager.getProperty("murder_run.command.game.help.search_query"));
    bundle.put(
        MESSAGE_NO_RESULTS_FOR_QUERY,
        manager.getProperty("murder_run.command.game.help.none_query"));
    bundle.put(
        MESSAGE_AVAILABLE_COMMANDS,
        manager.getProperty("murder_run.command.game.help.available_commands"));
    bundle.put(
        MESSAGE_CLICK_TO_SHOW_HELP, manager.getProperty("murder_run.command.game.help.show_help"));
    bundle.put(
        MESSAGE_PAGE_OUT_OF_RANGE,
        manager.getProperty("murder_run.command.game.help.page_invalid"));
    bundle.put(
        MESSAGE_CLICK_FOR_NEXT_PAGE, manager.getProperty("murder_run.command.game.help.next_page"));
    bundle.put(
        MESSAGE_CLICK_FOR_PREVIOUS_PAGE,
        manager.getProperty("murder_run.command.game.help.previous_page"));
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

  public void setManager(final CommandManager<CommandSender> manager) {
    this.manager = manager;
  }

  public MinecraftHelp<CommandSender> getMinecraftHelp() {
    return this.minecraftHelp;
  }

  public void setMinecraftHelp(final MinecraftHelp<CommandSender> minecraftHelp) {
    this.minecraftHelp = minecraftHelp;
  }

  public BukkitAudiences getBukkitAudiences() {
    return this.bukkitAudiences;
  }

  public void setBukkitAudiences(final BukkitAudiences bukkitAudiences) {
    this.bukkitAudiences = bukkitAudiences;
  }

  @CommandDescription("murder_run.command.help.info")
  @Command("murder help [query]")
  public void commandHelp(final CommandSender sender, @Greedy final String query) {
    this.minecraftHelp.queryCommands(query == null ? "" : query, sender);
  }
}
