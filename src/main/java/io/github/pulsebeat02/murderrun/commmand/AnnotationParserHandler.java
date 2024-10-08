package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public final class AnnotationParserHandler {

  private final CommandManager<CommandSender> manager;
  private final AnnotationParser<CommandSender> parser;
  private final MurderRun plugin;

  public AnnotationParserHandler(final MurderRun plugin) {
    this.plugin = plugin;
    this.manager = this.getCommandManager(plugin);
    this.parser = this.getAnnotationParser(this.manager);
  }

  private AnnotationParser<CommandSender> getAnnotationParser(
    @UnderInitialization AnnotationParserHandler this,
    final CommandManager<CommandSender> manager
  ) {
    final Class<CommandSender> sender = CommandSender.class;
    final AnnotationParser<CommandSender> parser = new AnnotationParser<>(manager, sender);
    parser.descriptionMapper(RichDescription::translatable);
    return parser;
  }

  private CommandManager<CommandSender> getCommandManager(@UnderInitialization AnnotationParserHandler this, final MurderRun plugin) {
    final ExecutionCoordinator<CommandSender> coordinator = ExecutionCoordinator.simpleCoordinator();
    final LegacyPaperCommandManager<CommandSender> manager = LegacyPaperCommandManager.createNative(plugin, coordinator);
    this.registerBrigadierCapability(manager);
    return manager;
  }

  private void registerBrigadierCapability(
    @UnderInitialization AnnotationParserHandler this,
    final LegacyPaperCommandManager<CommandSender> manager
  ) {
    if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
      manager.registerBrigadier();
    }
  }

  public CommandManager<CommandSender> getManager() {
    return this.manager;
  }

  public void registerCommands() {
    final List<AnnotationCommandFeature> features = Commands.getFeatures();
    for (final AnnotationCommandFeature feature : features) {
      feature.registerFeature(this.plugin, this.parser);
      this.parser.parse(feature);
    }
  }
}
