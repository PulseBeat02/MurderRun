package io.github.pulsebeat02.murderrun.game.gadget.util;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.reflect.PacketToolAPI;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import java.awt.Color;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class TrollGadget extends SurvivorTrap {

  private static final MethodHandle CRASH_HANDLE;

  static {
    try {
      final PacketToolAPI api = PacketToolsProvider.PACKET_API;
      final Class<?> clazz = api.getClass();
      final MethodHandles.Lookup lookup = MethodHandles.lookup();
      final MethodType type = MethodType.methodType(Void.TYPE, Player.class);
      CRASH_HANDLE = lookup.findVirtual(clazz, "crashPlayerClient", type);
    } catch (final NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  public TrollGadget() {
    super(
        "troll",
        Material.STRUCTURE_VOID,
        text("Troll Gadget", RED),
        text("Crashes the client"),
        null,
        128,
        Color.RED);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer activee, final Item item) {
    final Player player = activee.getInternalPlayer();
    try {
      CRASH_HANDLE.invoke(player);
    } catch (final Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
