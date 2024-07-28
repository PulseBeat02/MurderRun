package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CorpusWarp extends SurvivorTrap {

    public CorpusWarp() {
        super(
                "Corpus Warp",
                Material.SKELETON_SKULL,
                Locale.CORPUS_WARP_TRAP_NAME.build(),
                Locale.CORPUS_WARP_TRAP_LORE.build(),
                null);
    }

    @Override
    public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
        super.onDropEvent(game, event);
        final Player player = event.getPlayer();
        final PlayerManager manager = game.getPlayerManager();
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
            target.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 1, 1);
        }
    }
}
