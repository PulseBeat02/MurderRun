package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class DeathHound extends KillerGadget {

  public DeathHound() {
    super(
        "death_hound",
        Material.BONE,
        Locale.DEATH_HOUND_TRAP_NAME.build(),
        Locale.DEATH_HOUND_TRAP_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer nearest = manager.getNearestSurvivor(location);
    if (nearest == null) {
      return;
    }

    this.spawnWolf(location, player, nearest);
  }

  private void spawnWolf(final Location location, final Player owner, final GamePlayer target) {
    final World world = requireNonNull(location.getWorld());
    final Wolf wolf = world.spawn(location, Wolf.class, entity -> {
      entity.setOwner(owner);
      entity.setCustomName("Death Hound");
      entity.setCustomNameVisible(true);
      entity.setTamed(true);
      entity.setAngry(true);
      entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1));
      entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 2));
    });
    target.apply(wolf::setTarget);
  }
}
