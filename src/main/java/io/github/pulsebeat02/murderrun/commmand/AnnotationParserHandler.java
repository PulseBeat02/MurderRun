package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.List;

@SuppressWarnings("nullness")
public final class AnnotationParserHandler {

  private final MurderRun plugin;
  private final List<AnnotationCommandFeature> features;
  private final CommandManager<CommandSender> manager;
  private final AnnotationParser<CommandSender> parser;

  public AnnotationParserHandler(final MurderRun plugin) {
    this.plugin = plugin;
    this.features = List.of(
        new MurderArenaCommand(),
        new MurderLobbyCommand(),
        new MurderHelpCommand(),
        new MurderGameCommand(),
        new MurderVillagerCommand());
    this.manager = this.getCommandManager();
    this.parser = this.getAnnotationParser();
  }

  private CommandManager<CommandSender> getCommandManager(
      @UnderInitialization AnnotationParserHandler this) {

    if (this.plugin == null) {
      throw new AssertionError("MurderRun has been unloaded!");
    }

    final CommandManager<CommandSender> manager = this.createBasicManager();
    final AudienceHandler handler = this.plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    MinecraftExceptionHandler.create(audiences::sender)
        .defaultHandlers()
        .handler(InvalidCommandSenderException.class, (sender, e) -> Locale.NOT_PLAYER.build())
        .registerTo(manager);

    return manager;
  }

  private AnnotationParser<CommandSender> getAnnotationParser(
      @UnderInitialization AnnotationParserHandler this) {

    if (this.manager == null) {
      throw new AssertionError("Annotation command manager is null!");
    }

    if (this.plugin == null) {
      throw new AssertionError("MurderRun has been unloaded!");
    }

    final AnnotationParser<CommandSender> parser =
        new AnnotationParser<>(this.manager, CommandSender.class);
    parser.descriptionMapper(RichDescription::translatable);

    return parser;
  }

  private CommandManager<CommandSender> createBasicManager(
      @UnderInitialization AnnotationParserHandler this) {

    if (this.plugin == null) {
      throw new AssertionError("MurderRun has been unloaded!");
    }

    final LegacyPaperCommandManager<CommandSender> manager = LegacyPaperCommandManager.createNative(
        this.plugin, ExecutionCoordinator.simpleCoordinator());
    manager.registerBrigadier();

    return manager;
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
    this.features.forEach(feature -> {
      feature.registerFeature(this.plugin, this.parser);
      this.parser.parse(feature);
    });
  }
}
