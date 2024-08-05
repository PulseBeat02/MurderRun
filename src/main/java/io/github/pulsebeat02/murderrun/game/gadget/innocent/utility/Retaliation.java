package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Retaliation extends MurderGadget {

  private static final int MAX_DEATHS_COUNTED = 3;

  public Retaliation() {
    super(
        "retaliation",
        Material.GOLD_BLOCK,
        Locale.RETALIATION_TRAP_NAME.build(),
        Locale.RETALIATION_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final MurderPlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final Component message = Locale.RETALIATION_TRAP_ACTIVATE.build();
    gamePlayer.sendMessage(message);

    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.checkForDeadPlayers(manager, player), 80L);
  }

  private void checkForDeadPlayers(final MurderPlayerManager manager, final Player player) {
    final Collection<GamePlayer> deathCount = manager.getDead();
    final int dead = deathCount.size();
    final int effectLevel = Math.min(dead, MAX_DEATHS_COUNTED);
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, effectLevel));
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, effectLevel));
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, effectLevel));
  }
}
