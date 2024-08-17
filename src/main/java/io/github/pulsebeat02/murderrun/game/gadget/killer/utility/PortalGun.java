package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.incendo.cloud.type.tuple.Pair;

public final class PortalGun extends KillerGadget implements Listener {

  private final Map<String, Pair<Location, Location>> portals;

  public PortalGun() {
    super(
        "portal_gun",
        Material.BOW,
        Message.PORTAL_GUN_NAME.build(),
        Message.PORTAL_LORE.build(),
        64,
        stack -> {
          final UUID uuid = UUID.randomUUID();
          final String data = uuid.toString();
          ItemUtils.setPersistentDataAttribute(
              stack, Keys.PORTAL_GUN, PersistentDataType.BOOLEAN, true);
          ItemUtils.setPersistentDataAttribute(stack, Keys.UUID, PersistentDataType.STRING, data);
        });
    this.portals = new HashMap<>();
  }

  @EventHandler
  public void onProjectileHit(final ProjectileHitEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Arrow arrow)) {
      return;
    }

    final ProjectileSource shooter = arrow.getShooter();
    if (!(shooter instanceof final Player player)) {
      return;
    }

    final ItemStack stack = player.getItemInUse();
    if (stack == null) {
      return;
    }

    if (!ItemUtils.isPortalGun(stack)) {
      return;
    }

    final Boolean status =
        ItemUtils.getPersistentDataAttribute(stack, Keys.PORTAL_GUN, PersistentDataType.BOOLEAN);
    final String uuid =
        ItemUtils.getPersistentDataAttribute(stack, Keys.UUID, PersistentDataType.STRING);
    if (status == null || uuid == null) {
      return;
    }

    // true -> spawn sending portal
    // false -> spawn receiving portal
    if (!this.portals.containsKey(uuid)) {
      final Pair<Location, Location> pair = Pair.of(null, null);
      this.portals.put(uuid, pair);
    }

    // TODO working on
    final Pair<Location, Location> pair = this.portals.get(uuid);
    final Location location = arrow.getLocation();
    if (status) {
      final Location receiving = pair.second();
      final Pair<Location, Location> value = Pair.of(location, receiving);
      this.portals.put(uuid, value);
    } else {
      final Location sending = pair.first();
      final Pair<Location, Location> value = Pair.of(sending, location);
      this.portals.put(uuid, value);
    }
  }
}
