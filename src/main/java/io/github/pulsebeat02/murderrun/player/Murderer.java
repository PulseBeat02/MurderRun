package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public final class Murderer extends GamePlayer {
  public Murderer(final MurderGame game, final UUID uuid) {
    super(game, uuid);
  }

  @Override
  public void onPlayerAttemptPickupPartEvent(final EntityPickupItemEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onMatchStart() {
    super.onMatchStart();
    final PotionEffect effect = new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 127);
    final Player player = this.getPlayer();
    player.setWalkSpeed(0.3f);
    player.setGameMode(GameMode.SURVIVAL);
    player.addPotionEffect(effect);
  }
}
