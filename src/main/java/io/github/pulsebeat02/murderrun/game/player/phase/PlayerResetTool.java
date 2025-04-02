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
package io.github.pulsebeat02.murderrun.game.player.phase;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;

public final class PlayerResetTool {

  private final GamePlayerManager manager;

  public PlayerResetTool(final GamePlayerManager manager) {
    this.manager = manager;
  }

  public void configure() {
    this.manager.applyToAllParticipants(this::handlePlayer);
  }

  public void handlePlayer(final GamePlayer gamePlayer) {
    final List<World> worlds = Bukkit.getWorlds();
    final World world = worlds.getFirst();
    final Location location = world.getSpawnLocation();
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
    gamePlayer.resetAllAttributes();
  }
}
