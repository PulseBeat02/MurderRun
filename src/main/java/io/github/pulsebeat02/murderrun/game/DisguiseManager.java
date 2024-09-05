package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import java.util.ArrayList;
import java.util.Collection;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.entity.Player;

public final class DisguiseManager {

  private final Collection<Disguise> disguises;

  public DisguiseManager() {
    this.disguises = new ArrayList<>();
  }

  public void disguisePlayerAsOtherPlayer(final GamePlayer owner, final GamePlayer other) {
    final String name = other.getName();
    final Player disguisable = owner.getInternalPlayer();
    final PlayerDisguise disguise = new PlayerDisguise(name);
    disguise.setEntity(disguisable);
    disguise.startDisguise();
    this.disguises.add(disguise);
  }

  public void shutdown() {
    for (final Disguise disguise : this.disguises) {
      disguise.removeDisguise();
    }
  }
}
