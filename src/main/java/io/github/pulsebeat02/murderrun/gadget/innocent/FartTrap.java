package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.resourcepack.sound.FXSound;
import net.kyori.adventure.sound.Sound.Source;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class FartTrap extends SurvivorTrap {

  public FartTrap() {
    super(
        "fart",
        Material.GREEN_WOOL,
        Locale.FART_TRAP_NAME.build(),
        Locale.FART_TRAP_LORE.build(),
        Locale.FART_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
    super.onTrapActivate(game, murderer);
    final Location location = murderer.getLocation();
    murderer.playSound(location, FXSound.FART, Source.MASTER, 1f, 1f);
    murderer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 7 * 20, 3));
    murderer.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 7 * 20, 1));
  }
}
