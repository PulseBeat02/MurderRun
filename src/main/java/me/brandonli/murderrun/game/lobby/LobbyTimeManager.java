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
package me.brandonli.murderrun.game.lobby;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

import java.util.Collection;
import java.util.Set;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.InventoryUtils;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class LobbyTimeManager {

  private static final Set<Integer> ANNOUNCE_TIMES = Set.of(15, 10, 5, 4, 3, 2, 1);

  private final PreGameManager manager;

  private LobbyTimer timer;

  public LobbyTimeManager(final PreGameManager manager) {
    this.manager = manager;
  }

  public void startTimer() {
    final MurderRun plugin = this.manager.getPlugin();
    final GameProperties properties = this.manager.getProperties();
    final int time = properties.getLobbyStartingTime();
    this.timer = new LobbyTimer(time, this::handleTimer);
    this.timer.runTaskTimer(plugin, 0L, 20L);
  }

  public void shutdown() {
    this.timer.cancel();
  }

  public void cancelTimer() {
    this.timer.cancel();
    final Component msg = Message.LOBBY_TIMER_CANCEL.build();
    final MurderRun plugin = this.manager.getPlugin();
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final Collection<Player> players = playerManager.getParticipants();
    for (final Player player : players) {
      final Audience audience = audiences.player(player);
      audience.sendMessage(msg);
    }
  }

  public void resetTime() {
    this.timer.setTime(60);
  }

  private void handleTimer(final int seconds) {
    this.checkCurrency(seconds);

    if (ANNOUNCE_TIMES.contains(seconds)) {
      this.playTimerSound(seconds);
    }

    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final LobbyScoreboard scoreboard = playerManager.getScoreboard();
    scoreboard.updateScoreboard();

    this.setLevel(seconds);

    if (seconds == 0) {
      this.manager.startGame();
      this.timer.cancel();
    }
  }

  private void setLevel(final int seconds) {
    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final Collection<Player> players = playerManager.getParticipants();
    for (final Player player : players) {
      player.setLevel(seconds);
    }
  }

  private void playTimerSound(final int seconds) {
    final MurderRun plugin = this.manager.getPlugin();
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final Collection<Player> players = playerManager.getParticipants();
    final Component message = Message.LOBBY_TIMER.build(seconds);
    final GameProperties properties = this.manager.getProperties();
    final String raw = properties.getLobbyTimerSound();
    final Key key = key(raw);
    final Sound sound = sound(key, Sound.Source.MASTER, 1f, 2f);
    for (final Player player : players) {
      final Audience audience = audiences.player(player);
      audience.sendMessage(message);
      audience.playSound(sound);
    }
  }

  private void checkCurrency(final int seconds) {
    if (seconds % 2 == 0) {
      return;
    }

    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final Collection<Player> players = playerManager.getParticipants();
    for (final Player player : players) {
      final ItemStack[] slots = InventoryUtils.getAllSlotsOnScreen(player);
      if (this.checkAllSlots(slots)) {
        return;
      }
    }

    if (seconds <= 15) {
      return;
    }

    final MurderRun plugin = this.manager.getPlugin();
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final Component msg = Message.LOBBY_TIMER_SKIP.build();
    for (final Player player : players) {
      final Audience audience = audiences.player(player);
      audience.sendMessage(msg);
    }

    this.timer.setTime(15);
  }

  private boolean checkAllSlots(final ItemStack[] contents) {
    final GameProperties properties = this.manager.getProperties();
    final ItemStack sample = ItemFactory.createCurrency(properties, 1);
    for (final ItemStack stack : contents) {
      if (stack != null && stack.isSimilar(sample)) {
        return true;
      }
    }
    return false;
  }

  public LobbyTimer getTimer() {
    return this.timer;
  }
}
