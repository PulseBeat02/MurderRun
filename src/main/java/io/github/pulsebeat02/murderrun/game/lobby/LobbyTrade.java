package io.github.pulsebeat02.murderrun.game.lobby;

import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public enum LobbyTrade {
  ;

  private static final Map<String, LobbyTrade> LOOKUP_TABLE;

  static {
    final LobbyTrade[] trades = LobbyTrade.values();
    LOOKUP_TABLE =
        Stream.of(trades).collect(Collectors.toMap(Enum::name, UnaryOperator.identity()));
  }

  private final ItemStack cost;
  private final ItemStack stack;

  LobbyTrade(final ItemStack cost, final ItemStack stack) {
    this.cost = cost;
    this.stack = stack;
  }

  public static @Nullable LobbyTrade get(final String name) {
    return LOOKUP_TABLE.get(name.toLowerCase());
  }

  public ItemStack getCost() {
    return this.cost;
  }

  public ItemStack getStack() {
    return this.stack;
  }
}
