package io.github.pulsebeat02.murderrun.game.gadget;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import java.awt.Color;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;

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
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    final GameScheduler scheduler = game.getScheduler();
    item.setUnlimitedLifetime(true);
    item.setPickupDelay(Integer.MAX_VALUE);
    this.scheduleParticleTask(item, scheduler);

    return false;
  }

  private void scheduleParticleTask(final Item item, final GameScheduler scheduler) {
    final int r = this.color.getRed();
    final int g = this.color.getGreen();
    final int b = this.color.getBlue();
    final org.bukkit.Color bukkitColor = org.bukkit.Color.fromRGB(r, g, b);
    scheduler.scheduleParticleTaskUntilDeath(item, bukkitColor);
  }

  public abstract void onTrapActivate(final Game game, final GamePlayer activee, final Item item);
}
