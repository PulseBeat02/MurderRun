package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Bush extends SurvivorGadget {

  public Bush() {
    super(
        "bush", Material.OAK_LEAVES, Locale.BUSH_TRAP_NAME.build(), Locale.BUSH_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final GameScheduler scheduler = game.getScheduler();
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10 * 20, 1));
    scheduler.scheduleRepeatedTask(() -> player.teleport(location), 0, 20, 10 * 20);

    final Block block = location.getBlock();
    block.setType(Material.OAK_LEAVES);
    scheduler.scheduleTask(() -> block.setType(Material.AIR), 10 * 20);
  }
}
