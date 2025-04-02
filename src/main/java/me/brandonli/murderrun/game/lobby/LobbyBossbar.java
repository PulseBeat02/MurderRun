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
package me.brandonli.murderrun.game.lobby;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Set;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class LobbyBossbar {

  private final PreGamePlayerManager playerManager;
  private final BossBar bar;
  private final Set<Audience> players;

  public LobbyBossbar(final PreGamePlayerManager manager) {
    this.playerManager = manager;
    this.bar = this.createBossbar(manager);
    this.players = new HashSet<>();
  }

  private BossBar createBossbar(@UnderInitialization LobbyBossbar this, final PreGamePlayerManager playerManager) {
    final PreGameManager manager = playerManager.getManager();
    final GameSettings settings = manager.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final String name = arena.getName();
    final Component component = Message.LOBBY_BOSSBAR_TITLE.build(name);
    return BossBar.bossBar(component, 1.0f, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);
  }

  public void addPlayer(final Player player) {
    final PreGameManager manager = this.playerManager.getManager();
    final MurderRun plugin = manager.getPlugin();
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final Audience audience = audiences.player(player);
    this.players.add(audience);
    audience.showBossBar(this.bar);
  }

  public void shutdown() {
    for (final Audience audience : this.players) {
      audience.hideBossBar(this.bar);
    }
  }
}
