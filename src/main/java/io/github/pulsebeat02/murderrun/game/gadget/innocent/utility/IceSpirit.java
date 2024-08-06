package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class IceSpirit extends MurderGadget {

  public IceSpirit() {
    super(
        "ice_spirit",
        Material.SNOWBALL,
        Locale.ICE_SPIRIT_TRAP_NAME.build(),
        Locale.ICE_SPIRIT_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final MurderPlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    final GamePlayer nearest = manager.getNearestKiller(location);
    if (nearest == null) {
      return;
    }

    final Zombie iceSpirit = this.spawnSpirit(world, location, nearest);
    game.getScheduler().scheduleTask(() -> this.checkInteraction(manager, iceSpirit, nearest), 20L);
  }

  private void checkInteraction(
      final MurderPlayerManager manager, final Zombie zombie, final GamePlayer nearest) {
    final Location origin = zombie.getLocation();
    final Location target = nearest.getLocation();
    final double distance = origin.distanceSquared(target);
    if (distance <= 1) {
      this.applyDebuffs(manager, nearest);
      zombie.remove();
    }
  }

  private void applyDebuffs(final MurderPlayerManager manager, final GamePlayer killer) {
    killer.apply(player -> player.setFreezeTicks(7 * 20));
    killer.addPotionEffects(
        new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, Integer.MAX_VALUE),
        new PotionEffect(PotionEffectType.JUMP_BOOST, 7 * 20, Integer.MAX_VALUE));
    manager.applyToAllInnocents(
        innocent -> innocent.sendMessage(Locale.FREEZE_TRAP_ACTIVATE.build()));
  }

  private Zombie spawnSpirit(final World world, final Location location, final GamePlayer nearest) {
    return world.spawn(location, Zombie.class, zombie -> {
      this.setEquipment(zombie);
      this.setTarget(zombie, nearest);
      zombie.setBaby(true);
    });
  }

  private void setTarget(final Zombie zombie, final GamePlayer nearest) {
    nearest.apply(zombie::setTarget);
  }

  private void setEquipment(final Zombie zombie) {

    final EntityEquipment equipment = zombie.getEquipment();
    if (equipment == null) {
      throw new AssertionError("Zombie doesn't have equipment!");
    }

    equipment.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
    equipment.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
    equipment.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
    equipment.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
  }
}
