package io.github.pulsebeat02.murderrun.game;

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
