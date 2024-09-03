package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.gui.CentralGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public final class GuiCommand implements AnnotationCommandFeature {

  private MurderRun plugin;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    this.plugin = plugin;
  }

  @Permission("murderrun.command.gui")
  @CommandDescription("murderrun.command.gui.info")
  @Command(value = "murder gui", requiredSender = Player.class)
  public void openGui(final Player sender) {
    final CentralGui gui = new CentralGui(this.plugin, sender);
    gui.update();
    gui.show(sender);
  }
}
