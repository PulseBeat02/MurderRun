package me.brandonli.murderrun.game.map.event;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.resourcepack.sound.Sounds;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.text.Component.text;

public final class GamePlayerDamageEvent extends GameEvent {

  public GamePlayerDamageEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerDamage(final EntityDamageEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Player player)) {
      return;
    }

    if (!this.isGamePlayer(player)) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final double health = gamePlayer.getHealth();
    final int opacity = mapHealthToOpacity(health, 2.0);
    final String raw = String.valueOf(opacity);
    if (opacity > 2) {
      final PlayerAudience audience = gamePlayer.getAudience();
      audience.playSound(Sounds.BREATHING);
    }

    final PlayerAudience audience = gamePlayer.getAudience();
    final Key key = key("murderrun", "fill");
    final TextColor color = TextColor.fromHexString("#FF0000");
    audience.showTitle(text(raw).font(key).color(color), empty(), 0, 12 * 20, 8 * 20);
  }

  // create distribution
  public static int mapHealthToOpacity(final double health, final double gamma) {
    final double clamped = Math.max(0.0, Math.min(20.0, health));
    final double f = clamped / 20.0;
    final double inv = 1.0 - f;
    final double scaled = Math.pow(inv, Math.max(0.0, gamma));
    final int level = (int) Math.ceil(scaled * 5.0);
    return Math.max(1, Math.min(5, level));
  }
}
