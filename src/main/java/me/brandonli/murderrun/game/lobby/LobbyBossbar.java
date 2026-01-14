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
    this.players.clear();
  }
}
