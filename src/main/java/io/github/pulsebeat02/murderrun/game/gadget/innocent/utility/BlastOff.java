package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.FireworkMeta;

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
    final MurderPlayerManager manager = game.getPlayerManager();
    final GamePlayer killer = manager.getNearestKiller(location);
    if (killer == null) {
      return;
    }

    this.launchKillerIntoSpace(killer, game);

    final Component message = Locale.BLAST_OFF_TRAP_ACTIVATE.build();
    manager.applyToAllInnocents(innocent -> innocent.sendMessage(message));
  }

  private void launchKillerIntoSpace(final GamePlayer killer, final MurderGame game) {

    final Location location = killer.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    killer.apply(player -> {
      final Firework firework = (Firework) world.spawnEntity(location, EntityType.FIREWORK_ROCKET);
      final FireworkMeta meta = firework.getFireworkMeta();
      meta.setPower(2 * 20);
      firework.setFireworkMeta(meta);
      firework.addPassenger(player);
    });
  }
}
