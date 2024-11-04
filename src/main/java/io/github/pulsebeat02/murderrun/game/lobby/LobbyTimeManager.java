/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.lobby;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.InventoryUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.Collection;
import java.util.Set;
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
    final int time = GameProperties.LOBBY_STARTING_TIME;
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
    final String raw = GameProperties.LOBBY_TIMER_SOUND;
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
    final ItemStack sample = ItemFactory.createCurrency(1);
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
