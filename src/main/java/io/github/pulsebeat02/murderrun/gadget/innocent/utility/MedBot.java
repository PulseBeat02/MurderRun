package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class MedBot extends MurderGadget {

  public MedBot() {
    super(
        "med_bot",
        Material.CHORUS_FLOWER,
        Locale.MED_BOT_TRAP_NAME.build(),
        Locale.MED_BOT_TRAP_LORE.build());
  }

  @Override
  public void onDropEvent(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {
    super.onDropEvent(game, event, true);
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    final ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
    armorStand.setInvisible(true);
    armorStand.setGravity(false);
    armorStand.setMarker(true);

    final EntityEquipment equipment = armorStand.getEquipment();
    if (equipment == null) {
      throw new AssertionError("Failed to spawn Med Bot!");
    }
    equipment.setHelmet(new ItemStack(Material.CHORUS_FLOWER));

    final MurderPlayerManager manager = game.getPlayerManager();
    game.getScheduler()
        .scheduleTask(
            () -> {
              armorStand.teleport(armorStand
                  .getLocation()
                  .add(0, Math.sin(System.currentTimeMillis() / 1000.0) * 0.1, 0));
              armorStand.setRotation(
                  armorStand.getLocation().getYaw() + 10,
                  armorStand.getLocation().getPitch());
              armorStand
                  .getWorld()
                  .spawnParticle(
                      Particle.ENTITY_EFFECT, armorStand.getLocation(), 100, 8, 8, 8, Color.PINK);

              manager.applyToAllInnocents(innocent -> {
                if (innocent.getLocation().distanceSquared(armorStand.getLocation()) <= 64) {
                  innocent.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5, 2));
                }
              });
            },
            40L);

    manager.applyToAllMurderers(killer -> {
      if (killer.getLocation().distanceSquared(armorStand.getLocation()) <= 1) {
        armorStand.remove();
      }
    });
  }
}
