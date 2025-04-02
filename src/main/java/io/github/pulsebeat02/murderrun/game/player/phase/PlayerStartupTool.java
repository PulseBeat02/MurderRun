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
package io.github.pulsebeat02.murderrun.game.player.phase;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetRegistry;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.StrictPlayerReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
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
    final String sound = GameProperties.GAME_STARTING_SOUND;
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
    this.setAttributes(gamePlayer);
  }

  private void setAttributes(final GamePlayer gamePlayer) {
    gamePlayer.apply(player -> player.setWalkSpeed(0.25f));
  }

  private void sendFlashlightTip(final GamePlayer player) {
    final Game game = player.getGame();
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    final PlayerAudience audience = player.getAudience();
    scheduler.scheduleTask(() -> audience.sendMessage(Message.FLASHLIGHT_TIP.build()), 15 * 20L, reference);
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
    gamePlayer.setWalkSpeed(0.4f);
  }
}
