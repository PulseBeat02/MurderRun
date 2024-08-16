package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class InfraredVision extends KillerGadget {

  public InfraredVision() {
    super(
        "infrared_vision",
        Material.REDSTONE_LAMP,
        Locale.INFRARED_VISION_TRAP_NAME.build(),
        Locale.INFRARED_VISION_TRAP_LORE.build(),
        16);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer killer = manager.lookupPlayer(player).orElseThrow();
    manager.applyToAllInnocents(innocent -> this.setSurvivorGlow(innocent, killer));

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.removeGlow(killer, manager), 7 * 20);
  }

  private void setSurvivorGlow(final Survivor survivor, final GamePlayer killer) {
    final Component msg = Locale.INFRARED_VISION_ACTIVATE.build();
    killer.setEntityGlowingForPlayer(survivor);
    survivor.sendMessage(msg);
  }

  private void removeGlow(final GamePlayer killer, final PlayerManager manager) {
    manager.applyToAllInnocents(
        innocent -> innocent.apply(survivor -> killer.removeEntityGlowingForPlayer(innocent)));
  }
}
