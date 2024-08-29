package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

public final class EMPBlast extends KillerGadget {

  private static final int EMP_BLAST_DURATION = 5 * 20;

  public EMPBlast() {
    super(
        "emp_blast",
        Material.TARGET,
        Message.EMP_BLAST_NAME.build(),
        Message.EMP_BLAST_LORE.build(),
        64);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());

    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final BoundingBox box = arena.createBox();

    final GadgetManager manager = game.getGadgetManager();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    final Collection<Entity> entities = world.getNearbyEntities(box);
    this.removeAllSurvivorGadgets(entities, mechanism);

    final PlayerManager playerManager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    playerManager.applyToAllLivingInnocents(survivor -> this.stunSurvivors(scheduler, survivor));
    playerManager.playSoundForAllParticipants(Sounds.FLASHBANG);

    return false;
  }

  private void stunSurvivors(final GameScheduler scheduler, final GamePlayer survivor) {
    final PlayerAudience audience = survivor.getAudience();
    final Component msg = Message.EMP_BLAST_ACTIVATE.build();
    survivor.disableJump(scheduler, EMP_BLAST_DURATION);
    survivor.disableWalkWithFOVEffects(EMP_BLAST_DURATION);
    survivor.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, EMP_BLAST_DURATION, 1));
    audience.sendMessage(msg);
  }

  private void removeAllSurvivorGadgets(
      final Collection<Entity> entities, final GadgetLoadingMechanism mechanism) {

    for (final Entity entity : entities) {

      if (!(entity instanceof final Item item)) {
        continue;
      }

      final ItemStack stack = item.getItemStack();
      final Gadget gadget = mechanism.getGadgetFromStack(stack);
      if (gadget == null) {
        continue;
      }

      if (gadget instanceof KillerGadget) {
        continue;
      }

      item.remove();
    }
  }
}
