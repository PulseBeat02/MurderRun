package io.github.pulsebeat02.murderrun.commmand.game;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import org.bukkit.entity.Player;

public final class PlayerResourcePackChecker {

  private final Set<Player> players;

  public PlayerResourcePackChecker() {
    this.players = Collections.newSetFromMap(new WeakHashMap<>());
  }

  public void markLoaded(final Player player) {
    this.players.add(player);
  }

  public boolean isLoaded(final Player player) {
    return this.players.contains(player);
  }
}
