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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import java.util.HashSet;
import java.util.Set;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;

public final class Distorter extends SurvivorGadget {

  private final Set<Integer> removed;

  public Distorter() {
    super(
      "distorter",
      GameProperties.DISTORTER_COST,
      ItemFactory.createGadget(
        "distorter",
        GameProperties.DISTORTER_MATERIAL,
        Message.DISTORTER_NAME.build(),
        Message.DISTORTER_LORE.build()
      )
    );
    this.removed = new HashSet<>();
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    final GamePlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTaskUntilDeath(() -> this.handleKillers(manager, item), item);
    scheduler.scheduleParticleTaskUntilDeath(item, Color.PURPLE);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.DISTORTER_SOUND);

    return false;
  }

  private void handleKillers(final GamePlayerManager manager, final Item item) {
    manager.applyToKillers(killer -> this.applyDistortionEffect(manager, killer, item));
  }

  private void applyDistortionEffect(final GamePlayerManager manager, final GamePlayer killer, final Item item) {
    final Location location = killer.getLocation();
    final Location origin = item.getLocation();
    final double distance = location.distanceSquared(origin);
    final double destroyRadius = GameProperties.DISTORTER_DESTROY_RADIUS;
    final double effectRadius = GameProperties.DISTORTER_EFFECT_RADIUS;
    final int id = item.getEntityId();
    if (distance < destroyRadius * destroyRadius && !this.removed.contains(id)) {
      final Component message = Message.DISTORTER_DEACTIVATE.build();
      manager.sendMessageToAllLivingSurvivors(message);
      item.remove();
      this.removed.add(id);
    } else if (distance < effectRadius * effectRadius) {
      killer.spawnPlayerSpecificParticle(Particle.ELDER_GUARDIAN);
    }
  }
}
