package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
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
  public void onGadgetRightClick(final Game game, final PlayerInteractEvent event, final boolean remove) {
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

    final byte[] data = requireNonNull(
      PDCUtils.getPersistentDataAttribute(stack, Keys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY)
    );
    final Location location = MapUtils.byteArrayToLocation(data);
    player.teleport(location);

    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final PlayerAudience audience = gamePlayer.getAudience();
    audience.playSound(GameProperties.TRANSLOCATOR_SOUND);
  }

  @Override
  public boolean onGadgetDrop(
    final Game game,
    final GamePlayer player,
    final org.bukkit.entity.Item item,
    final boolean remove
  ) {
    final Location location = player.getLocation();
    final ItemStack stack = item.getItemStack();
    final byte[] bytes = MapUtils.locationToByteArray(location);
    Item.builder(stack)
      .lore(Message.TRANSLOCATOR_LORE1.build())
      .pdc(Keys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY, bytes)
      .type(Material.LEVER);

    return super.onGadgetDrop(game, player, item, false);
  }
}
