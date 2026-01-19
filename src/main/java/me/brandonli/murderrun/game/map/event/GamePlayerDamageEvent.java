/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.map.event;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

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

    final double health = player.getHealth();
    final double damage = event.getFinalDamage();
    final double finalHealth = health - damage;
    if (finalHealth <= 0.0) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final int opacity = mapHealthToOpacity(health, 2.0);
    final String raw = String.valueOf(opacity);
    if (opacity > 2) {
      final PlayerAudience audience = gamePlayer.getAudience();
      audience.playSound(Sounds.BREATHING);
    }

    final PlayerAudience audience = gamePlayer.getAudience();
    final Key key = key("murderrun", "fill");
    final TextColor color = TextColor.fromHexString("#FF0000");
    audience.showTitle(
        "screen", text(raw).font(key).color(color), empty(), Integer.MAX_VALUE, 0, 12 * 20, 8 * 20);
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
