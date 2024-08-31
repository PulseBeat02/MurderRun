package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
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
import org.incendo.cloud.annotations.Permission;

public final class ArenaCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;

  private String name;
  private Location spawn;
  private Location truck;
  private Location first;
  private Location second;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.plugin = plugin;
  }

  @Permission("murderrun.command.arena.list")
  @CommandDescription("murderrun.command.arena.list.info")
  @Command(value = "murder arena list", requiredSender = Player.class)
  public void listArenas(final Player sender) {
    final ArenaManager manager = this.plugin.getArenaManager();
    final Map<String, Arena> arenas = manager.getArenas();
    final List<String> keys = new ArrayList<>(arenas.keySet());
    final Component message = Message.ARENA_LIST.build(keys);
    this.sendSuccessMessage(sender, message);
  }

  @Permission("murderrun.command.arena.remove")
  @CommandDescription("murderrun.command.arena.remove.info")
  @Command(value = "murder arena remove <name>", requiredSender = Player.class)
  public void removeArena(final Player sender, final String name) {
    final Audience audience = this.audiences.player(sender);
    if (this.checkInvalidArena(audience, name)) {
      return;
    }
    final ArenaManager manager = this.plugin.getArenaManager();
    manager.removeArena(name);
    final Component message = Message.ARENA_REMOVE.build(name);
    this.sendSuccessMessage(sender, message);
  }

  private boolean checkInvalidArena(final Audience audience, final String name) {
    final ArenaManager manager = this.plugin.getArenaManager();
    final Map<String, Arena> arenas = manager.getArenas();
    final Arena arena = arenas.get(name);
    if (arena == null) {
      final Component message = Message.ARENA_REMOVE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private void sendSuccessMessage(final Player player, final Component component) {
    final Audience audience = this.audiences.player(player);
    audience.sendMessage(component);
  }

  @Permission("murderrun.command.arena.create")
  @CommandDescription("murderrun.command.arena.create.info")
  @Command(value = "murder arena create", requiredSender = Player.class)
  public void createArena(final Player sender) {
    final Audience audience = this.audiences.player(sender);
    if (this.handleNullCorner(audience)
        || this.handleNullSpawn(audience)
        || this.handleNullTruck(audience)
        || this.handleNullName(audience)) {
      return;
    }
    final Location[] corners = new Location[] {this.first, this.second};
    final ArenaManager manager = this.plugin.getArenaManager();
    manager.addArena(this.name, corners, this.spawn, this.truck);
    final Component message = Message.ARENA_BUILT.build();
    audience.sendMessage(message);
    this.plugin.updatePluginData();
  }

  private boolean handleNullCorner(final Audience audience) {
    if (this.first == null || this.second == null) {
      final Component message = Message.ARENA_CORNER_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean handleNullSpawn(final Audience audience) {
    if (this.spawn == null) {
      final Component message = Message.ARENA_SPAWN_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean handleNullTruck(final Audience audience) {
    if (this.truck == null) {
      final Component message = Message.ARENA_TRUCK_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean handleNullName(final Audience audience) {
    if (this.name == null) {
      final Component message = Message.ARENA_NAME_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @Permission("murderrun.command.arena.set.name")
  @CommandDescription("murderrun.command.arena.set.name.info")
  @Command(value = "murder arena set name <name>", requiredSender = Player.class)
  public void setName(final Player sender, @Quoted final String name) {
    this.name = name;
    final Component message = Message.ARENA_NAME.build(name);
    this.sendSuccessMessage(sender, message);
  }

  @Permission("murderrun.command.arena.set.spawn")
  @CommandDescription("murderrun.command.arena.set.spawn.info")
  @Command(value = "murder arena set spawn", requiredSender = Player.class)
  public void setSpawn(final Player sender) {
    final Location location = sender.getLocation();
    this.spawn = location;
    final Component message = AdventureUtils.createLocationComponent(Message.ARENA_SPAWN, location);
    this.sendSuccessMessage(sender, message);
  }

  @Permission("murderrun.command.arena.set.truck")
  @CommandDescription("murderrun.command.arena.set.truck.info")
  @Command(value = "murder arena set truck", requiredSender = Player.class)
  public void setTruck(final Player sender) {
    final Location location = sender.getLocation();
    this.truck = location;
    final Component message = AdventureUtils.createLocationComponent(Message.ARENA_TRUCK, location);
    this.sendSuccessMessage(sender, message);
  }

  @Permission("murderrun.command.arena.set.first-corner")
  @CommandDescription("murderrun.command.arena.set.first_corner.info")
  @Command(value = "murder arena set first-corner", requiredSender = Player.class)
  public void setFirstCorner(final Player sender) {
    final Location location = sender.getLocation();
    this.first = location;
    final Component message =
        AdventureUtils.createLocationComponent(Message.ARENA_FIRST_CORNER, location);
    this.sendSuccessMessage(sender, message);
  }

  @Permission("murderrun.command.arena.set.second-corner")
  @CommandDescription("murderrun.command.arena.set.second_corner.info")
  @Command(value = "murder arena set second-corner", requiredSender = Player.class)
  public void setSecondCorner(final Player sender) {
    final Location location = sender.getLocation();
    this.second = location;
    final Component message =
        AdventureUtils.createLocationComponent(Message.ARENA_SECOND_CORNER, location);
    this.sendSuccessMessage(sender, message);
  }
}
