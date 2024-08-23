package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EntityEquipment;

public final class IceSpirit extends SurvivorGadget {

  public IceSpirit() {
    super(
        "ice_spirit",
        Material.SNOWBALL,
        Message.ICE_SPIRIT_NAME.build(),
        Message.ICE_SPIRIT_LORE.build(),
        16);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GamePlayer nearest = manager.getNearestKiller(location);
    if (nearest == null) {
      return;
    }

    final Zombie iceSpirit = this.spawnSpirit(world, location, nearest);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.checkInteraction(manager, iceSpirit, nearest), 20L);
  }

  private void checkInteraction(
      final PlayerManager manager, final Zombie zombie, final GamePlayer nearest) {
    final Location origin = zombie.getLocation();
    final Location target = nearest.getLocation();
    final double distance = origin.distanceSquared(target);
    if (distance < 1) {
      this.applyDebuffs(manager, nearest);
      zombie.remove();
    }
  }

  private void applyDebuffs(final PlayerManager manager, final GamePlayer killer) {
    final Game game = manager.getGame();
    final GameScheduler scheduler = game.getScheduler();
    killer.disableJump(scheduler, 7 * 20L);
    killer.apply(player -> player.setFreezeTicks(7 * 20));
    killer.disableWalkWithFOVEffects(10 * 20);
    manager.applyToAllLivingInnocents(
        innocent -> innocent.sendMessage(Message.FREEZE_ACTIVATE.build()));
  }

  private Zombie spawnSpirit(final World world, final Location location, final GamePlayer nearest) {
    return world.spawn(location, Zombie.class, zombie -> {
      this.setEquipment(zombie);
      this.setTarget(zombie, nearest);
      if (zombie instanceof final Ageable ageable) {
        ageable.setBaby();
      }
    });
  }

  private void setTarget(final Zombie zombie, final GamePlayer nearest) {
    nearest.apply(zombie::setTarget);
  }

  private void setEquipment(final Zombie zombie) {
    final EntityEquipment equipment = requireNonNull(zombie.getEquipment());
    equipment.setHelmet(Item.create(Material.DIAMOND_HELMET));
    equipment.setChestplate(Item.create(Material.DIAMOND_CHESTPLATE));
    equipment.setLeggings(Item.create(Material.DIAMOND_LEGGINGS));
    equipment.setBoots(Item.create(Material.DIAMOND_BOOTS));
  }
}
