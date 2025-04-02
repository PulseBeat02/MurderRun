/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.map.GameMap;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.StreamUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class PartWarp extends KillerGadget {

  public PartWarp() {
    super(
      "part_warp",
      GameProperties.PART_WARP_COST,
      ItemFactory.createGadget(
        "part_warp",
        GameProperties.PART_WARP_MATERIAL,
        Message.PART_WARP_NAME.build(),
        Message.PART_WARP_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GameMap map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final Map<String, CarPart> parts = manager.getParts();
    final Collection<CarPart> values = parts.values();
    final List<CarPart> shuffled = values.stream().collect(StreamUtils.toShuffledList());
    final CarPart part = this.getRandomCarPart(shuffled);
    final Item carPartItem = part.getItem();

    final Location location = player.getLocation();
    carPartItem.teleport(location);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.PART_WARP_SOUND);

    return false;
  }

  public CarPart getRandomCarPart(final List<CarPart> shuffled) {
    CarPart chosen = shuffled.getFirst();
    while (chosen.isPickedUp()) {
      shuffled.remove(chosen);
      chosen = shuffled.getFirst();
    }
    return chosen;
  }
}
