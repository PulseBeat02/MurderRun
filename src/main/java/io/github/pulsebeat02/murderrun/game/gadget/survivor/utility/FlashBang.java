package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.data.GadgetConstants;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.EventUtils;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class FlashBang extends SurvivorGadget implements Listener {

  private final Game game;

  public FlashBang(final Game game) {
    super(
        "flash_bang",
        Material.SNOWBALL,
        Message.FLASHBANG_NAME.build(),
        Message.FLASHBANG_LORE.build(),
        8,
        ItemFactory::createFlashBang);
    this.game = game;
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {
    return super.onGadgetDrop(game, player, item, false);
  }

  @Override
  public void onGadgetRightClick(
      final Game game, final PlayerInteractEvent event, final boolean remove) {
    // ignore impl
  }

  @EventHandler
  public void onProjectileHitEvent(final ProjectileHitEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Snowball snowball)) {
      return;
    }

    final ItemStack stack = snowball.getItem();
    if (!PDCUtils.isFlashBang(stack)) {
      return;
    }

    final Location location = EventUtils.getProjectileLocation(event);
    if (location == null) {
      return;
    }

    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(
        Particle.DUST, location, 25, 0.5, 0.5, 0.5, 0.5, new DustOptions(Color.YELLOW, 4));

    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllMurderers(killer -> {
      final Location killerLocation = killer.getLocation();
      final double distance = killerLocation.distanceSquared(location);
      final double radius = GadgetConstants.FLASHBANG_RADIUS;
      if (distance < radius * radius) {
        final int duration = GadgetConstants.FLASHBANG_DURATION;
        killer.addPotionEffects(
            new PotionEffect(PotionEffectType.BLINDNESS, duration, 0),
            new PotionEffect(PotionEffectType.SLOWNESS, duration, 4));
      }
    });

    manager.playSoundForAllParticipantsAtLocation(location, Sounds.FLASHBANG);
  }
}
