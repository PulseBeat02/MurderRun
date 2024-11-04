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
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Item;

public final class BurnTheBody extends KillerGadget {

  public BurnTheBody() {
    super(
      "burn_the_body",
      Material.RED_STAINED_GLASS,
      Message.BURN_THE_BODY_NAME.build(),
      Message.BURN_THE_BODY_LORE.build(),
      GameProperties.BURN_THE_BODY_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    final Location location = player.getLocation();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer closest = manager.getNearestDeadSurvivor(location);
    if (closest == null) {
      return true;
    }

    final Location deathLocation = requireNonNull(closest.getDeathLocation());
    final double distance = location.distanceSquared(deathLocation);
    final double radius = GameProperties.BURN_THE_BODY_RADIUS;
    if (distance > radius * radius) {
      return true;
    }
    item.remove();

    final GameScheduler scheduler = game.getScheduler();
    this.destroyBody(scheduler, closest, deathLocation);
    manager.playSoundForAllParticipants(GameProperties.BURN_THE_BODY_SOUND);

    return false;
  }

  private void destroyBody(final GameScheduler scheduler, final GamePlayer victim, final Location deathLocation) {
    final World world = requireNonNull(deathLocation.getWorld());
    scheduler.scheduleRepeatedTask(() -> this.summonEffects(deathLocation, world), 0, 20L, 5 * 20L);
    scheduler.scheduleTask(() -> this.handleBurnTasks(victim), 100);
  }

  private void summonEffects(final Location deathLocation, final World world) {
    world.spawnParticle(Particle.LAVA, deathLocation, 15, 1, 1, 1);
    world.strikeLightningEffect(deathLocation);
  }

  private void handleBurnTasks(final GamePlayer victim) {
    final DeathManager manager = victim.getDeathManager();
    final NPC stand = manager.getCorpse();
    if (stand != null) {
      stand.despawn();
    }

    victim.setLastDeathLocation(null);
  }
}
