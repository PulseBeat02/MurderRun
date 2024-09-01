package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.commmand.arena.ArenaCommand;
import io.github.pulsebeat02.murderrun.commmand.game.GameCommand;
import io.github.pulsebeat02.murderrun.commmand.shop.ShopCommand;
import java.util.Arrays;
import java.util.List;

public enum Commands {
  ARENA(new ArenaCommand()),
  LOBBY(new LobbyCommand()),
  HELP(new HelpCommand()),
  GAME(new GameCommand()),
  GADGET(new GadgetCommand()),
  SHOP(new ShopCommand()),
  DEBUG(new DebugCommand());

  private static final List<AnnotationCommandFeature> FEATURES = getValues();

  private final AnnotationCommandFeature feature;

  Commands(final AnnotationCommandFeature feature) {
    this.feature = feature;
  }

  public AnnotationCommandFeature getFeature() {
    return this.feature;
  }

  public static List<AnnotationCommandFeature> getValues() {
    final Commands[] commands = values();
    return Arrays.stream(commands).map(Commands::getFeature).toList();
  }

  public static List<AnnotationCommandFeature> getFeatures() {
    return FEATURES;
  }
}
