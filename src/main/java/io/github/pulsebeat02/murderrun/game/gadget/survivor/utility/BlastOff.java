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
package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetRightClickPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.PlayerReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.inventory.meta.FireworkMeta;

public final class BlastOff extends SurvivorGadget {

  public BlastOff() {
    super(
      "blast_off",
      Material.FIREWORK_ROCKET,
      Message.BLAST_OFF_NAME.build(),
      Message.BLAST_OFF_LORE.build(),
      GameProperties.BLAST_OFF_COST
    );
  }

  @Override
  public boolean onGadgetRightClick(final GadgetRightClickPacket packet) {
    return true;
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final Location location = player.getLocation();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer killer = manager.getNearestKiller(location);
    if (killer == null) {
      return true;
    }
    item.remove();

    final Location before = killer.getLocation();
    final Firework firework = this.spawnRocket(killer);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleAfterDeath(() -> this.resetPlayer(killer, before), firework);
    killer.setInvulnerable(true);

    final PlayerReference reference = PlayerReference.of(killer);
    scheduler.scheduleTask(() -> player.setInvulnerable(false), 4 * 20L, reference);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.BLASTOFF_SOUND);

    return false;
  }

  private void resetPlayer(final GamePlayer killer, final Location before) {
    killer.teleport(before);
    killer.setFallDistance(0.0f);
    killer.setCanDismount(true);
  }

  private Firework spawnRocket(final GamePlayer player) {
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    return world.spawn(location, Firework.class, firework -> {
      this.customizeMeta(firework);
      this.customizeProperties(player, firework);
      player.setCanDismount(false);
    });
  }

  private void customizeProperties(final GamePlayer player, final Firework firework) {
    player.apply(internal -> {
      firework.setShotAtAngle(false);
      firework.addPassenger(internal);
    });
  }

  private void customizeMeta(final Firework firework) {
    final FireworkMeta meta = firework.getFireworkMeta();
    meta.setPower(2);
    firework.setFireworkMeta(meta);
  }
}
