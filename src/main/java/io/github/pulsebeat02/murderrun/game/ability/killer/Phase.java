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
package io.github.pulsebeat02.murderrun.game.ability.killer;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameStatus;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class Phase extends KillerAbility implements Listener {

  private static final Collection<Material> BLACKLISTED_BLOCKS;
  private static final String PHASE_NAME = "phase";

  static {
    BLACKLISTED_BLOCKS = new HashSet<>();
    final String raw = GameProperties.PHASE_BLACKLISTED_BLOCKS;
    final String[] individual = raw.split(",");
    for (final String material : individual) {
      final String upper = material.toUpperCase();
      final Material target = Material.getMaterial(upper);
      if (target != null) {
        BLACKLISTED_BLOCKS.add(Material.valueOf(material));
      }
    }
  }

  private final Map<GamePlayer, Long> cooldowns;

  public Phase(final Game game) {
    super(
      game,
      PHASE_NAME,
      ItemFactory.createAbility(
        PHASE_NAME,
        Message.PHASE_NAME.build(),
        Message.PHASE_LORE.build(),
        (int) (GameProperties.PHASE_COOLDOWN * 20)
      )
    );
    this.cooldowns = new HashMap<>();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerRightClick(final PlayerInteractEvent event) {
    final Action action = event.getAction();
    if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final GameStatus status = game.getStatus();
    final GameStatus.Status currentStatus = status.getStatus();
    if (currentStatus == GameStatus.Status.NOT_STARTED) {
      return;
    }

    final ItemStack item = event.getItem();
    if (item == null) {
      return;
    }

    if (!gamePlayer.hasAbility(PHASE_NAME)) {
      return;
    }

    if (!PDCUtils.isAbility(item)) {
      return;
    }

    final boolean killer = gamePlayer instanceof Killer;
    if (currentStatus == GameStatus.Status.SURVIVORS_RELEASED && killer) {
      player.setCooldown(item, 0);
      event.setCancelled(true);
      return;
    }

    if (this.invokeEvent(gamePlayer)) {
      return;
    }

    final Location eyeLocation = player.getEyeLocation();
    final Vector eyeDirection = eyeLocation.getDirection();
    final Vector direction = eyeDirection.normalize();
    final double maxDistance = GameProperties.PHASE_DISTANCE;

    Location targetLocation = null;
    for (int i = 1; i <= maxDistance; i++) {
      final Location clone = eyeLocation.clone();
      final Vector cloneDirection = direction.clone();
      final Vector multiply = cloneDirection.multiply(i);
      final Location checkLocation = clone.add(multiply);
      final Location clone2 = eyeLocation.clone();
      final Vector cloneDirection2 = direction.clone();
      final Vector multiply2 = cloneDirection2.multiply(i + 1);
      final Location nextLocation = clone2.add(multiply2);
      final Block checkBlock = checkLocation.getBlock();
      final Block nextBlock = nextLocation.getBlock();
      final Material checkType = checkBlock.getType();
      final Material nextType = nextBlock.getType();
      if (checkType.isSolid() && !nextType.isSolid()) {
        targetLocation = nextLocation;
        break;
      }
    }

    if (targetLocation != null) {
      boolean isPathClear = true;
      for (int i = 1; i <= maxDistance; i++) {
        final Location clone = eyeLocation.clone();
        final Vector cloneDirection = direction.clone();
        final Vector multiply = cloneDirection.multiply(i);
        final Location checkLocation = clone.add(multiply);
        final Block checkBlock = checkLocation.getBlock();
        final Material checkType = checkBlock.getType();
        if (BLACKLISTED_BLOCKS.contains(checkType)) {
          isPathClear = false;
          break;
        }
      }

      if (isPathClear) {
        final Location feetLocation = targetLocation.clone();
        final Location headLocation = feetLocation.clone().add(0, 1, 0);
        final Location belowFeetLocation = feetLocation.clone().add(0, -1, 0);

        final Block feetBlock = feetLocation.getBlock();
        final Block headBlock = headLocation.getBlock();
        final Block belowFeetBlock = belowFeetLocation.getBlock();

        final Material feetType = feetBlock.getType();
        final Material headType = headBlock.getType();
        final Material belowFeetType = belowFeetBlock.getType();

        if (!feetType.isSolid() && !headType.isSolid() && belowFeetType.isSolid()) {
          player.teleport(targetLocation);

          final int cooldown = (int) (GameProperties.PHASE_COOLDOWN * 1000);
          if (this.cooldowns.containsKey(gamePlayer)) {
            final long last = this.cooldowns.get(gamePlayer);
            final long current = System.currentTimeMillis();
            final long timeElapsed = current - last;
            if (timeElapsed < cooldown) {
              event.setCancelled(true);
              return;
            }
          }

          final long current = System.currentTimeMillis();
          this.cooldowns.put(gamePlayer, current);
          gamePlayer.setAbilityCooldowns(PHASE_NAME, (int) (GameProperties.PHASE_COOLDOWN * 20));
        }
      }
    }
  }
}
