package io.github.pulsebeat02.murderrun.game.player;

import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PlayerStartupTool {

  private final PlayerManager manager;

  public PlayerStartupTool(final PlayerManager manager) {
    this.manager = manager;
  }

  public void configurePlayers() {
    this.manager.applyToAllMurderers(this::handleMurderer);
    this.manager.applyToAllLivingInnocents(this::handleInnocent);
  }

  private void handleAll(final GamePlayer gamePlayer) {
    gamePlayer.apply(player -> {
      player.setGameMode(GameMode.ADVENTURE);
      player.setHealth(20f);
      player.setFoodLevel(20);
    });
  }

  public void handleInnocent(final GamePlayer gamePlayer) {
    this.handleAll(gamePlayer);
    gamePlayer.apply(player -> player.setWalkSpeed(0.2f));
  }

  public void handleMurderer(final GamePlayer gamePlayer) {
    this.handleAll(gamePlayer);
    gamePlayer.addPotionEffects(
        new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
    gamePlayer.apply(player -> player.setWalkSpeed(0.3f));
  }
}
