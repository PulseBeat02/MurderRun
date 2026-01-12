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
package me.brandonli.murderrun.game.player.phase;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.gadget.Gadget;
import me.brandonli.murderrun.game.gadget.GadgetRegistry;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.Item;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PlayerStartupTool {

  private final GamePlayerManager manager;

  public PlayerStartupTool(final GamePlayerManager manager) {
    this.manager = manager;
  }

  public void configurePlayers() {
    this.manager.applyToKillers(this::handleMurderer);
    this.manager.applyToLivingSurvivors(this::handleInnocent);
  }

  private void handleAll(final GamePlayer gamePlayer) {
    final Location spawn = this.getSpawnLocation();
    final PlayerAudience audience = gamePlayer.getAudience();
    final Game game = gamePlayer.getGame();
    final GameProperties properties = game.getProperties();
    final String sound = properties.getGameStartingSound();
    final SoundStop soundStop = SoundStop.source(Sound.Source.MUSIC);
    gamePlayer.setGameMode(GameMode.SURVIVAL);
    gamePlayer.setWalkSpeed(0.2f);
    gamePlayer.setGravity(true);
    gamePlayer.setHealth(20f);
    gamePlayer.setFoodLevel(20);
    gamePlayer.setSaturation(20);
    gamePlayer.setRespawnLocation(spawn, true);
    audience.stopSound(soundStop);
    audience.playSound(sound);
  }

  private Location getSpawnLocation() {
    final Game game = this.manager.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    return arena.getSpawn();
  }

  public void handleInnocent(final GamePlayer gamePlayer) {
    this.handleAll(gamePlayer);
    this.giveFlashlight(gamePlayer);
    this.sendFlashlightTip(gamePlayer);
  }

  private void sendFlashlightTip(final GamePlayer player) {
    final Game game = player.getGame();
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    final PlayerAudience audience = player.getAudience();
    scheduler.scheduleTask(() -> audience.sendMessage(Message.FLASHLIGHT_TIP.build()), 15 * 20L, reference);
    scheduler.scheduleTask(() -> audience.sendMessage(Message.SPRINT_TIP.build()), 45 * 20L, reference);
  }

  private void giveFlashlight(final GamePlayer player) {
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    final Gadget flashlight = requireNonNull(registry.getGadget("flashlight"));
    final Item.Builder item = flashlight.getStackBuilder();
    final ItemStack stack = item.build();
    final PlayerInventory inventory = player.getInventory();
    inventory.addItem(stack);
  }

  public void handleMurderer(final GamePlayer gamePlayer) {
    this.handleAll(gamePlayer);
    gamePlayer.addPotionEffects(
      new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 1),
      new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, 4),
      new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 4)
    );
    gamePlayer.setGameMode(GameMode.SURVIVAL);
  }
}
