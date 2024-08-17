package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Forewarn extends KillerGadget {

  private final Multimap<GamePlayer, GamePlayer> glowStates;

  public Forewarn() {
    super(
        "forewarn",
        Material.GLOWSTONE_DUST,
        Message.FOREWARN_NAME.build(),
        Message.FOREWARN_LORE.build(),
        96);
    this.glowStates = ArrayListMultimap.create();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final Component msg = Message.FOREWARN_ACTIVATE.build();
    gamePlayer.sendMessage(msg);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllLivingInnocents(
            survivor -> this.handleForewarn(survivor, gamePlayer)),
        0,
        60);
  }

  private void handleForewarn(final GamePlayer gamePlayer, final GamePlayer player) {

    final Collection<GamePlayer> set = requireNonNull(this.glowStates.get(player));
    if (!(gamePlayer instanceof final Survivor survivor)) {
      return;
    }

    if (survivor.hasCarPart()) {
      set.add(survivor);
      player.setEntityGlowingForPlayer(survivor);
    } else if (set.contains(player)) {
      set.remove(player);
      player.removeEntityGlowingForPlayer(survivor);
    }
  }
}
