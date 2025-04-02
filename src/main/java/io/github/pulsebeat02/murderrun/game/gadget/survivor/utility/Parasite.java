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

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.HashSet;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Parasite extends SurvivorGadget {

  private final Set<Integer> removed;

  public Parasite() {
    super(
      "parasite",
      GameProperties.PARASITE_COST,
      ItemFactory.createGadget("parasite", GameProperties.PARSITE_MATERIAL, Message.PARASITE_NAME.build(), Message.PARASITE_LORE.build())
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
    scheduler.scheduleParticleTaskUntilDeath(item, Color.GREEN);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.PARASITE_SOUND);

    return false;
  }

  private void handleKillers(final GamePlayerManager manager, final Item item) {
    manager.applyToKillers(killer -> this.checkActivationDistance(killer, manager, item));
  }

  private void checkActivationDistance(final GamePlayer player, final GamePlayerManager manager, final Item item) {
    final Location origin = item.getLocation();
    final Location location = player.getLocation();
    final double distance = origin.distanceSquared(location);
    final double destroyRadius = GameProperties.PARASITE_DESTROY_RADIUS;
    final double radius = GameProperties.PARASITE_RADIUS;
    final int id = item.getEntityId();
    if (distance < destroyRadius * destroyRadius && !this.removed.contains(id)) {
      final Component message = Message.PARASITE_DEACTIVATE.build();
      manager.sendMessageToAllLivingSurvivors(message);
      item.remove();
      this.removed.add(id);
    } else if (distance < radius * radius) {
      player.addPotionEffects(
        new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, 0),
        new PotionEffect(PotionEffectType.POISON, 10 * 20, 0),
        new PotionEffect(PotionEffectType.WEAKNESS, 10 * 20, 0)
      );
    }
  }
}
