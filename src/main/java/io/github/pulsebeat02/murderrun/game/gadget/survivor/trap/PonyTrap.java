package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.HorseInventory;

public final class PonyTrap extends SurvivorTrap {

  public PonyTrap() {
    super(
        "pony",
        Material.SADDLE,
        Message.PONY_NAME.build(),
        Message.PONY_LORE.build(),
        Message.PONY_ACTIVATE.build(),
        16,
        new Color(177, 156, 217));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final Location location = murderer.getLocation();
    final World world = requireNonNull(location.getWorld());
    final Horse horse = this.spawnCustomisedHorse(world, location);
    this.setHorseSpeed(horse);
    murderer.playSound(key("entity.horse.angry"));
  }

  private void setHorseSpeed(final Horse horse) {
    final AttributeInstance attribute =
        requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED));
    attribute.setBaseValue(0.5);
  }

  private Horse spawnCustomisedHorse(final World world, final Location location) {
    return world.spawn(location, Horse.class, horse -> {
      horse.setTamed(true);
      horse.setJumpStrength(2);
      horse.setAdult();
      this.setSaddle(horse);
    });
  }

  private void setSaddle(final Horse horse) {
    final HorseInventory inventory = horse.getInventory();
    inventory.setSaddle(ItemFactory.createSaddle());
  }
}
