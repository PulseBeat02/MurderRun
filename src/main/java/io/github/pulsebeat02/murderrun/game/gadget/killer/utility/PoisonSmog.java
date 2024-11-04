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
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PoisonSmog extends KillerGadget {

  public PoisonSmog() {
    super(
      "poison_smog",
      Material.SLIME_BALL,
      Message.POISON_SMOG_NAME.build(),
      Message.POISON_SMOG_LORE.build(),
      GameProperties.POISON_SMOG_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GameScheduler scheduler = game.getScheduler();
    final PlayerManager manager = game.getPlayerManager();
    scheduler.scheduleRepeatedTask(() -> this.handleSmog(world, location, manager), 0, 2 * 20L, GameProperties.POISON_SMOG_DURATION);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.POISON_SMOG_SOUND);

    return false;
  }

  private void handleSmog(final World world, final Location location, final PlayerManager manager) {
    this.spawnSmogParticles(world, location);
    this.handleSurvivors(manager, location);
  }

  private void handleSurvivors(final PlayerManager manager, final Location origin) {
    manager.applyToLivingSurvivors(survivor -> this.handleDebuffs(survivor, origin));
  }

  private void handleDebuffs(final GamePlayer survivor, final Location origin) {
    final Location location = survivor.getLocation();
    final double distance = location.distanceSquared(origin);
    final double radius = GameProperties.POISON_SMOG_RADIUS;
    if (distance < radius * radius) {
      survivor.addPotionEffects(new PotionEffect(PotionEffectType.POISON, 3 * 20, 0));
    }
  }

  private void spawnSmogParticles(final World world, final Location origin) {
    world.spawnParticle(Particle.DUST, origin, 25, 10, 10, 10, new DustOptions(Color.GREEN, 4));
  }
}
