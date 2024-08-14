package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

public final class EMPBlast extends KillerGadget {

  public EMPBlast() {
    super(
        "emp_blast",
        Material.SNOWBALL,
        Locale.EMP_BLAST_TRAP_NAME.build(),
        Locale.EMP_BLAST_TRAP_LORE.build(),
        96);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());

    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location[] corners = arena.getCorners();
    final BoundingBox box = BoundingBox.of(corners[0], corners[1]);

    final GadgetManager manager = game.getGadgetManager();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    final Collection<Entity> entities = world.getNearbyEntities(box);
    for (final Entity entity : entities) {

      if (!(entity instanceof final Item item)) {
        continue;
      }

      final ItemStack stack = item.getItemStack();
      final Gadget gadget = mechanism.getGadgetFromStack(stack);
      if (gadget == null) {
        continue;
      }

      if (!(gadget instanceof SurvivorGadget)) {
        continue;
      }

      item.remove();
    }

    final PlayerManager playerManager = game.getPlayerManager();
    final Component msg = Locale.EMP_BLAST_ACTIVATE.build();
    playerManager.applyToAllInnocents(survivor -> {
      survivor.addPotionEffects(
          new PotionEffect(PotionEffectType.SLOWNESS, 7 * 20, 1),
          new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 1),
          new PotionEffect(PotionEffectType.JUMP_BOOST, 5 * 20, 0));
      survivor.sendMessage(msg);
    });
  }
}
