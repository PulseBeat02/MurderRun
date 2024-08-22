package io.github.pulsebeat02.murderrun.commmand;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;

public final class DebugCommand implements AnnotationCommandFeature {

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    // do nothing
  }

  @Command(value = "murder debug", requiredSender = Player.class)
  public void startDebugGame(final Player sender) {

    sender.performCommand("murder game create TestLobby TestArena");
    sender.performCommand("murder game set murderer PulseBeat_02");
    sender.performCommand("murder game invite Player1");

    final Player other = requireNonNull(Bukkit.getPlayer("Player1"));
    other.performCommand("murder game join PulseBeat_02");

    sender.performCommand("murder game start");
  }
}
