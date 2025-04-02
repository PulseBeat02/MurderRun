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
package me.brandonli.murderrun.game.lobby;

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
    LOOKUP_TABLE = Stream.of(trades).collect(Collectors.toMap(Enum::name, UnaryOperator.identity()));
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
