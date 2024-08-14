package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class CorpusWarp extends SurvivorGadget {

  public CorpusWarp() {
    super(
        "corpus_warp",
        Material.PURPLE_STAINED_GLASS,
        Locale.CORPUS_WARP_TRAP_NAME.build(),
        Locale.CORPUS_WARP_TRAP_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final Collection<GamePlayer> raw = manager.getDead();
    final List<GamePlayer> alive = new ArrayList<>(raw);
    if (alive.isEmpty()) {
      return;
    }

    Collections.shuffle(alive);
    final GamePlayer target = alive.getFirst();
    final Location location = target.getDeathLocation();
    if (location == null) {
      return;
    }

    final Key key = key("entity.enderman.teleport");
    player.teleport(location);
    target.playSound(key, Source.MASTER, 1f, 1f);
  }
}
