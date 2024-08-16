package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerApparatus;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
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

public final class TrapSeeker extends KillerGadget {

  private final Multimap<GamePlayer, Item> glowItemStates;

  public TrapSeeker() {
    super(
        "trap_seeker",
        Material.CLOCK,
        Locale.TRAP_SEEKER_TRAP_NAME.build(),
        Locale.TRAP_SEEKER_TRAP_LORE.build(),
        64);
    this.glowItemStates = ArrayListMultimap.create();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final Component message = Locale.TRAP_SEEKER_ACTIVATE.build();
    gamePlayer.sendMessage(message);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleTrapSeeking(game, gamePlayer), 0, 2 * 20L);
  }

  private void handleTrapSeeking(final Game game, final GamePlayer innocent) {

    final GadgetManager manager = game.getGadgetManager();
    final Location origin = innocent.getLocation();
    final World world = requireNonNull(origin.getWorld());
    final int range = 7;
    final Collection<Entity> entities = world.getNearbyEntities(origin, range, range, range);
    final GadgetLoadingMechanism mechanism = manager.getMechanism();

    for (final Entity entity : entities) {

      if (!(entity instanceof final Item item)) {
        continue;
      }

      final ItemStack stack = item.getItemStack();
      final Gadget gadget = mechanism.getGadgetFromStack(stack);
      if (gadget == null) {
        continue;
      }

      final Collection<Item> set = requireNonNull(this.glowItemStates.get(innocent));
      final boolean survivor = gadget instanceof KillerApparatus;
      if (survivor) {
        set.add(item);
        innocent.setEntityGlowingForPlayer(item);
      } else if (set.contains(entity)) {
        set.remove(entity);
        innocent.removeEntityGlowingForPlayer(item);
      }
    }
  }
}
