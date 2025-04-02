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
package io.github.pulsebeat02.murderrun.game.ability.survivor;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class TrapVest extends SurvivorAbility {

  private static final String TRAP_VEST_NAME = "trap_vest";

  public TrapVest(final Game game) {
    super(
      game,
      TRAP_VEST_NAME,
      ItemFactory.createAbility(TRAP_VEST_NAME, Message.TRAP_VEST_NAME.build(), Message.TRAP_VEST_LORE.build(), 1)
    );
  }

  @Override
  public void start() {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    manager.applyToLivingSurvivors(participant -> {
      if (!participant.hasAbility(TRAP_VEST_NAME)) {
        return;
      }
      final DeathManager deathManager = participant.getDeathManager();
      final PlayerDeathTask task = new PlayerDeathTask(() -> this.handleTrapVest(participant), false);
      deathManager.addDeathTask(task);
    });
  }

  private void handleTrapVest(final GamePlayer gamePlayer) {
    this.teleport(gamePlayer);
    this.handleTraps(gamePlayer);
  }

  private void teleport(final GamePlayer gamePlayer) {
    final Game game = this.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location location = arena.getSpawn();
    gamePlayer.setRespawnLocation(location, true);
  }

  private void handleTraps(final GamePlayer player) {
    final Game game = this.getGame();
    final GadgetManager manager = game.getGadgetManager();
    final Location location = requireNonNull(player.getDeathLocation());
    final DeathManager deathManager = player.getDeathManager();
    final List<ItemStack> drops = deathManager.getDeathLoot();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    final double multiplier = GameProperties.TRAP_VEST_VELOCITY;
    final World world = requireNonNull(location.getWorld());
    for (final ItemStack slot : drops) {
      if (slot == null) {
        continue;
      }
      if (PDCUtils.isAbility(slot)) {
        continue;
      }
      final int count = slot.getAmount();
      for (int i = 0; i < count; i++) {
        final Item droppedItem = world.dropItem(location, slot);
        final ItemStack stack = droppedItem.getItemStack();
        final Gadget gadget = mechanism.getGadgetFromStack(stack);
        if (gadget instanceof SurvivorTrap) {
          final Vector velocity = new Vector(
            (RandomUtils.generateDouble() - 0.5) * multiplier,
            RandomUtils.generateDouble() * multiplier,
            (RandomUtils.generateDouble() - 0.5) * multiplier
          );
          droppedItem.setVelocity(velocity);
        }
      }
    }
  }
}
