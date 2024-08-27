package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class BlindTrap extends SurvivorTrap {

  private static final int BLIND_TRAP_DURATION = 7 * 20;
  private static final String BLIND_TRAP_SOUND = "entity.ghast.scream";

  public BlindTrap() {
    super(
        "blind",
        Material.BLACK_STAINED_GLASS,
        Message.BLIND_NAME.build(),
        Message.BLIND_LORE.build(),
        Message.BLIND_ACTIVATE.build(),
        16,
        Color.BLACK);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final PlayerManager manager = game.getPlayerManager();
    murderer.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, BLIND_TRAP_DURATION, 0));
    manager.playSoundForAllParticipants(BLIND_TRAP_SOUND);
  }
}
