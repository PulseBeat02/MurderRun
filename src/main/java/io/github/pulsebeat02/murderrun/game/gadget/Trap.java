package io.github.pulsebeat02.murderrun.game.gadget;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import java.awt.Color;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerDropItemEvent;

public abstract sealed class Trap extends Gadget permits SurvivorTrap, KillerTrap {

  private final Component announcement;
  private final Color color;

  public Trap(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final Component announcement,
      final Color color) {
    super(name, material, itemName, itemLore);
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
  public void onGadgetNearby(final Game game, final GamePlayer activator) {
    this.onTrapActivate(game, activator);
    if (this.announcement != null) {
      final PlayerManager manager = game.getPlayerManager();
      manager.applyToAllParticipants(player -> player.showTitle(this.announcement, empty()));
    }
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, false);

    final Item item = event.getItemDrop();
    final Location location = item.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTaskUntilCondition(
        () -> world.spawnParticle(Particle.DUST, location, 10, 0.2, 0.2, 0.2, this.color),
        0,
        20,
        item::isDead);
  }

  public abstract void onTrapActivate(final Game game, final GamePlayer activee);
}
