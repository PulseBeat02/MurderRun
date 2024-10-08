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
