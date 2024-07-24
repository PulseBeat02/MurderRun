package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.List;

public final class AnnotationParserHandler {

  private final MurderRun plugin;
  private final List<AnnotationCommandFeature> features;
  private final AnnotationParser<CommandSender> parser;

  public AnnotationParserHandler(final MurderRun plugin) {
    final CommandManager<CommandSender> manager = this.getCommmandManager(plugin);
    this.plugin = plugin;
    this.features =
        List.of(
            new MurderArenaCommand(),
            new MurderLobbyCommand(),
            new MurderHelpCommand(),
            new MurderGameCommand());
    this.parser = new AnnotationParser<>(manager, CommandSender.class);
  }

  private CommandManager<CommandSender> getCommmandManager(final MurderRun plugin) {
    return LegacyPaperCommandManager.createNative(plugin, ExecutionCoordinator.simpleCoordinator());
  }

  public void registerCommands() {
    this.features.forEach(feature -> feature.registerFeature(this.plugin, this.parser));
  }
}
