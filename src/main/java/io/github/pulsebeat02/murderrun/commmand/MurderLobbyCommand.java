package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.lobby.MurderLobby;
import io.github.pulsebeat02.murderrun.lobby.MurderLobbyManager;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MurderLobbyCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;
  private Location spawn;
  private String name;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceHandler handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.plugin = plugin;
  }

  @CommandDescription("Lists all created lobbies")
  @Command("murder lobby list")
  public void listLobbies(final CommandSender sender) {

    if (PlayerUtils.checkIfPlayer(this.plugin, sender)) {
      return;
    }

    final MurderLobbyManager manager = this.plugin.getLobbyManager();
    final Map<String, MurderLobby> arenas = manager.getLobbies();
    final List<String> keys = new ArrayList<>(arenas.keySet());
    final Player player = (Player) sender;
    final Audience audience = this.audiences.player(player);
    final Component message = Locale.LOBBY_LIST.build(keys);
    audience.sendMessage(message);
  }

  @CommandDescription("Creates an arena with the specified settings")
  @Command("murder lobby create")
  public void createLobby(final CommandSender sender) {

    if (PlayerUtils.checkIfPlayer(this.plugin, sender)) {
      return;
    }

    final Player player = (Player) sender;
    final Audience audience = this.audiences.player(player);
    if (this.spawn == null) {
      final Component message = Locale.LOBBY_SPAWN_ERROR.build();
      audience.sendMessage(message);
      return;
    }

    if (this.name == null) {
      final Component message = Locale.LOBBY_NAME_ERROR.build();
      audience.sendMessage(message);
      return;
    }

    final MurderLobbyManager manager = this.plugin.getLobbyManager();
    manager.addLobby(this.name, this.spawn);

    final Component message = Locale.LOBBY_BUILT.build();
    audience.sendMessage(message);

    this.plugin.updatePluginData();
  }

  @CommandDescription("Sets the name of the lobby")
  @Command("murder lobby set name <string>")
  public void setName(final CommandSender sender, final String name) {
    if (PlayerUtils.checkIfPlayer(this.plugin, sender)) {
      return;
    }
    final Player player = (Player) sender;
    this.name = name;
    final Audience audience = this.audiences.player(player);
    final Component message = Locale.LOBBY_NAME.build(name);
    audience.sendMessage(message);
  }

  @CommandDescription("Sets the spawn location of the lobby")
  @Command("murder lobby set spawn")
  public void setSpawn(final CommandSender sender) {
    if (PlayerUtils.checkIfPlayer(this.plugin, sender)) {
      return;
    }
    final Player player = (Player) sender;
    final Location location = player.getLocation();
    this.spawn = location;
    final Audience audience = this.audiences.player(player);
    final Component message = AdventureUtils.createLocationComponent(Locale.LOBBY_SPAWN, location);
    audience.sendMessage(message);
  }
}
