package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.arena.MurderArenaManager;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.locale.LocaleParent;
import io.github.pulsebeat02.murderrun.locale.Sender;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;

public final class MurderArenaCommand implements AnnotationCommandFeature {

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
    final AudienceHandler handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.plugin = plugin;
  }

  @Command("murder arena create")
  public void createArena(final CommandSender sender) {

    if (PlayerUtils.checkIfPlayer(this.plugin, sender)) {
      return;
    }

    final Player player = (Player) sender;
    final Audience audience = this.audiences.player(player);
    if (this.first == null || this.second == null) {
      final Component message = Locale.CORNER_ERROR.build();
      audience.sendMessage(message);
      return;
    }

    if (this.spawn == null) {
      final Component message = Locale.SPAWN_ERROR.build();
      audience.sendMessage(message);
      return;
    }

    if (this.truck == null) {
      final Component message = Locale.TRUCK_ERROR.build();
      audience.sendMessage(message);
      return;
    }

    if (this.name == null) {
      final Component message = Locale.NAME_ERROR.build();
      audience.sendMessage(message);
      return;
    }

    final Location[] corners = new Location[] {this.first, this.second};
    final MurderArenaManager manager = this.plugin.getArenaManager();
    manager.addArena(this.name, corners, this.spawn, this.truck);

    final Component message = Locale.BUILT_ARENA.build();
    audience.sendMessage(message);
  }

  @Command("murder arena set name <string>")
  public void setSpawn(final CommandSender sender, final String name) {
    if (PlayerUtils.checkIfPlayer(this.plugin, sender)) {
      return;
    }
    final Player player = (Player) sender;
    this.name = name;
    final Audience audience = this.audiences.player(player);
    final Component message = Locale.SET_NAME.build(name);
    audience.sendMessage(message);
  }

  @Command("murder arena set spawn")
  public void setSpawn(final CommandSender sender) {
    if (PlayerUtils.checkIfPlayer(this.plugin, sender)) {
      return;
    }
    final Player player = (Player) sender;
    final Location location = player.getLocation();
    this.spawn = location;
    final Audience audience = this.audiences.player(player);
    final Component message = this.createLocationMessage(Locale.SET_SPAWN, location);
    audience.sendMessage(message);
  }

  @Command("murder arena set truck")
  public void setTruck(final CommandSender sender) {
    if (PlayerUtils.checkIfPlayer(this.plugin, sender)) {
      return;
    }
    final Player player = (Player) sender;
    final Location location = player.getLocation();
    this.truck = location;
    final Audience audience = this.audiences.player(player);
    final Component message = this.createLocationMessage(Locale.SET_TRUCK, location);
    audience.sendMessage(message);
  }

  @Command("murder arena set first-corner")
  public void setFirstCorner(final CommandSender sender) {
    if (PlayerUtils.checkIfPlayer(this.plugin, sender)) {
      return;
    }
    final Player player = (Player) sender;
    final Location location = player.getLocation();
    this.first = location;
    final Audience audience = this.audiences.player(player);
    final Component message = this.createLocationMessage(Locale.SET_FIRST_CORNER, location);
    audience.sendMessage(message);
  }

  @Command("murder arena set second-corner")
  public void setSecondCorner(final CommandSender sender) {
    if (PlayerUtils.checkIfPlayer(this.plugin, sender)) {
      return;
    }
    final Player player = (Player) sender;
    final Location location = player.getLocation();
    this.second = location;
    final Audience audience = this.audiences.player(player);
    final Component message = this.createLocationMessage(Locale.SET_SECOND_CORNER, location);
    audience.sendMessage(message);
  }

  private Component createLocationMessage(
      final LocaleParent.TriComponent<Sender, Integer, Integer, Integer> function,
      final Location location) {
    final int x = location.getBlockX();
    final int y = location.getBlockY();
    final int z = location.getBlockZ();
    return function.build(x, y, z);
  }
}
