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
package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.StrictPlayerReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import io.github.pulsebeat02.murderrun.utils.map.MapUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

public final class EagleEye extends KillerGadget {

  public EagleEye() {
    super(
      "eagle_eye",
      GameProperties.EAGLE_EYE_COST,
      ItemFactory.createGadget(
        "eagle_eye",
        GameProperties.EAGLE_EYE_MATERIAL,
        Message.EAGLE_EYE_NAME.build(),
        Message.EAGLE_EYE_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location[] corners = arena.getCorners();
    final Location average = MapUtils.getAverageLocation(corners[0], corners[1]);
    final World world = requireNonNull(average.getWorld());

    final Block highest = world.getHighestBlockAt(average);
    final Location location = highest.getLocation();
    final Location teleport = location.add(0, 50, 0);

    final Location previous = player.getLocation();
    player.setGravity(false);
    player.teleport(teleport);
    player.setAllowFlight(true);

    final float before = player.getFlySpeed();
    player.setFlySpeed(0.0f);

    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    scheduler.scheduleTask(() -> this.resetState(player, previous, before), GameProperties.EAGLE_EYE_DURATION, reference);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.EAGLE_EYE_SOUND);

    return false;
  }

  private void resetState(final GamePlayer gamePlayer, final Location previous, final float flySpeed) {
    gamePlayer.teleport(previous);
    gamePlayer.setGravity(true);
    gamePlayer.setAllowFlight(false);
    gamePlayer.setFlySpeed(flySpeed);
  }
}
