package io.github.pulsebeat02.murderrun.map.event;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.InnocentPlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Collection;
import java.util.Optional;
import java.util.SplittableRandom;
import java.util.UUID;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.title.Title.title;

public final class GamePlayerDeathEvent implements Listener {

  private static final SplittableRandom RANDOM = new SplittableRandom();

  private final MurderGame game;

  public GamePlayerDeathEvent(final MurderGame game) {
    this.game = game;
  }

  @EventHandler
  public void onPlayerDeath(final PlayerDeathEvent event) {
    final Player player = event.getEntity();
    final UUID uuid = player.getUniqueId();
    final PlayerManager manager = this.game.getPlayerManager();
    final Optional<GamePlayer> optional = manager.lookupPlayer(uuid);
    if (optional.isEmpty()) {
      return;
    }
    final GamePlayer gamePlayer = optional.get();
    gamePlayer.markDeath();
    this.playDeathSoundEffect();
    if (this.allInnocentDead()) {
      this.announceMurdererVictory();
      this.game.finishGame();
    }
  }

  private boolean allInnocentDead() {
    final PlayerManager manager = this.game.getPlayerManager();
    final Collection<InnocentPlayer> players = manager.getInnocentPlayers();
    return players.stream().noneMatch(GamePlayer::isAlive);
  }

  private void announceMurdererVictory() {
    final PlayerManager manager = this.game.getPlayerManager();
    for (final GamePlayer gamePlayer : manager.getParticipants()) {
      final Player player = gamePlayer.getPlayer();
      player.playSound(player.getLocation(), Sound.ENTITY_CAT_HISS, 1, 1);
      player.showTitle(title(Locale.MURDERER_VICTORY.build(), empty()));
    }
  }

  private void playDeathSoundEffect() {
    final String[] sounds =
        new String[] {"murder_run:death_1", "murder_run:death_1", "murder_run:death_3"};
    final int index = this.generateRandomIndex(0, 2);
    final String key = sounds[index];
    final PlayerManager manager = this.game.getPlayerManager();
    for (final GamePlayer gamePlayer : manager.getParticipants()) {
      final Player player = gamePlayer.getPlayer();
      final Location location = player.getLocation();
      player.playSound(location, key, SoundCategory.AMBIENT, 1, 1);
    }
  }

  private int generateRandomIndex(final int min, final int max) {
    return RANDOM.nextInt(min, max);
  }
}
