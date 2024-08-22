package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ItemBuilder;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class Translocator extends SurvivorGadget {

  public Translocator() {
    super(
        "translocator",
        Material.END_ROD,
        Message.TRANSLOCATOR_NAME.build(),
        Message.TRANSLOCATOR_LORE.build(),
        64,
        stack -> PDCUtils.setPersistentDataAttribute(
            stack, Keys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY, new byte[0]));
  }

  @Override
  public void onGadgetRightClick(
      final Game game, final PlayerInteractEvent event, final boolean remove) {

    super.onGadgetRightClick(game, event, true);

    final Player player = event.getPlayer();
    final ItemStack stack = event.getItem();
    if (stack == null) {
      return;
    }

    final Material material = stack.getType();
    if (material != Material.LEVER) {
      return;
    }

    final byte[] data = requireNonNull(PDCUtils.getPersistentDataAttribute(
        stack, Keys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY));
    final Location location = this.byteArrayToLocation(data);
    player.teleport(location);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, false);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    final byte[] bytes = this.locationToByteArray(location);
    PDCUtils.setPersistentDataAttribute(
        stack, Keys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY, bytes);
    ItemBuilder.builder(stack).lore(Message.TRANSLOCATOR_LORE1.build()).type(Material.LEVER);
  }

  private Location byteArrayToLocation(final byte[] array) {

    final ByteBuffer buffer = ByteBuffer.wrap(array);
    final int worldNameLength = buffer.getInt();
    final byte[] worldBytes = new byte[worldNameLength];
    buffer.get(worldBytes);

    final String worldName = new String(worldBytes, StandardCharsets.UTF_8);
    final World world = Bukkit.getWorld(worldName);
    final double x = buffer.getDouble();
    final double y = buffer.getDouble();
    final double z = buffer.getDouble();
    final float pitch = (float) buffer.getDouble();
    final float yaw = (float) buffer.getDouble();

    return new Location(world, x, y, z, yaw, pitch);
  }

  private byte[] locationToByteArray(final Location location) {

    final World world = requireNonNull(location.getWorld());
    final String name = world.getName();
    final byte[] worldBytes = name.getBytes(StandardCharsets.UTF_8);
    final ByteBuffer buffer =
        ByteBuffer.allocate(Double.BYTES * 5 + Integer.BYTES + worldBytes.length);
    buffer.putInt(worldBytes.length);
    buffer.put(worldBytes);
    buffer.putDouble(location.getX());
    buffer.putDouble(location.getY());
    buffer.putDouble(location.getZ());
    buffer.putDouble(location.getPitch());
    buffer.putDouble(location.getYaw());

    return buffer.array();
  }
}
