package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class StarTrap extends SurvivorTrap {

  public StarTrap() {
    super(
        "star",
        Material.FIREWORK_STAR,
        Locale.STAR_TRAP_NAME.build(),
        Locale.STAR_TRAP_LORE.build(),
        Locale.STAR_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
    super.onTrapActivate(game, murderer);
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllInnocents(this::addPotionEffect);
  }

  private void addPotionEffect(final GamePlayer player) {
    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, 2));
    player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 5, 2));
    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5, 2));
  }
}
