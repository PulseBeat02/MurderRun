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
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class Ghosting extends SurvivorGadget {

  public Ghosting() {
    super("ghosting", Material.WHITE_WOOL, Message.GHOSTING_NAME.build(), Message.GHOSTING_LORE.build(), GameProperties.GHOSTING_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final org.bukkit.entity.Item item = packet.getItem();

    if (!(player instanceof final Survivor survivor)) {
      return true;
    }
    item.remove();

    final DeathManager deathManager = player.getDeathManager();
    final PlayerDeathTask task = new PlayerDeathTask(() -> this.handleGhosting(game, survivor), false);
    deathManager.addDeathTask(task);

    final PlayerAudience audience = survivor.getAudience();
    final Component message = Message.GHOSTING_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GameProperties.GHOSTING_SOUND);

    return false;
  }

  private void handleGhosting(final Game game, final Survivor gamePlayer) {
    this.setPlayerAttributes(gamePlayer);
    this.giveWhiteLeatherArmor(gamePlayer);
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
  }

  private void createWoolSetting(final Game game, final GamePlayer player) {
    final GameScheduler scheduler = game.getScheduler();
    final PlayerInventory inventory = player.getInventory();
    final ItemStack wool = Item.create(Material.WHITE_WOOL);
    scheduler.scheduleRepeatedTask(() -> inventory.addItem(wool), 0, GameProperties.GHOSTING_WOOL_DELAY);
  }

  private void giveWhiteBone(final GamePlayer player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack stack = ItemFactory.createKnockBackBone();
    inventory.addItem(stack);
  }

  private void giveWhiteLeatherArmor(final GamePlayer player) {
    final PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(ItemFactory.createGhostGear(Material.LEATHER_HELMET));
    inventory.setChestplate(ItemFactory.createGhostGear(Material.LEATHER_CHESTPLATE));
    inventory.setLeggings(ItemFactory.createGhostGear(Material.LEATHER_LEGGINGS));
    inventory.setBoots(ItemFactory.createGhostGear(Material.LEATHER_BOOTS));
  }
}
