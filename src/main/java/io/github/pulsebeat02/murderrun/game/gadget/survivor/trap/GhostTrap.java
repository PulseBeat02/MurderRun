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

public final class GhostTrap extends SurvivorTrap {

  public GhostTrap() {
    super(
        "ghost",
        Material.WHITE_WOOL,
        Message.GHOST_NAME.build(),
        Message.GHOST_LORE.build(),
        Message.GHOST_ACTIVATE.build(),
        16,
        Color.WHITE);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllLivingInnocents(player -> player.addPotionEffects(
        new PotionEffect(PotionEffectType.INVISIBILITY, 10 * 20, 1),
        new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1)));
    manager.playSoundForAllParticipants("entity.ghast.hurt");
  }
}
