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

public final class Models {

  private static final Set<ItemModel> ALL = new HashSet<>();

  public static final ItemModel CAR_PART_1 = of("car_part_1", 1);
  public static final ItemModel CAR_PART_2 = of("car_part_2", 2);
  public static final ItemModel CAR_PART_3 = of("car_part_3", 3);
  public static final ItemModel CAR_PART_4 = of("car_part_4", 4);
  public static final ItemModel CAR_PART_5 = of("car_part_5", 5);
  public static final ItemModel MINEBUCKS = of("minebucks", 1);
  public static final ItemModel SWORD = of("sword", Model.ITEM_HANDHELD, 1);
  public static final ItemModel FLASHBANG = of("flashbang", 1);
  public static final ItemModel SMOKE_BOMB = of("smoke_bomb", 2);

  private static ItemModel of(final String name, final int id) {
    final ItemTexture texture = new ItemTexture(name);
    final ItemModel model = new ItemModel(texture, id);
    ALL.add(model);
    return model;
  }

  private static ItemModel of(final String name, final Key key, final int id) {
    final ItemTexture texture = new ItemTexture(name);
    final ItemModel model = new ItemModel(texture, key, id);
    ALL.add(model);
    return model;
  }

  public static Set<ItemModel> getAllModels() {
    return ALL;
  }
}
