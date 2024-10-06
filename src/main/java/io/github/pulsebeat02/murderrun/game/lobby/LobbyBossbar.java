package io.github.pulsebeat02.murderrun.game.lobby;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.HashSet;
import java.util.Set;
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

  private BossBar createBossbar(
      @UnderInitialization LobbyBossbar this, final PreGamePlayerManager playerManager) {
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
