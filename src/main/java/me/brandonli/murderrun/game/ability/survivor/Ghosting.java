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
package me.brandonli.murderrun.game.ability.survivor;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.player.death.DeathManager;
import me.brandonli.murderrun.game.player.death.PlayerDeathTask;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.LoosePlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Ghosting extends SurvivorAbility {

  private static final String GHOSTING_NAME = "ghosting";

  public Ghosting(final Game game) {
    super(game, GHOSTING_NAME, ItemFactory.createAbility(GHOSTING_NAME, Message.GHOSTING_NAME.build(), Message.GHOSTING_LORE.build(), 1));
  }

  @Override
  public void start() {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    manager.applyToLivingSurvivors(participant -> {
      if (!participant.hasAbility(GHOSTING_NAME)) {
        return;
      }
      if (this.invokeEvent(participant)) {
        return;
      }
      final Survivor survivor = (Survivor) participant;
      final DeathManager deathManager = participant.getDeathManager();
      final PlayerDeathTask task = new PlayerDeathTask(() -> this.handleGhosting(game, survivor), false);
      deathManager.addDeathTask(task);
    });
  }

  private void handleGhosting(final Game game, final Survivor gamePlayer) {
    this.setPlayerAttributes(gamePlayer);
    this.giveWhiteBone(gamePlayer);
    this.createWoolSetting(game, gamePlayer);
    this.teleport(game, gamePlayer);
  }

  private void teleport(final Game game, final GamePlayer gamePlayer) {
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location location = arena.getSpawn();
    gamePlayer.setRespawnLocation(location, true);
  }

  private void setPlayerAttributes(final Survivor gamePlayer) {
    gamePlayer.clearInventory();
    gamePlayer.setGameMode(GameMode.SURVIVAL);
    gamePlayer.setCanPickupCarPart(false);
    gamePlayer.setCanPlaceBlocks(true);
    gamePlayer.setInvulnerable(true);
    gamePlayer.addPotionEffects(PotionEffectType.INVISIBILITY.createEffect(PotionEffect.INFINITE_DURATION, 1));
  }

  private void createWoolSetting(final Game game, final GamePlayer player) {
    final GameScheduler scheduler = game.getScheduler();
    final PlayerInventory inventory = player.getInventory();
    final ItemStack wool = Item.create(Material.WHITE_WOOL);
    final LoosePlayerReference reference = LoosePlayerReference.of(player);
    scheduler.scheduleRepeatedTask(() -> inventory.addItem(wool), 1L, GameProperties.GHOSTING_WOOL_DELAY, reference);
  }

  private void giveWhiteBone(final GamePlayer player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack stack = ItemFactory.createKnockBackBone();
    inventory.addItem(stack);
  }
}
