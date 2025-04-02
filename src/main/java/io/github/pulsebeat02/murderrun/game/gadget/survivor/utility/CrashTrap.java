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
package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.reflect.PacketToolAPI;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.awt.Color;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class CrashTrap extends SurvivorTrap {

  private static MethodHandle CRASH_HANDLE;

  static {
    try {
      final PacketToolAPI api = PacketToolsProvider.PACKET_API;
      final Class<?> clazz = api.getClass();
      final MethodHandles.Lookup lookup = MethodHandles.lookup();
      final MethodType type = MethodType.methodType(Void.TYPE, Player.class);
      CRASH_HANDLE = lookup.findVirtual(clazz, "crashPlayerClient", type);
    } catch (final NoSuchMethodException | IllegalAccessException ignored) {}
  }

  public CrashTrap() {
    super(
      "crash",
      Integer.MAX_VALUE,
      ItemFactory.createGadget(
        "crash",
        Material.STRUCTURE_VOID,
        text("Crash Trap", RED),
        text("Crashes the client (wtf, use at your own risk)", RED)
      ),
      empty(),
      Color.RED
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer activee, final Item item) {
    //    final Player player = activee.getInternalPlayer();
    //    try {
    //      CRASH_HANDLE.invoke(player);
    //    } catch (final Throwable e) {
    //      throw new AssertionError(e);
    //    }
  }
}
