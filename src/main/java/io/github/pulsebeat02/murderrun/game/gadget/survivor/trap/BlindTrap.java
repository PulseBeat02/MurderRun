package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetSettings;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class BlindTrap extends SurvivorTrap {

  public BlindTrap() {
    super(
        "blind",
        Material.BLACK_STAINED_GLASS,
        Message.BLIND_NAME.build(),
        Message.BLIND_LORE.build(),
        Message.BLIND_ACTIVATE.build(),
        GadgetSettings.BLIND_COST,
        Color.BLACK);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final int duration = GadgetSettings.BLIND_DURATION;
    murderer.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0));

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GadgetSettings.BLIND_SOUND);
  }
}
