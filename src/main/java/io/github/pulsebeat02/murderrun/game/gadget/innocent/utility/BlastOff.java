package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public final class BlastOff extends MurderGadget {

  public BlastOff() {
    super(
        "blast_off",
        Material.FIREWORK_ROCKET,
        Locale.BLAST_OFF_TRAP_NAME.build(),
        Locale.BLAST_OFF_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
  }

  private void launchKillerIntoSpace(Player killer, MurderGame game) {

    Location location = killer.getLocation();
    Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
    FireworkMeta meta = firework.getFireworkMeta();
    meta.setPower(2); // Set the power of the firework
    firework.setFireworkMeta(meta);
    firework.addPassenger(killer);

    new BukkitRunnable() {
      @Override
      public void run() {
        if (firework.isDead() || !firework.getPassengers().contains(killer)) {
          firework.remove();
          this.cancel();
          return;
        }
        firework.setVelocity(new Vector(0, 1, 0));
      }
    }.runTaskTimer(game.getPlugin(), 0L, 1L);
  }
}
