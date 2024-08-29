package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public final class TrapSeeker extends KillerGadget {

  private static final String TRAP_SEEKER_SOUND = "block.amethyst_block.chime";
  private static final double TRAP_SEEKER_RANGE = 15D;

  public TrapSeeker() {
    super(
        "trap_seeker",
        Material.CLOCK,
        Message.TRAP_SEEKER_NAME.build(),
        Message.TRAP_SEEKER_LORE.build(),
        64);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    if (!(player instanceof final Killer killer)) {
      return true;
    }

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleTrapSeeking(game, killer), 0, 20L);

    final PlayerAudience audience = killer.getAudience();
    final Component message = Message.TRAP_SEEKER_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(TRAP_SEEKER_SOUND);

    return false;
  }

  private void handleTrapSeeking(final Game game, final Killer killer) {

    final GadgetManager manager = game.getGadgetManager();
    final Location origin = killer.getLocation();
    final World world = requireNonNull(origin.getWorld());
    final Collection<Entity> entities =
        world.getNearbyEntities(origin, TRAP_SEEKER_RANGE, TRAP_SEEKER_RANGE, TRAP_SEEKER_RANGE);
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    final Set<Item> gadgets = new HashSet<>();

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
        return;
      }

      gadgets.add(item);
    }

    final Collection<Item> set = killer.getGlowingTraps();
    final MetadataManager metadata = killer.getMetadataManager();
    for (final Item item : gadgets) {
      if (!set.contains(item)) {
        set.add(item);
        metadata.setEntityGlowing(item, ChatColor.YELLOW, true);
      }
    }

    for (final Item entity : set) {
      if (!gadgets.contains(entity)) {
        set.remove(entity);
        metadata.setEntityGlowing(entity, ChatColor.YELLOW, false);
      }
    }
  }
}
