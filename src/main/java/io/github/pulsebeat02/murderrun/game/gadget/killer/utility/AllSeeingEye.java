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

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.StrictPlayerReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class AllSeeingEye extends KillerGadget {

  public AllSeeingEye() {
    super(
      "all_seeing_eye",
      GameProperties.ALL_SEEING_EYE_COST,
      ItemFactory.createGadget(
        "all_seeing_eye",
        Material.ENDER_EYE,
        Message.ALL_SEEING_EYE_NAME.build(),
        Message.ALL_SEEING_EYE_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer random = manager.getRandomAliveInnocentPlayer();
    final Location before = player.getLocation();
    this.setPlayerState(player, random);

    final int duration = GameProperties.ALL_SEEING_EYE_DURATION;
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(random);
    random.apply(target -> scheduler.scheduleRepeatedTask(() -> player.setSpectatorTarget(target), 0, 10, duration, reference));
    scheduler.scheduleTask(() -> this.resetPlayerState(player, before), duration, reference);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.ALL_SEEING_EYE_SOUND);

    return false;
  }

  private void resetPlayerState(final GamePlayer player, final Location location) {
    player.teleport(location);
    player.setSpectatorTarget(null);
    player.setAllowSpectatorTeleport(true);
    player.setGameMode(GameMode.SURVIVAL);
  }

  private void setPlayerState(final GamePlayer player, final GamePlayer survivor) {
    survivor.apply(internal -> {
      player.setGameMode(GameMode.SPECTATOR);
      player.setAllowSpectatorTeleport(false);
      player.setSpectatorTarget(internal);
    });
  }
}
