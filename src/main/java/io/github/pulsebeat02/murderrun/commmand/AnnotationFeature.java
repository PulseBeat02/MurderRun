package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;

public interface AnnotationFeature {

  void registerFeature(MurderRun plugin, AnnotationParser<CommandSender> parser);
}
