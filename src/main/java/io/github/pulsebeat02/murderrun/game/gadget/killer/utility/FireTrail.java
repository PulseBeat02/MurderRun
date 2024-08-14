package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class FireTrail extends KillerGadget {

  public FireTrail() {
    super(
        "fire_trail",
        Material.FLINT_AND_STEEL,
        Locale.FIRE_TRAIL_TRAP_NAME.build(),
        Locale.FIRE_TRAIL_TRAP_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> {
          final Location location = player.getLocation();
          final Block block = location.getBlock();
          final Block replace = block.getRelative(BlockFace.UP);
          replace.setType(Material.FIRE);
        },
        0,
        10);
  }
}
