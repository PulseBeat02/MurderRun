package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;

public final class PonyTrap extends SurvivorTrap {

  public PonyTrap() {
    super(
        "pony",
        Material.HORSE_SPAWN_EGG,
        Locale.PONY_TRAP_NAME.build(),
        Locale.PONY_TRAP_LORE.build(),
        Locale.PONY_TRAP_ACTIVATE.build(),
        new Color(177, 156, 217));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final Location location = murderer.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    final Horse horse = this.spawnCustomisedHorse(world, location);
    this.setHorseSpeed(horse);
  }

  private void setHorseSpeed(final Horse horse) {
    final AttributeInstance attribute = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
    if (attribute == null) {
      throw new AssertionError("Couldn't modify pony speed!");
    }
    attribute.setBaseValue(0.5);
  }

  private Horse spawnCustomisedHorse(final World world, final Location location) {
    return world.spawn(location, Horse.class, horse -> {
      horse.setTamed(true);
      horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
      horse.setJumpStrength(2);
      horse.setAdult();
    });
  }
}
