package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
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
    parser.parse(this);
  }

  private void setupHelp() {
    this.minecraftHelp =
        MinecraftHelp.create("/murder help", this.manager, this.bukkitAudiences::sender);
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
  public void commandHelp(
      final CommandSender sender, @Argument(value = "query") @Greedy final String query) {
    this.minecraftHelp.queryCommands(query == null ? "" : query, sender);
  }
}
