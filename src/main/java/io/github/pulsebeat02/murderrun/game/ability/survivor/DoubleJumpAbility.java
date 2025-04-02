/*

MIT License

Copyright (c) 2025 Brandon Li

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
package io.github.pulsebeat02.murderrun.game.ability.survivor;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.PluginManager;

public final class DoubleJumpAbility extends SurvivorAbility implements Listener {

  private Game game;
  private GamePlayer player;

  public DoubleJumpAbility() {
    super(
      "double_jump",
      ItemFactory.createAbility("double_jump", Material.RABBIT_FOOT, Message.DOUBLE_JUMP_NAME.build(), Message.DOUBLE_JUMP_LORE.build())
    );
  }

  @Override
  public void start(final Game game, final GamePlayer player) {
    this.game = game;
    this.player = player;
    final MurderRun plugin = game.getPlugin();
    final Server server = Bukkit.getServer();
    final PluginManager pluginManager = server.getPluginManager();
    pluginManager.registerEvents(this, plugin);
  }

  @Override
  public void shutdown() {}

  @EventHandler
  public void onPlayerToggleFlight(final PlayerToggleFlightEvent event) {
    final Player player = event.getPlayer();
    //    if (lastJumpTime.containsKey(player.getUniqueId())) {
    //      final long timeElapsed = System.currentTimeMillis() - lastJumpTime.get(player.getUniqueId());
    //      if (timeElapsed < cooldown) {
    //        event.setCancelled(true);
    //        return;
    //      }
    //    }
    //
    //    // Check if player is allowed to double-jump
    //    if (!player.isFlying()) {
    //      // Cancel flight toggle
    //      event.setCancelled(true);
    //
    //      // Set player to not be allowed to fly again until they land
    //      player.setAllowFlight(false);
    //
    //      // Update last jump time
    //      lastJumpTime.put(player.getUniqueId(), System.currentTimeMillis());
    //
    //      // Give upward velocity
    //      player.setVelocity(player.getLocation().getDirection().setY(jumpVelocity));
    //
    //      // Add particle effect
    //      player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 30, 0.5, 0.1, 0.5, 0.1);
    //
    //      // Add sound effect
    //      player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0f, 1.0f);
  }

  @EventHandler
  @SuppressWarnings("deprecation")
  public void onPlayerLand(final PlayerMoveEvent event) {
    final Player player = event.getPlayer();
    if (player.isOnGround()) {
      player.setAllowFlight(true);
    }
  }
}
