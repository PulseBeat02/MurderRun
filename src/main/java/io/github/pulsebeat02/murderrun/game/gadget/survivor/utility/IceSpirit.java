package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class IceSpirit extends SurvivorGadget {

  public IceSpirit() {
    super(
        "ice_spirit",
        Material.ZOMBIE_HEAD,
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

    final GamePlayer owner = manager.getGamePlayer(player);
    owner.playSound("entity.zombie.ambient");

    final Zombie iceSpirit = this.spawnSpirit(world, location);
    iceSpirit.setTarget(null);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleConditionalTask(
        () -> this.checkInteraction(manager, iceSpirit, nearest), 0, 5L, iceSpirit::isDead);
  }

  private void checkInteraction(
      final PlayerManager manager, final Zombie zombie, final GamePlayer nearest) {
    final Location origin = zombie.getLocation();
    final Location target = nearest.getLocation();
    final double distance = origin.distanceSquared(target);
    nearest.apply(zombie::setTarget);
    if (distance < 4) {
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
    final Component msg = Message.FREEZE_ACTIVATE.build();
    manager.sendMessageToAllSurvivors(msg);
  }

  private Zombie spawnSpirit(final World world, final Location location) {
    return world.spawn(location, Zombie.class, zombie -> {
      zombie.setInvulnerable(true);
      zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
      this.setEquipment(zombie);
      if (zombie instanceof final Ageable ageable) {
        ageable.setBaby();
      }
    });
  }

  private void setEquipment(final Zombie zombie) {
    final EntityEquipment equipment = requireNonNull(zombie.getEquipment());
    equipment.setHelmet(Item.create(Material.DIAMOND_HELMET));
    equipment.setChestplate(Item.create(Material.DIAMOND_CHESTPLATE));
    equipment.setLeggings(Item.create(Material.DIAMOND_LEGGINGS));
    equipment.setBoots(Item.create(Material.DIAMOND_BOOTS));
  }
}
