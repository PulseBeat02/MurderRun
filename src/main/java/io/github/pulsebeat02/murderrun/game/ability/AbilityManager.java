/*

MIT License

Copyright (c) 2025 Brandon Li

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
package io.github.pulsebeat02.murderrun.game.ability;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;

public final class AbilityManager {

  private final MurderRun plugin;
  private final Game game;

  private AbilityLoadingMechanism mechanism;
  private AbilityActionHandler actionHandler;

  public AbilityManager(final Game game) {
    final MurderRun plugin = game.getPlugin();
    this.game = game;
    this.plugin = plugin;
  }

  public void start() {
    this.mechanism = new AbilityLoadingMechanism(this);
    this.actionHandler = new AbilityActionHandler(this);
    this.actionHandler.start();
  }

  public void shutdown() {
    this.actionHandler.shutdown();
    this.mechanism.shutdown();
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public Game getGame() {
    return this.game;
  }

  public AbilityLoadingMechanism getMechanism() {
    return this.mechanism;
  }

  public AbilityActionHandler getActionHandler() {
    return this.actionHandler;
  }
}
