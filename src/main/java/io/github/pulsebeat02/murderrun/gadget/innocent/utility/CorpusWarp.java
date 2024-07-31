package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.sound.Sound.Source;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class CorpusWarp extends MurderGadget {

  public CorpusWarp() {
    super(
        "corpus_warp",
        Material.PURPLE_STAINED_GLASS,
        Locale.CORPUS_WARP_TRAP_NAME.build(),
        Locale.CORPUS_WARP_TRAP_LORE.build());
  }

  @Override
  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
    super.onDropEvent(game, event);
    final Player player = event.getPlayer();
    final MurderPlayerManager manager = game.getPlayerManager();
    final Collection<GamePlayer> raw = manager.getDead();
    final List<GamePlayer> alive = new ArrayList<>(raw);
    Collections.shuffle(alive);
    if (!alive.isEmpty()) {
      final GamePlayer target = alive.getFirst();
      final Location location = target.getDeathLocation();
      if (location == null) {
        return;
      }
      player.teleport(location);
      target.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, Source.MASTER, 1f, 1f);
    }
  }
}
