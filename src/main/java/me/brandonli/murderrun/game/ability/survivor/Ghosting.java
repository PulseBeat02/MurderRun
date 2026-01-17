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
    super(
        game,
        GHOSTING_NAME,
        ItemFactory.createAbility(
            GHOSTING_NAME, Message.GHOSTING_NAME.build(), Message.GHOSTING_LORE.build(), 1));
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
      final PlayerDeathTask task =
          new PlayerDeathTask(() -> this.handleGhosting(game, survivor), false);
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
    gamePlayer.addPotionEffects(
        PotionEffectType.INVISIBILITY.createEffect(PotionEffect.INFINITE_DURATION, 1));
  }

  private void createWoolSetting(final Game game, final GamePlayer player) {
    final GameScheduler scheduler = game.getScheduler();
    final PlayerInventory inventory = player.getInventory();
    final ItemStack wool = Item.create(Material.WHITE_WOOL);
    final LoosePlayerReference reference = LoosePlayerReference.of(player);
    final GameProperties properties = game.getProperties();
    scheduler.scheduleRepeatedTask(
        () -> inventory.addItem(wool), 1L, properties.getGhostingWoolDelay(), reference);
  }

  private void giveWhiteBone(final GamePlayer player) {
    final PlayerInventory inventory = player.getInventory();
    final Game game = this.getGame();
    final GameProperties properties = game.getProperties();
    final ItemStack stack = ItemFactory.createKnockBackBone(properties);
    inventory.addItem(stack);
  }
}
