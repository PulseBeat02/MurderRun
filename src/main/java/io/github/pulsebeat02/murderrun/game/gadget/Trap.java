package io.github.pulsebeat02.murderrun.game.gadget;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import java.awt.Color;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerDropItemEvent;

public abstract class Trap extends AbstractGadget {

  private final Component announcement;
  private final Color color;

  public Trap(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final Component announcement,
      final int cost,
      final Color color) {
    super(name, material, itemName, itemLore, cost);
    this.announcement = announcement;
    this.color = color;
  }

  public Component getAnnouncement() {
    return this.announcement;
  }

  public Color getColor() {
    return this.color;
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, false);

    final Item item = event.getItemDrop();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTaskWhenItemFalls(() -> this.scheduleParticleTask(item, scheduler), item);
  }

  private void scheduleParticleTask(final Item item, final GameScheduler scheduler) {
    final Location location = item.getLocation();
    final World world = requireNonNull(location.getWorld());
    final int r = this.color.getRed();
    final int g = this.color.getGreen();
    final int b = this.color.getBlue();
    final org.bukkit.Color bukkitColor = org.bukkit.Color.fromRGB(r, g, b);
    scheduler.scheduleConditionalTask(
        () -> spawnTrapParticles(world, location, bukkitColor), 0, 20, item::isDead);
  }

  private static void spawnTrapParticles(
      final World world, final Location location, final org.bukkit.Color bukkitColor) {
    world.spawnParticle(
        Particle.DUST, location, 10, 0.5, 0.5, 0.5, new DustOptions(bukkitColor, 2));
  }

  public abstract void onTrapActivate(final Game game, final GamePlayer activee);
}
