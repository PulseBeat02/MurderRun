package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.List;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public final class AnnotationParserHandler {

  private final MurderRun plugin;
  private final CommandManager<CommandSender> manager;
  private final List<AnnotationCommandFeature> features;
  private final AnnotationParser<CommandSender> parser;

  public AnnotationParserHandler(final MurderRun plugin) {
    this.manager = this.getCommandManager(plugin);
    this.plugin = plugin;
    this.features =
        List.of(
            new MurderArenaCommand(),
            new MurderLobbyCommand(),
            new MurderHelpCommand(),
            new MurderGameCommand());
    this.parser = this.getAnnotationParser();
  }

  private LegacyPaperCommandManager<CommandSender> getCommandManager(final MurderRun plugin) {
    final LegacyPaperCommandManager<CommandSender> manager =
        LegacyPaperCommandManager.createNative(plugin, ExecutionCoordinator.simpleCoordinator());
    final AudienceHandler handler = this.plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    MinecraftExceptionHandler.create(audiences::sender)
        .defaultInvalidSenderHandler()
        .decorator(message -> Locale.NOT_PLAYER.build())
        .registerTo(manager);
    manager.registerBrigadier();
    return manager;
  }

  private AnnotationParser<CommandSender> getAnnotationParser() {
    final AnnotationParser<CommandSender> parser =
        new AnnotationParser<>(this.manager, CommandSender.class);
    parser.descriptionMapper(RichDescription::translatable);
    return parser;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public CommandManager<CommandSender> getManager() {
    return this.manager;
  }

  public List<AnnotationCommandFeature> getFeatures() {
    return this.features;
  }

  public AnnotationParser<CommandSender> getParser() {
    return this.parser;
  }

  public void registerCommands() {
    this.features.forEach(feature -> feature.registerFeature(this.plugin, this.parser));
  }
}
