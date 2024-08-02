package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerDropItemEvent;
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
  public void onDropEvent(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {
    super.onDropEvent(game, event, true);
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final Zombie iceSpirit = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
    iceSpirit.setBaby(true);
    iceSpirit.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
    iceSpirit.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
    iceSpirit.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
    iceSpirit.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
    game.getScheduler()
        .scheduleTask(
            () -> {
              final MurderPlayerManager manager = game.getPlayerManager();
              manager.applyToAllMurderers(killer -> {
                final Player killerPlayer = killer.getPlayer();
                if (killer.getLocation().distance(iceSpirit.getLocation()) <= 0.5) {
                  killerPlayer.addPotionEffect(
                      new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, Integer.MAX_VALUE));
                  killerPlayer.addPotionEffect(
                      new PotionEffect(PotionEffectType.JUMP_BOOST, 7 * 20, Integer.MAX_VALUE));
                  killerPlayer.setFreezeTicks(7 * 20);
                  manager.applyToAllInnocents(
                      innocent -> innocent.sendMessage(Locale.FREEZE_TRAP_ACTIVATE.build()));
                  iceSpirit.remove();
                } else {
                  iceSpirit.setTarget(killerPlayer);
                }
              });
            },
            20L);
  }
}
