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
package io.github.pulsebeat02.murderrun.game.extension.citizens;

import io.github.pulsebeat02.murderrun.game.Game;
import java.util.UUID;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public final class CitizensManager {

  private final Game game;
  private final NPCRegistry registry;

  public CitizensManager(final Game game) {
    final UUID uuid = game.getGameUUID();
    final String str = uuid.toString();
    final String id = "murderrun-%s".formatted(str);
    this.game = game;
    this.registry = CitizensAPI.createInMemoryNPCRegistry(id);
  }

  public void shutdown() {
    final Iterable<NPC> npcs = this.registry.sorted();
    for (final NPC npc : npcs) {
      npc.destroy();
    }
  }

  public Game getGame() {
    return this.game;
  }

  public NPCRegistry getRegistry() {
    return this.registry;
  }
}
