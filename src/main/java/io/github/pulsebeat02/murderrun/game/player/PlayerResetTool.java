package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.persistence.PersistentDataContainer;

public final class PlayerResetTool {

  private final PlayerManager manager;

  public PlayerResetTool(final PlayerManager manager) {
    this.manager = manager;
  }

  public void configure() {
    this.manager.applyToAllParticipants(this::handlePlayer);
  }

  public void handlePlayer(final GamePlayer gamePlayer) {
    final Game game = this.manager.getGame();
    final GameSettings configuration = game.getSettings();
    final Lobby lobby = requireNonNull(configuration.getLobby());
    final Location location = lobby.getLobbySpawn();
    final MetadataManager metadata = gamePlayer.getMetadataManager();
    final PlayerAudience audience = gamePlayer.getAudience();
    final PersistentDataContainer container = gamePlayer.getPersistentDataContainer();
    container.remove(Keys.KILLER_ROLE);
    metadata.setWorldBorderEffect(false);
    metadata.setNameTagStatus(false);
    metadata.shutdown();
    audience.removeAllBossBars();
    gamePlayer.removeAllPotionEffects();
    gamePlayer.teleport(location);
    gamePlayer.clearInventory();
    gamePlayer.setGameMode(GameMode.SURVIVAL);
    gamePlayer.setHealth(20f);
    gamePlayer.setFoodLevel(20);
    gamePlayer.setLevel(0);
    gamePlayer.setSaturation(Float.MAX_VALUE);
    gamePlayer.setFreezeTicks(0);
    gamePlayer.setWalkSpeed(0.2f);
    gamePlayer.setExp(0);
    gamePlayer.setGlowing(false);
    gamePlayer.setFireTicks(0);
    gamePlayer.stopAllSounds();
    gamePlayer.setInvulnerable(false);
    this.resetAttributes(gamePlayer);
  }

  private void resetAttributes(final GamePlayer player) {
    final Attribute[] attributes = Attribute.values();
    for (final Attribute attribute : attributes) {
      final AttributeInstance instance = player.getAttribute(attribute);
      if (instance != null) {
        final double defaultValue = instance.getDefaultValue();
        instance.setBaseValue(defaultValue);
      }
    }

    // overrides
    // LivingEntity.createLivingAttributes().add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.10000000149011612D).add(Attributes.ATTACK_SPEED).add(Attributes.LUCK).add(Attributes.BLOCK_INTERACTION_RANGE, 4.5D).add(Attributes.ENTITY_INTERACTION_RANGE, 3.0D)
    final AttributeInstance speed = requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED));
    speed.setBaseValue(0.10000000149011612D);
  }
}
