package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;

public final class PonyTrap extends SurvivorTrap {

  public PonyTrap() {
    super(
        "pony",
        Material.HORSE_SPAWN_EGG,
        Locale.PONY_TRAP_NAME.build(),
        Locale.PONY_TRAP_LORE.build(),
        Locale.PONY_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
    super.onTrapActivate(game, murderer);

    final Location location = murderer.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Failed to spawn pony!");
    }

    final Horse horse = (Horse) world.spawnEntity(location, EntityType.HORSE);
    horse.setTamed(true);
    horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
    horse.setJumpStrength(2);
    horse.setAdult();

    final AttributeInstance attribute = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
    if (attribute == null) {
      throw new AssertionError("Couldn't modify pony speed!");
    }
    attribute.setBaseValue(0.5);
  }
}
