/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.commmand.game.GameCommand;
import java.util.Arrays;
import java.util.List;

public enum Commands {
  ARENA(new ArenaCommand()),
  LOBBY(new LobbyCommand()),
  HELP(new HelpCommand()),
  GAME(new GameCommand()),
  GADGET(new GadgetCommand()),
  SHOP(new ShopCommand()),
  DEBUG(new DebugCommand()),
  GUI(new GuiCommand()),
  TRUCK(new TruckCommand());

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
