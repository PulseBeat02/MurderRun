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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import static net.kyori.adventure.text.Component.empty;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class MindControl extends SurvivorGadget {

  public MindControl() {
    super(
      "mind_control",
      GameProperties.MIND_CONTROL_COST,
      ItemFactory.createGadget(
        "mind_control",
        GameProperties.MIND_CONTROL_MATERIAL,
        Message.MIND_CONTROL_NAME.build(),
        Message.MIND_CONTROL_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    final GamePlayerManager manager = game.getPlayerManager();
    final Location originLoc = player.getLocation();
    final GamePlayer nearest = manager.getNearestKiller(originLoc);
    if (nearest == null) {
      return true;
    }

    if (!(player instanceof final Survivor survivor)) {
      return true;
    }
    item.remove();

    survivor.setCanPickupCarPart(false);

    final Location location = nearest.getLocation();
    final Location origin = player.getLocation();
    final int duration = GameProperties.MIND_CONTROL_DURATION;
    player.addPotionEffects(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 1));
    player.setInvulnerable(true);
    player.teleport(location);

    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.applyMindControlEffects(player, nearest), 0L, 1L, duration, reference);
    scheduler.scheduleTask(() -> this.resetPlayer(survivor, origin), duration, reference);

    final String targetName = nearest.getDisplayName();
    final Component targetMsg = Message.MIND_CONTROL_ACTIVATE_SURVIVOR.build(targetName);
    final PlayerAudience audience1 = player.getAudience();
    audience1.showTitle(empty(), targetMsg);
    audience1.playSound(GameProperties.MIND_CONTROL_SOUND);

    final String name = player.getDisplayName();
    final Component msg = Message.MIND_CONTROL_ACTIVATE_KILLER.build(name);
    final PlayerAudience audience = nearest.getAudience();
    audience.sendMessage(msg);

    return false;
  }

  private void resetPlayer(final Survivor player, final Location location) {
    player.teleport(location);
    player.setInvulnerable(false);
    player.setCanPickupCarPart(true);
  }

  private void applyMindControlEffects(final GamePlayer player, final GamePlayer killer) {
    final Location location = player.getLocation();
    final Vector velocity = player.getVelocity();
    killer.teleport(location);
    killer.setVelocity(velocity);
  }
}
