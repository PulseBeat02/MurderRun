package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundKeys;
import java.awt.Color;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class FartTrap extends SurvivorTrap {

  public FartTrap() {
    super(
        "fart",
        Material.GREEN_WOOL,
        Locale.FART_TRAP_NAME.build(),
        Locale.FART_TRAP_LORE.build(),
        Locale.FART_TRAP_ACTIVATE.build(),
        32,
        Color.GREEN);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final Key key = SoundKeys.FART.getSound().key();
    murderer.playSound(key, Source.MASTER, 1f, 1f);
    murderer.addPotionEffects(
        new PotionEffect(PotionEffectType.SLOWNESS, 7 * 20, 3),
        new PotionEffect(PotionEffectType.NAUSEA, 7 * 20, 1));
  }
}