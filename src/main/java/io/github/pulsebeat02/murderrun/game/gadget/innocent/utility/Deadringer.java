package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Deadringer extends MurderGadget {

  public Deadringer() {
    super(
        "deadringer",
        Material.WITHER_SKELETON_SKULL,
        Locale.DEADRINGER_TRAP_NAME.build(),
        Locale.DEADRINGER_TRAP_LORE.build());
  }

  @Override
  public void onDropEvent(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onDropEvent(game, event, true);

    final Player player = event.getPlayer();
    final String name = player.getDisplayName();
    final MurderPlayerManager manager = game.getPlayerManager();
    final Component message = Locale.PLAYER_DEATH.build(name);
    manager.applyToAllParticipants(gamePlayer -> gamePlayer.showTitle(message, empty()));
    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15 * 20, 1, true, false));
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.INVISIBILITY, 15 * 20, 1, true, false));
    player.setInvulnerable(true);

    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> player.setInvulnerable(false), 15 * 20);
  }
}
