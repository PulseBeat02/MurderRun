package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class GamePlayerChatEvent extends GameEvent {

  public GamePlayerChatEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerChat(final AsyncPlayerChatEvent event) {
    final Player player = event.getPlayer();
    if (!this.isGamePlayer(player)) {
      return;
    }
    event.setCancelled(true);

    final Game game = this.getGame();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final String raw = event.getMessage();
    final String format = event.getFormat();
    final String display = player.getDisplayName();
    final String formatted = String.format(format, display, raw);
    if (gamePlayer.isAlive()) {
      final Component msg = AdventureUtils.deserializeLegacyStringToComponent(formatted);
      manager.sendMessageToAllParticipants(msg);
      return;
    }

    final Component msg = Message.DEAD_CHAT_PREFIX.build(formatted);
    manager.sendMessageToAllDeceased(msg);
  }
}
