package io.github.pulsebeat02.murderrun.locale;

import static io.github.pulsebeat02.murderrun.locale.LocaleParent.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface Locale extends LocaleParent {
  NullComponent<Sender> CAR_PART_ITEM_NAME = itemName("murder_run.item.car_part.name");
  NullComponent<Sender> CAR_PART_ITEM_LORE = itemLore("murder_run.item.car_part.lore");
  NullComponent<Sender> PREPARATION_PHASE = title("murder_run.game.survivor_preparation", GOLD);
  NullComponent<Sender> RELEASE_PHASE = title("murder_run.game.murderer_released", RED);
  NullComponent<Sender> INNOCENT_VICTORY = title("murder_run.game.innocent_victory", GREEN);
  NullComponent<Sender> MURDERER_VICTORY = title("murder_run.game.murderer_victory", RED);
  NullComponent<Sender> NOT_PLAYER = error("murder_run.command.console");
  UniComponent<Sender, Long> FINAL_TIMME = info("murder_run.game.time", null);
  UniComponent<Sender, Integer> CAR_PART_ITEM_RETRIEVAL =
      title("murder_run.game.item.car_part.retrieval", null, AQUA);
  UniComponent<Sender, String> PLAYER_DEATH = title("murder_run.game.death", null, AQUA);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_FIRST_CORNER =
      info("murder_run.command.arena.set.first-corner", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_SECOND_CORNER =
      info("murder_run.command.arena.set.second-corner", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_SPAWN =
      info("murder_run.command.arena.set.spawn", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_TRUCK =
      info("murder_run.command.arena.set.truck", null, null, null);
  UniComponent<Sender, String> ARENA_NAME = info("murder_run.command.arena.set.name", null);
  NullComponent<Sender> ARENA_CORNER_ERROR = error("murder_run.command.arena.set.error_corner");
  NullComponent<Sender> ARENA_SPAWN_ERROR = error("murder_run.command.arena.set.error_spawn");
  NullComponent<Sender> ARENA_NAME_ERROR = error("murder_run.command.arena.set.error_name");
  NullComponent<Sender> ARENA_TRUCK_ERROR = error("murder_run.command.arena.set.error_truck");
  NullComponent<Sender> ARENA_BUILT_ARENA = error("murder_run.command.arena.create");
  NullComponent<Sender> RESOURCEPACK_PROMPT = info("murder_run.resourcepack");
}
