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
package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.api.event.ApiEventBus;
import io.github.pulsebeat02.murderrun.api.event.EventBusProvider;
import io.github.pulsebeat02.murderrun.api.event.contract.GameStatusEvent;
import java.util.concurrent.atomic.AtomicReference;

public final class GameStatus {

  private final AtomicReference<Status> status;
  private final Game game;

  public GameStatus(final Game game) {
    this.status = new AtomicReference<>(Status.NOT_STARTED);
    this.game = game;
    this.setStatus(Status.NOT_STARTED);
  }

  public Status getStatus() {
    return this.status.get();
  }

  public void setStatus(final Status status) {
    final ApiEventBus eventBus = EventBusProvider.getBus();
    eventBus.post(GameStatusEvent.class, this, game);
    this.status.set(status);
  }

  public enum Status {
    NOT_STARTED,
    SURVIVORS_RELEASED,
    KILLERS_RELEASED,
    FINISHED,
  }
}
