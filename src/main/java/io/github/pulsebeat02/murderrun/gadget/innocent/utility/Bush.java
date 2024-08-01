package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Bush extends MurderGadget {

  public Bush() {
    super(
        "bush", Material.OAK_LEAVES, Locale.BUSH_TRAP_NAME.build(), Locale.BUSH_TRAP_LORE.build());
  }

  @Override
  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
    super.onDropEvent(game, event);
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    location.getBlock().setType(Material.OAK_LEAVES);
    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10 * 20, 1));
    game.getScheduler()
        .scheduleRepeatingTaskDuration(
            () -> {
              player.teleport(location);
            },
            0,
            20,
            10 * 20);
    game.getScheduler().scheduleTask(() -> location.getBlock().setType(Material.AIR), 10 * 20);
  }
}
