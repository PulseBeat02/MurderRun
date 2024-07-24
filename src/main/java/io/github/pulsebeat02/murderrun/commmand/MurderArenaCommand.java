package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.arena.MurderArenaManager;
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
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

  @CommandDescription("Lists all created arenas")
  @Command(value = "murder arena list", requiredSender = Player.class)
  public void listArenas(final Player sender) {
    final MurderArenaManager manager = this.plugin.getArenaManager();
    final Map<String, MurderArena> arenas = manager.getArenas();
    final List<String> keys = new ArrayList<>(arenas.keySet());
    final Audience audience = this.audiences.player(sender);
    final Component message = Locale.ARENA_LIST.build(keys);
    audience.sendMessage(message);
  }

  @CommandDescription("Creates an arena with the specified settings")
  @Command(value = "murder arena create", requiredSender = Player.class)
  public void createArena(final Player sender) {

    final Audience audience = this.audiences.player(sender);
    if (this.first == null || this.second == null) {
      final Component message = Locale.ARENA_CORNER_ERROR.build();
      audience.sendMessage(message);
      return;
    }

    if (this.spawn == null) {
      final Component message = Locale.ARENA_SPAWN_ERROR.build();
      audience.sendMessage(message);
      return;
    }

    if (this.truck == null) {
      final Component message = Locale.ARENA_TRUCK_ERROR.build();
      audience.sendMessage(message);
      return;
    }

    if (this.name == null) {
      final Component message = Locale.ARENA_NAME_ERROR.build();
      audience.sendMessage(message);
      return;
    }

    final Location[] corners = new Location[] {this.first, this.second};
    final MurderArenaManager manager = this.plugin.getArenaManager();
    manager.addArena(this.name, corners, this.spawn, this.truck);

    final Component message = Locale.ARENA_BUILT.build();
    audience.sendMessage(message);

    this.plugin.updatePluginData();
  }

  @CommandDescription("Sets the name of the arena")
  @Command(value = "murder arena set name <string>", requiredSender = Player.class)
  public void setName(final Player sender, @Quoted final String name) {
    this.name = name;
    final Audience audience = this.audiences.player(sender);
    final Component message = Locale.ARENA_NAME.build(name);
    audience.sendMessage(message);
  }

  @CommandDescription("Sets the spawn location of the arena")
  @Command(value = "murder arena set spawn", requiredSender = Player.class)
  public void setSpawn(final Player sender) {
    final Location location = sender.getLocation();
    this.spawn = location;
    final Audience audience = this.audiences.player(sender);
    final Component message = AdventureUtils.createLocationComponent(Locale.ARENA_SPAWN, location);
    audience.sendMessage(message);
  }

  @CommandDescription("Sets the truck location of the arena")
  @Command(value = "murder arena set truck", requiredSender = Player.class)
  public void setTruck(final Player sender) {
    final Location location = sender.getLocation();
    this.truck = location;
    final Audience audience = this.audiences.player(sender);
    final Component message = AdventureUtils.createLocationComponent(Locale.ARENA_TRUCK, location);
    audience.sendMessage(message);
  }

  @CommandDescription("Sets the first corner location of the arena")
  @Command(value = "murder arena set first-corner", requiredSender = Player.class)
  public void setFirstCorner(final Player sender) {
    final Location location = sender.getLocation();
    this.first = location;
    final Audience audience = this.audiences.player(sender);
    final Component message =
        AdventureUtils.createLocationComponent(Locale.ARENA_FIRST_CORNER, location);
    audience.sendMessage(message);
  }

  @CommandDescription("Sets the second corner location of the arena")
  @Command(value = "murder arena set second-corner", requiredSender = Player.class)
  public void setSecondCorner(final Player sender) {
    final Location location = sender.getLocation();
    this.second = location;
    final Audience audience = this.audiences.player(sender);
    final Component message =
        AdventureUtils.createLocationComponent(Locale.ARENA_SECOND_CORNER, location);
    audience.sendMessage(message);
  }
}
