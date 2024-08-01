package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.lobby.MurderLobby;
import io.github.pulsebeat02.murderrun.lobby.MurderLobbyManager;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;

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

  @CommandDescription("murder_run.command.lobby.list.info")
  @Command(value = "murder lobby list", requiredSender = Player.class)
  public void listLobbies(final Player sender) {
    final MurderLobbyManager manager = this.plugin.getLobbyManager();
    final Map<String, MurderLobby> arenas = manager.getLobbies();
    final List<String> keys = new ArrayList<>(arenas.keySet());
    final Component message = Locale.LOBBY_LIST.build(keys);
    this.sendSuccessMessage(sender, message);
  }

  private void sendSuccessMessage(final Player player, final Component component) {
    final Audience audience = this.audiences.player(player);
    audience.sendMessage(component);
  }

  @CommandDescription("murder_run.command.lobby.remove.info")
  @Command(value = "murder lobby remove <name>", requiredSender = Player.class)
  public void removeLobby(final Player sender, final String name) {
    final Audience audience = this.audiences.player(sender);
    if (this.checkInvalidLobby(audience, name)) {
      return;
    }
    final MurderLobbyManager manager = this.plugin.getLobbyManager();
    manager.removeLobby(name);
    final Component message = Locale.LOBBY_REMOVE.build(name);
    this.sendSuccessMessage(sender, message);
  }

  private boolean checkInvalidLobby(final Audience audience, final String name) {
    final MurderLobbyManager manager = this.plugin.getLobbyManager();
    final Map<String, MurderLobby> arenas = manager.getLobbies();
    final MurderLobby lobby = arenas.get(name);
    if (lobby == null) {
      final Component message = Locale.LOBBY_REMOVE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @CommandDescription("murder_run.command.lobby.create.info")
  @Command(value = "murder lobby create", requiredSender = Player.class)
  public void createLobby(final Player sender) {
    final Audience audience = this.audiences.player(sender);
    if (this.handleNullSpawn(audience) || this.handleNullName(audience)) {
      return;
    }
    final MurderLobbyManager manager = this.plugin.getLobbyManager();
    manager.addLobby(this.name, this.spawn);
    final Component message = Locale.LOBBY_BUILT.build();
    this.sendSuccessMessage(sender, message);
    this.plugin.updatePluginData();
  }

  private boolean handleNullSpawn(final Audience audience) {
    if (this.spawn == null) {
      final Component message = Locale.LOBBY_SPAWN_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean handleNullName(final Audience audience) {
    if (this.name == null) {
      final Component message = Locale.LOBBY_NAME_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @CommandDescription("murder_run.command.lobby.set.name.info")
  @Command(value = "murder lobby set name <name>", requiredSender = Player.class)
  public void setName(final Player sender, @Quoted final String name) {
    this.name = name;
    final Component message = Locale.LOBBY_NAME.build(name);
    this.sendSuccessMessage(sender, message);
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public void setPlugin(final MurderRun plugin) {
    this.plugin = plugin;
  }

  public BukkitAudiences getAudiences() {
    return this.audiences;
  }

  public void setAudiences(final BukkitAudiences audiences) {
    this.audiences = audiences;
  }

  public Location getSpawn() {
    return this.spawn;
  }

  @CommandDescription("murder_run.command.lobby.set.spawn.info")
  @Command(value = "murder lobby set spawn", requiredSender = Player.class)
  public void setSpawn(final Player sender) {
    final Location location = sender.getLocation();
    this.spawn = location;
    final Component message = AdventureUtils.createLocationComponent(Locale.LOBBY_SPAWN, location);
    this.sendSuccessMessage(sender, message);
  }

  public void setSpawn(final Location spawn) {
    this.spawn = spawn;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }
}
