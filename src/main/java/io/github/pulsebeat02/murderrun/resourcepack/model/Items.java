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
package io.github.pulsebeat02.murderrun.resourcepack.model;

import java.util.HashSet;
import java.util.Set;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.model.Model;

public final class Items {

  private static final Set<ItemResource> ALL = new HashSet<>();

  public static final ItemResource CAR_PARTS = of(
    "item/diamond",
    Models.CAR_PART_1,
    Models.CAR_PART_2,
    Models.CAR_PART_3,
    Models.CAR_PART_4,
    Models.CAR_PART_5
  );

  public static final ItemResource MINEBUCKS = of("item/nether_star", Models.MINEBUCKS);

  public static final ItemResource SWORD = of("item/diamond_sword", Model.ITEM_HANDHELD, Models.SWORD);

  public static final ItemResource PROJECTILE = of("item/snowball", Models.FLASHBANG, Models.SMOKE_BOMB);

  private static ItemResource of(final String key, final Key parent, final ItemModel... models) {
    final ItemResource model = new ItemResource(key, parent, models);
    ALL.add(model);
    return model;
  }

  private static ItemResource of(final String key, final ItemModel... models) {
    final ItemResource model = new ItemResource(key, models);
    ALL.add(model);
    return model;
  }

  public static Set<ItemResource> getAllVanillaItemModels() {
    return ALL;
  }
}
