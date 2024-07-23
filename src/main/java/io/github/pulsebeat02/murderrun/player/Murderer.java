package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public final class Murderer extends GamePlayer {
  public Murderer(final MurderGame game, final UUID uuid) {
    super(game, uuid);
  }

  @Override
  public void onPlayerAttemptPickupPartEvent(final PlayerAttemptPickupItemEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onMatchStart() {
    super.onMatchStart();
    final PotionEffect effect = new PotionEffect(PotionEffectType.HASTE, Integer.MAX_VALUE, 127);
    final Player player = this.getPlayer();
    player.setWalkSpeed(0.4f);
    player.setGameMode(GameMode.SURVIVAL);
    player.addPotionEffect(effect);
  }
}
