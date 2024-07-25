package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.List;

public final class AnnotationParserHandler {

  private final MurderRun plugin;
  private final List<AnnotationCommandFeature> features;
  private final AnnotationParser<CommandSender> parser;

  public AnnotationParserHandler(final MurderRun plugin) {
    final LegacyPaperCommandManager<CommandSender> manager = this.getCommmandManager(plugin);
    manager.registerBrigadier();
    this.registerCustomMessages(manager);
    this.plugin = plugin;
    this.features =
        List.of(
            new MurderArenaCommand(),
            new MurderLobbyCommand(),
            new MurderHelpCommand(),
            new MurderGameCommand());
    this.parser = new AnnotationParser<>(manager, CommandSender.class);
  }

  private void registerCustomMessages(final CommandManager<CommandSender> manager) {
    final AudienceHandler handler = this.plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    MinecraftExceptionHandler.create(audiences::sender)
        .defaultInvalidSenderHandler()
        .decorator(message -> Locale.NOT_PLAYER.build())
        .registerTo(manager);
  }

  private LegacyPaperCommandManager<CommandSender> getCommmandManager(final MurderRun plugin) {
    return LegacyPaperCommandManager.createNative(plugin, ExecutionCoordinator.simpleCoordinator());
  }

  public void registerCommands() {
    this.features.forEach(feature -> feature.registerFeature(this.plugin, this.parser));
  }
}
