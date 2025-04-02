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
package me.brandonli.murderrun.game.gadget.killer.utility;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class AllSeeingEye extends KillerGadget {

  public AllSeeingEye() {
    super(
      "all_seeing_eye",
      GameProperties.ALL_SEEING_EYE_COST,
      ItemFactory.createGadget(
        "all_seeing_eye",
        GameProperties.ALL_SEEING_EYE_MATERIAL,
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
