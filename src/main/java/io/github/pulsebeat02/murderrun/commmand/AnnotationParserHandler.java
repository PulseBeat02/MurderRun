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
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public final class AnnotationParserHandler {

  private final List<AnnotationCommandFeature> features;
  private final CommandManager<CommandSender> manager;
  private final AnnotationParser<CommandSender> parser;

  public AnnotationParserHandler(final MurderRun plugin) {
    this.features = List.of(
        new MurderArenaCommand(),
        new MurderLobbyCommand(),
        new MurderHelpCommand(),
        new MurderGameCommand(),
        new MurderVillagerCommand());
    this.manager = this.getCommandManager(plugin);
    this.parser = this.getAnnotationParser();
  }

  @SuppressWarnings("nullness")
  private CommandManager<CommandSender> getCommandManager(
      @UnderInitialization AnnotationParserHandler this, final MurderRun plugin) {

    final CommandManager<CommandSender> manager = this.createBasicManager(plugin);
    final AudienceHandler handler = plugin.getAudience();
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

    final AnnotationParser<CommandSender> parser =
        new AnnotationParser<>(this.manager, CommandSender.class);
    parser.descriptionMapper(RichDescription::translatable);

    return parser;
  }

  private CommandManager<CommandSender> createBasicManager(
      @UnderInitialization AnnotationParserHandler this, final MurderRun plugin) {

    final LegacyPaperCommandManager<CommandSender> manager =
        LegacyPaperCommandManager.createNative(plugin, ExecutionCoordinator.simpleCoordinator());
    if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
      manager.registerLegacyPaperBrigadier();
    }

    return manager;
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

  public void registerCommands(final MurderRun plugin) {
    this.features.forEach(feature -> {
      feature.registerFeature(plugin, this.parser);
      this.parser.parse(feature);
    });
  }
}
