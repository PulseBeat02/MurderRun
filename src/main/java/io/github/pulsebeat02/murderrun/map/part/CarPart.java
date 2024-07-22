package io.github.pulsebeat02.murderrun.map;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.SplittableRandom;

public final class CarPart {

  private static final SplittableRandom RANDOM;

  static {
    RANDOM = new SplittableRandom();
  }

  private final ItemStack stack;
  private Location location;

  public CarPart(final Location location) {
    this.location = location;
    this.stack = this.createItemStack();
    this.spawn();
  }

  public void spawn() {
    final World world = this.location.getWorld();
    final Item item = world.dropItemNaturally(this.location, this.stack);
    final Plugin plugin = MurderRun.getPlugin(MurderRun.class);
    item.setUnlimitedLifetime(true);
    item.setWillAge(false);
    Bukkit.getScheduler().runTaskTimer(plugin, (task) -> this.spawnParticles(task, item), 0, 20);
  }

  public void spawnParticles(final BukkitTask task, final Item item) {
    if (!item.isValid()) {
      task.cancel();
    }
    final World world = this.location.getWorld();
    final Location location = this.location.clone().add(0, 1, 0);
    world.spawnParticle(Particle.EFFECT, location, 10, 0.5, 0.5, 0.5);
  }

  private ItemStack createItemStack() {
    final ItemStack stack = new ItemStack(Material.DIAMOND);
    final ItemMeta meta = this.customize(stack.getItemMeta());
    stack.setItemMeta(meta);
    return stack;
  }

  private ItemMeta customize(final ItemMeta meta) {
    this.tagData(meta);
    final int id = this.randomizeTexture();
    meta.displayName(Locale.CAR_PART_NAME.build());
    meta.setCustomModelData(id);
    if (!meta.hasLore()) {
      final List<Component> components = List.of(Locale.CAR_PART_LORE.build());
      meta.lore(components);
    }
    return meta;
  }

  private void tagData(final ItemMeta meta) {
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final NamespacedKey key = MurderRun.getKey();
    container.set(key, PersistentDataType.STRING, "car_part");
  }

  private int randomizeTexture() {
    return RANDOM.nextInt(4);
  }

  public ItemStack getStack() {
    return this.stack;
  }

  public Location getLocation() {
    return this.location;
  }

  public void setLocation(final Location location) {
    this.location = location;
  }
}
