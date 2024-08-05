package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Parasite extends MurderGadget {

  public Parasite() {
    super(
        "parasite",
        Material.VINE,
        Locale.PARASITE_TRAP_NAME.build(),
        Locale.PARASITE_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, false);

    final Item item = event.getItemDrop();
    final Player player = event.getPlayer();
    final Location origin = player.getLocation();
    final MurderPlayerManager manager = game.getPlayerManager();
    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTaskUntilCondition(
        () -> manager.applyToAllMurderers(
            killer -> this.checkActivationDistance(killer, origin, manager, item)),
        0,
        40,
        item::isDead);
  }

  private void checkActivationDistance(
      final GamePlayer player,
      final Location origin,
      final MurderPlayerManager manager,
      final Item item) {
    final Location location = player.getLocation();
    final double distance = origin.distanceSquared(location);
    if (distance <= 1) {
      final Component message = Locale.PARASITE_TRAP_DEACTIVATE.build();
      manager.applyToAllInnocents(survivor -> survivor.sendMessage(message));
      item.remove();
    } else if (distance <= 100) {
      player.addPotionEffects(
          new PotionEffect(PotionEffectType.SLOWNESS, 10, 1),
          new PotionEffect(PotionEffectType.POISON, 10, 1),
          new PotionEffect(PotionEffectType.WEAKNESS, 10, 1));
    }
  }
}
