package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorApparatus;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
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
        Message.TRAP_SEEKER_NAME.build(),
        Message.TRAP_SEEKER_LORE.build(),
        64);
    this.glowItemStates = HashMultimap.create();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final Component message = Message.TRAP_SEEKER_ACTIVATE.build();
    gamePlayer.sendMessage(message);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleTrapSeeking(game, gamePlayer), 0, 20L);
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
      final boolean survivor = gadget instanceof SurvivorApparatus;
      final MetadataManager metadata = innocent.getMetadataManager();
      if (survivor) {
        set.add(item);
        metadata.setEntityGlowing(item, ChatColor.YELLOW, true);
      } else if (set.contains(entity)) {
        set.remove(entity);
        metadata.setEntityGlowing(item, ChatColor.YELLOW, false);
      }
    }
  }
}
