package io.github.pulsebeat02.murderrun.resourcepack.model;

import java.util.HashSet;
import java.util.Set;

public final class Models {

  private static final Set<ItemModel> ALL = new HashSet<>();

  public static final ItemModel CAR_PART_1 = of("car_part_1", 1);
  public static final ItemModel CAR_PART_2 = of("car_part_2", 2);
  public static final ItemModel CAR_PART_3 = of("car_part_3", 3);
  public static final ItemModel CAR_PART_4 = of("car_part_4", 4);
  public static final ItemModel CAR_PART_5 = of("car_part_5", 5);
  public static final ItemModel SWORD = of("sword", 1);

  private static ItemModel of(final String name, final int id) {
    final ItemTexture texture = new ItemTexture(name);
    final ItemModel model = new ItemModel(texture, id);
    ALL.add(model);
    return model;
  }

  public static Set<ItemModel> getAllModels() {
    return ALL;
  }
}
