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
