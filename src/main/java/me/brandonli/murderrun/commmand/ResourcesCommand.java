package me.brandonli.murderrun.commmand;

import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.provider.ResourcePackProvider;
import me.brandonli.murderrun.utils.ComponentUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.resource.ResourcePackRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import static java.util.Objects.requireNonNull;

public final class ResourcesCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.plugin = plugin;
  }

  @Permission("murderrun.command.resources")
  @Command(value = "murder resources", requiredSender = Player.class)
  @CommandDescription("murderrun.command.resources.info")
  public void sendResourcePack(final Player sender) {
    this.setResourcePack(sender);
  }

  private void setResourcePack(final Player player) {
    final ResourcePackProvider daemon = this.plugin.getProvider();
    final ResourcePackRequest request = daemon.getResourcePackRequest();
    ComponentUtils.sendPacksLegacy(player, request);
  }
}
