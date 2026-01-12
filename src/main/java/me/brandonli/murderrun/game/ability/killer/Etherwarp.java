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
package me.brandonli.murderrun.game.ability.killer;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameStatus;
import me.brandonli.murderrun.game.player.*;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class Etherwarp extends KillerAbility implements Listener {

  private static final String ETHERWARP_NAME = "etherwarp";

  private final Map<GamePlayer, Long> cooldowns;
  private final Map<GamePlayer, Integer> sneakingPlayers;
  private final Map<Integer, EtherwarpBlockSelector> tasks;
  private final Map<GamePlayer, Location> targetBlock;

  public Etherwarp(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      game,
      ETHERWARP_NAME,
      ItemFactory.createAbility(
        ETHERWARP_NAME,
        Message.ETHERWARP_NAME.build(),
        Message.ETHERWARP_LORE.build(),
        (int) (properties.getEtherwarpCooldown() * 20)
      )
    );
    this.cooldowns = new HashMap<>();
    this.sneakingPlayers = new HashMap<>();
    this.targetBlock = new HashMap<>();
    this.tasks = new HashMap<>();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerRightClick(final PlayerInteractEvent event) {
    final Action action = event.getAction();
    if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final GameStatus status = game.getStatus();
    final GameStatus.Status currentStatus = status.getStatus();
    if (currentStatus == GameStatus.Status.NOT_STARTED) {
      return;
    }

    if (!gamePlayer.hasAbility(ETHERWARP_NAME)) {
      return;
    }

    final ItemStack item = event.getItem();
    if (item == null) {
      return;
    }

    if (!PDCUtils.isAbility(item)) {
      return;
    }

    if (!this.sneakingPlayers.containsKey(gamePlayer)) {
      if (currentStatus == GameStatus.Status.SURVIVORS_RELEASED) {
        player.setCooldown(item, 0);
        event.setCancelled(true);
        return;
      }
      return;
    }

    if (!this.targetBlock.containsKey(gamePlayer)) {
      if (currentStatus == GameStatus.Status.SURVIVORS_RELEASED) {
        player.setCooldown(item, 0);
        event.setCancelled(true);
        return;
      }
      return;
    }

    if (this.invokeEvent(gamePlayer)) {
      return;
    }

    final GameProperties properties = game.getProperties();
    final int cooldown = (int) (properties.getEtherwarpCooldown() * 1000);
    if (this.cooldowns.containsKey(gamePlayer)) {
      final long last = this.cooldowns.get(gamePlayer);
      final long current = System.currentTimeMillis();
      final long timeElapsed = current - last;
      if (timeElapsed < cooldown) {
        event.setCancelled(true);
        return;
      }
    }

    this.applyTeleport(gamePlayer, game, player);

    final long current = System.currentTimeMillis();
    this.cooldowns.put(gamePlayer, current);
    gamePlayer.setAbilityCooldowns(ETHERWARP_NAME, (int) (properties.getEtherwarpCooldown() * 20));
  }

  private void applyTeleport(final GamePlayer gamePlayer, final Game game, final Player player) {
    final Integer id = requireNonNull(this.sneakingPlayers.remove(gamePlayer));
    final EtherwarpBlockSelector task = requireNonNull(this.tasks.remove(id));
    final GameScheduler scheduler = game.getScheduler();
    scheduler.cancelTask(id);

    final AtomicReference<Block> reference = task.getReference();
    final Block currentBlock = reference.get();
    if (currentBlock != null) {
      final MetadataManager metadata = gamePlayer.getMetadataManager();
      metadata.setBlockGlowing(currentBlock, ChatColor.WHITE, false);
    }

    final Location targetLocation = requireNonNull(this.targetBlock.remove(gamePlayer));
    player.teleport(targetLocation);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerSneak(final PlayerToggleSneakEvent event) {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final GameStatus status = game.getStatus();
    final GameStatus.Status currentStatus = status.getStatus();
    if (currentStatus == GameStatus.Status.NOT_STARTED) {
      return;
    }

    if (!gamePlayer.hasAbility(ETHERWARP_NAME)) {
      return;
    }

    final boolean sneaking = event.isSneaking();
    if (!sneaking) {
      final PlayerAudience audience = gamePlayer.getAudience();
      final Integer id = this.sneakingPlayers.remove(gamePlayer);
      if (id != null) {
        final GameScheduler scheduler = game.getScheduler();
        scheduler.cancelTask(id);

        final EtherwarpBlockSelector task = requireNonNull(this.tasks.remove(id));
        final AtomicReference<Block> reference = task.getReference();
        final Block current = reference.get();
        if (current != null) {
          final MetadataManager metadata = gamePlayer.getMetadataManager();
          metadata.setBlockGlowing(current, ChatColor.WHITE, false);
        }
      }
      audience.setActionBar(empty());
      return;
    }

    final PlayerInventory inventory = gamePlayer.getInventory();
    final ItemStack item = inventory.getItemInMainHand();
    if (!PDCUtils.isAbility(item)) {
      return;
    }

    final boolean killer = gamePlayer instanceof Killer;
    if (currentStatus == GameStatus.Status.SURVIVORS_RELEASED && killer) {
      player.setCooldown(item, 0);
      event.setCancelled(true);
      return;
    }

    if (this.sneakingPlayers.containsKey(gamePlayer)) {
      return;
    }

    final GameProperties properties = game.getProperties();
    final int cooldown = (int) (properties.getEtherwarpCooldown() * 1000);
    if (this.cooldowns.containsKey(gamePlayer)) {
      final long last = this.cooldowns.get(gamePlayer);
      final long current = System.currentTimeMillis();
      final long timeElapsed = current - last;
      if (timeElapsed < cooldown) {
        event.setCancelled(true);
        return;
      }
    }

    this.sneakingPlayers.put(gamePlayer, this.getTask(gamePlayer));
  }

  private int getTask(final GamePlayer player) {
    final Game game = this.getGame();
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    final EtherwarpBlockSelector selector = new EtherwarpBlockSelector(player);
    final BukkitTask task = scheduler.scheduleRepeatedTask(selector, 1L, 1L, reference);
    final int id = task.getTaskId();
    this.tasks.put(id, selector);
    return id;
  }

  private final class EtherwarpBlockSelector extends BukkitRunnable {

    private final GamePlayer player;
    private final AtomicReference<Block> reference;

    public EtherwarpBlockSelector(final GamePlayer player) {
      this.player = player;
      this.reference = new AtomicReference<>();
    }

    @Override
    public void run() {
      final Game game = Etherwarp.this.getGame();
      final GameProperties properties = game.getProperties();
      final int max = properties.getEtherwarpMaxDistance();
      final PlayerAudience audience = this.player.getAudience();
      final Block block = this.player.getTargetBlockExact(max);
      final Block current = this.reference.get();
      final MetadataManager metadata = this.player.getMetadataManager();
      if (block == null) {
        Etherwarp.this.targetBlock.remove(this.player);
        if (current != null) {
          metadata.setBlockGlowing(current, ChatColor.RED, false);
          metadata.setBlockGlowing(current, ChatColor.GREEN, false);
        }
        audience.setActionBar(Message.EITHERWARP_FAR.build());
        return;
      }
      if (current != block) {
        if (current != null) {
          metadata.setBlockGlowing(current, ChatColor.RED, false);
          metadata.setBlockGlowing(current, ChatColor.GREEN, false);
        }
        this.reference.set(block);
      }
      final Material blockType = block.getType();
      if (!blockType.isSolid()) {
        Etherwarp.this.targetBlock.remove(this.player);
        metadata.setBlockGlowing(block, ChatColor.RED, true);
        audience.setActionBar(Message.EITHERWARP_INVALID.build());
        return;
      }
      final Location blockLocation = block.getLocation();
      final Location target = blockLocation.add(0.5, 1, 0.5);
      final Block targetedBlock = target.getBlock();
      final Block aboveTargetBlock = targetedBlock.getRelative(BlockFace.UP);
      final Material targetBlockType = targetedBlock.getType();
      final Material aboveTargetBlockType = aboveTargetBlock.getType();
      if (targetBlockType.isSolid() || aboveTargetBlockType.isSolid()) {
        Etherwarp.this.targetBlock.remove(this.player);
        metadata.setBlockGlowing(block, ChatColor.RED, true);
        audience.setActionBar(Message.EITHERWARP_INVALID2.build());
        return;
      }
      audience.setActionBar(empty());
      metadata.setBlockGlowing(block, ChatColor.GREEN, true);
      final Location safeLocation = blockLocation.add(0, 1, 0);
      Etherwarp.this.targetBlock.put(this.player, safeLocation);
    }

    public AtomicReference<Block> getReference() {
      return this.reference;
    }
  }
}
