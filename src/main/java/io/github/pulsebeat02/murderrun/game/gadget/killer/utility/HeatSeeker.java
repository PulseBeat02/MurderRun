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
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.StrictPlayerReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class HeatSeeker extends KillerGadget {

  public HeatSeeker() {
    super(
      "heat_seeker",
      Material.BLAZE_ROD,
      Message.HEAT_SEEKER_NAME.build(),
      Message.HEAT_SEEKER_LORE.build(),
      GameProperties.HEAT_SEEKER_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final GamePlayerManager manager = game.getPlayerManager();
    if (!(player instanceof final Killer killer)) {
      return true;
    }
    item.remove();

    final StrictPlayerReference reference = StrictPlayerReference.of(killer);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.scheduleTasks(manager, killer), 0, 2 * 20L, reference);

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.HEAT_SEEKER_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GameProperties.HEAT_SEEKER_SOUND);

    return false;
  }

  private void scheduleTasks(final GamePlayerManager manager, final Killer player) {
    manager.applyToLivingSurvivors(innocent -> this.handleGlowInnocent(innocent, player));
  }

  private void handleGlowInnocent(final GamePlayer innocent, final Killer owner) {
    final Location location = innocent.getLocation();
    final Location other = owner.getLocation();
    final Collection<GamePlayer> visible = owner.getHeatSeekerGlowing();
    final double distance = location.distanceSquared(other);
    final MetadataManager metadata = owner.getMetadataManager();
    final double radius = GameProperties.HEAT_SEEKER_RADIUS;
    if (distance < radius * radius) {
      visible.add(innocent);
      metadata.setEntityGlowing(innocent, ChatColor.RED, true);
    } else if (visible.contains(innocent)) {
      visible.remove(innocent);
      metadata.setEntityGlowing(innocent, ChatColor.RED, false);
    }
  }
}
