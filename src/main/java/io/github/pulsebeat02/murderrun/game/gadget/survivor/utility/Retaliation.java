package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Retaliation extends SurvivorGadget {

  private static final int RETALATION_MAX_AMPLIFIER = 3;
  private static final String RETALIATION_SOUND = "entity.experience_orb.pickup";

  public Retaliation() {
    super(
        "retaliation",
        Material.GOLD_BLOCK,
        Message.RETALIATION_NAME.build(),
        Message.RETALIATION_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final Component message = Message.RETALIATION_ACTIVATE.build();
    final PlayerAudience audience = gamePlayer.getAudience();
    audience.sendMessage(message);
    audience.playSound(RETALIATION_SOUND);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.checkForDeadPlayers(manager, player), 0, 4 * 20L);
  }

  private void checkForDeadPlayers(final PlayerManager manager, final Player player) {

    final Collection<GamePlayer> deathCount = manager.getDead();
    final int dead = deathCount.size();
    if (dead == 0) {
      return;
    }

    final int effectLevel = Math.min(dead - 1, RETALATION_MAX_AMPLIFIER);
    player.addPotionEffects(Set.of(
        new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, effectLevel),
        new PotionEffect(
            PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, effectLevel),
        new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, effectLevel)));
  }
}
