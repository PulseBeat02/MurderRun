package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.inventory.HorseInventory;

public final class PonyTrap extends SurvivorTrap {

  public PonyTrap() {
    super(
      "pony",
      Material.SADDLE,
      Message.PONY_NAME.build(),
      Message.PONY_LORE.build(),
      Message.PONY_ACTIVATE.build(),
      GameProperties.PONY_COST,
      new Color(177, 156, 217)
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final Location location = murderer.getLocation();
    this.spawnHorse(location);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.PONY_SOUND);
  }

  private void spawnHorse(final Location location) {
    final World world = requireNonNull(location.getWorld());
    world.spawn(location, Horse.class, horse -> {
      this.customizeProperties(horse);
      this.setSaddle(horse);
    });
  }

  private void customizeProperties(final Horse horse) {
    horse.setTamed(true);
    horse.setJumpStrength(2);
    horse.setAdult();
    this.setSpeed(horse);
  }

  private void setSpeed(final Horse horse) {
    final AttributeInstance attribute = requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED));
    attribute.setBaseValue(GameProperties.PONY_HORSE_SPEED);
  }

  private void setSaddle(final Horse horse) {
    final HorseInventory inventory = horse.getInventory();
    inventory.setSaddle(ItemFactory.createSaddle());
  }
}
