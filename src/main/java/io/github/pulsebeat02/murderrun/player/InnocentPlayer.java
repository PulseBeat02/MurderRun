package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;

public final class InnocentPlayer extends GamePlayer {

  private boolean carPart;

  public InnocentPlayer(final MurderGame game, final UUID uuid) {
    super(game, uuid);
  }

  @Override
  public void onPlayerAttemptPickupPartEvent(final EntityPickupItemEvent event) {
    this.setHasCarPart(true);
  }

  @Override
  public void onMatchStart() {
    super.onMatchStart();
    final Player player = this.getPlayer();
    player.setWalkSpeed(0.25f);
    player.setGameMode(GameMode.ADVENTURE);
  }

  public void setHasCarPart(final boolean hasCarPart) {
    this.carPart = hasCarPart;
  }

  public boolean hasCarPart() {
    return this.carPart;
  }
}
