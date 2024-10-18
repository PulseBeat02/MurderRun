package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Retaliation extends SurvivorGadget {

  public Retaliation() {
    super(
      "retaliation",
      Material.GOLD_BLOCK,
      Message.RETALIATION_NAME.build(),
      Message.RETALIATION_LORE.build(),
      GameProperties.RETALIATION_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.checkForDeadPlayers(manager, player), 0, 4 * 20L);

    final Component message = Message.RETALIATION_ACTIVATE.build();
    final PlayerAudience audience = player.getAudience();
    audience.sendMessage(message);
    audience.playSound(GameProperties.RETALIATION_SOUND);

    return false;
  }

  private void checkForDeadPlayers(final PlayerManager manager, final GamePlayer player) {
    final Collection<GamePlayer> deathCount = manager.getDead();
    final int dead = deathCount.size();
    if (dead == 0) {
      return;
    }

    final int effectLevel = Math.min(dead - 1, GameProperties.RETALIATION_MAX_AMPLIFIER);
    player.addPotionEffects(
      new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, effectLevel),
      new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, effectLevel),
      new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, effectLevel)
    );
  }
}
