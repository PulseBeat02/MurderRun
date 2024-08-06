package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.ItemStackUtils;
import io.github.pulsebeat02.murderrun.utils.NamespacedKeys;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class Translocator extends MurderGadget {

  public Translocator(final MurderRun plugin) {
    super(
        "translocator",
        Material.CHORUS_FLOWER,
        Locale.TRANSLOCATOR_TRAP_NAME.build(),
        Locale.TRANSLOCATOR_TRAP_LORE.build(),
        stack -> ItemStackUtils.setData(
            stack, NamespacedKeys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY, null));
  }

  @Override
  public void onGadgetRightClick(
      final MurderGame game, final PlayerInteractEvent event, final boolean remove) {

    super.onGadgetRightClick(game, event, true);

    final Player player = event.getPlayer();
    final ItemStack stack = event.getItem();

    final byte[] data =
        ItemStackUtils.getData(stack, NamespacedKeys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY);
    if (data == null) {
      return;
    }

    final Location location = this.byteArrayToLocation(data);
    player.teleport(location);
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, false);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    final byte[] bytes = this.locationToByteArray(location);
    ItemStackUtils.setData(
        stack, NamespacedKeys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY, bytes);

    final ItemMeta meta = stack.getItemMeta();
    if (meta == null) {
      throw new AssertionError("Couldn't construct Translocator!");
    }

    final Component lore = Locale.TRANSLOCATOR_TRAP_LORE1.build();
    final String message = AdventureUtils.serializeComponentToLegacy(lore);
    final List<String> newLore = List.of(message);
    meta.setLore(newLore);
    stack.setType(Material.LEVER);
  }

  public Location byteArrayToLocation(final byte[] array) {

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

  public byte[] locationToByteArray(final Location location) {

    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

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
