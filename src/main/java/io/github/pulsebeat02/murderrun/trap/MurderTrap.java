package io.github.pulsebeat02.murderrun.trap;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public abstract sealed class MurderTrap implements Listener permits SurvivorTrap, KillerTrap {

  private final ItemStack stack;
  private final String name;
  private final Material material;
  private final Component itemName;
  private final Component itemLore;
  private final Component announcement;

  public MurderTrap(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final Component announcement) {
    this.name = name;
    this.material = material;
    this.itemName = itemName;
    this.itemLore = itemLore;
    this.announcement = announcement;
    this.stack = this.constructItemStack();
  }

  public ItemStack constructItemStack(@UnderInitialization MurderTrap this) {
    if (this.itemName == null || this.itemLore == null || this.material == null) {
      throw new AssertionError("Failed to create ItemStack for trap!");
    }
    final String name = AdventureUtils.serializeComponentToLegacy(this.itemName);
    final String rawLore = AdventureUtils.serializeComponentToLegacy(this.itemLore);
    final List<String> lore = List.of(rawLore);
    final ItemStack stack = new ItemStack(this.material);
    final ItemMeta meta = stack.getItemMeta();
    if (meta == null) {
      throw new AssertionError("Failed to construct ItemStack for trap!");
    }
    meta.setDisplayName(name);
    meta.setLore(lore);
    stack.setItemMeta(meta);
    return stack;
  }

  public Component getAnnouncement() {
    return this.announcement;
  }

  public Component getItemLore() {
    return this.itemLore;
  }

  public Component getItemName() {
    return this.itemName;
  }

  public Material getMaterial() {
    return this.material;
  }

  public ItemStack getStack() {
    return this.stack;
  }

  public String getName() {
    return this.name;
  }

  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {}

  public void activate(final MurderGame game, final GamePlayer activee) {
    if (this.announcement == null) {
      return;
    }
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(player -> player.sendMessage(this.announcement));
  }
}
