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
package me.brandonli.murderrun.game.lobby;

import java.util.concurrent.atomic.AtomicInteger;
import me.brandonli.murderrun.game.GameProperties;
import org.bukkit.scheduler.BukkitRunnable;

public final class PreGameExpirationTimer extends BukkitRunnable {

  private final PreGamePlayerManager manager;
  private final AtomicInteger seconds;

  public PreGameExpirationTimer(final PreGamePlayerManager manager) {
    this.manager = manager;
    this.seconds = new AtomicInteger(0);
  }

  @Override
  public void run() {
    final int current = this.seconds.incrementAndGet();
    final int shutdownSeconds = GameProperties.GAME_EXPIRATION_TIME;
    if (current >= shutdownSeconds) {
      final PreGameManager preGameManager = this.manager.getManager();
      final GameManager gameManager = preGameManager.getGameManager();
      final String id = preGameManager.getId();
      gameManager.removeGame(id);
      this.cancel();
    }
  }
}
