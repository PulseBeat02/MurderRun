package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetRightClickPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

public final class Translocator extends SurvivorGadget {

  public Translocator() {
    super(
      "translocator",
      Material.POPPED_CHORUS_FRUIT,
      Message.TRANSLOCATOR_NAME.build(),
      Message.TRANSLOCATOR_LORE.build(),
      GameProperties.TRANSLOCATOR_COST,
      ItemFactory::createTranslocator
    );
  }

  @Override
  public boolean onGadgetRightClick(final GadgetRightClickPacket packet) {
    final GamePlayer player = packet.getPlayer();
    final ItemStack stack = packet.getItemStack();
    if (stack == null) {
      return true;
    }

    final Material material = stack.getType();
    if (material != Material.LEVER) {
      return true;
    }

    final byte[] data = requireNonNull(PDCUtils.getPersistentDataAttribute(stack, Keys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY));
    final Location location = MapUtils.byteArrayToLocation(data);
    player.teleport(location);

    final PlayerInventory inventory = player.getInventory();
    inventory.removeItem(stack);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.TRANSLOCATOR_SOUND);

    return false;
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final GamePlayer player = packet.getPlayer();
    final org.bukkit.entity.Item item = packet.getItem();

    final Location location = player.getLocation();
    final ItemStack stack = item.getItemStack();
    final byte[] bytes = MapUtils.locationToByteArray(location);
    Item.builder(stack)
      .lore(Message.TRANSLOCATOR_LORE1.build())
      .pdc(Keys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY, bytes)
      .type(Material.LEVER);

    return true;
  }
}
