package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Forewarn extends KillerGadget {

  private static final String FOREWARN_SOUND = "entity.phantom.ambient";

  public Forewarn() {
    super(
        "forewarn",
        Material.GLOWSTONE_DUST,
        Message.FOREWARN_NAME.build(),
        Message.FOREWARN_LORE.build(),
        128);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    if (!(player instanceof final Killer killer)) {
      return true;
    }

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleInnocents(manager, killer), 0, 20L);

    final PlayerAudience audience = player.getAudience();
    final Component msg = Message.FOREWARN_ACTIVATE.build();
    audience.sendMessage(msg);
    audience.playSound(FOREWARN_SOUND);

    return false;
  }

  private void handleInnocents(final PlayerManager manager, final Killer gamePlayer) {
    manager.applyToAllLivingInnocents(survivor -> this.handleForewarn(survivor, gamePlayer));
  }

  private void handleForewarn(final GamePlayer gamePlayer, final Killer player) {

    final Collection<GamePlayer> set = player.getForewarnGlowing();
    if (!(gamePlayer instanceof final Survivor survivor)) {
      return;
    }

    final MetadataManager metadata = player.getMetadataManager();
    if (survivor.hasCarPart()) {
      set.add(survivor);
      metadata.setEntityGlowing(survivor, ChatColor.RED, true);
    } else if (set.contains(survivor)) {
      set.remove(player);
      metadata.setEntityGlowing(survivor, ChatColor.RED, false);
    }
  }
}
