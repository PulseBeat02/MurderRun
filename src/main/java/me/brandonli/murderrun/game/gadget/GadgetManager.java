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
package me.brandonli.murderrun.game.gadget;

import com.google.common.util.concurrent.AtomicDouble;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;

public final class GadgetManager {

  private final MurderRun plugin;
  private final Game game;
  private final AtomicDouble activationRange;

  private GadgetLoadingMechanism mechanism;
  private GadgetActionHandler actionHandler;

  public GadgetManager(final Game game) {
    final MurderRun plugin = game.getPlugin();
    this.game = game;
    this.plugin = plugin;
    this.activationRange = new AtomicDouble(2);
  }

  public void start() {
    this.mechanism = new GadgetLoadingMechanism(this);
    this.actionHandler = new GadgetActionHandler(this);
    this.actionHandler.start();
  }

  public void shutdown() {
    this.mechanism.shutdown();
    this.actionHandler.shutdown();
  }

  public GadgetLoadingMechanism getMechanism() {
    return this.mechanism;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public Game getGame() {
    return this.game;
  }

  public double getActivationRange() {
    return this.activationRange.get();
  }

  public void setActivationRange(final double range) {
    this.activationRange.getAndSet(range);
  }
}
