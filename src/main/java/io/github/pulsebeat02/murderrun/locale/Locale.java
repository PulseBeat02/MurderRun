package io.github.pulsebeat02.murderrun.locale;

import java.util.List;

import static io.github.pulsebeat02.murderrun.locale.LocaleParent.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface Locale extends LocaleParent {
  NullComponent<Sender> CAR_PART_ITEM_NAME = itemName("murder_run.item.car_part.name");
  NullComponent<Sender> CAR_PART_ITEM_LORE = itemLore("murder_run.item.car_part.lore");
  UniComponent<Sender, Integer> CAR_PART_ITEM_RETRIEVAL =
      title("murder_run.game.item.car_part.retrieval", null, AQUA);

  NullComponent<Sender> PREPARATION_PHASE = title("murder_run.game.survivor_preparation", GOLD);
  NullComponent<Sender> RELEASE_PHASE = title("murder_run.game.murderer_released", RED);
  NullComponent<Sender> INNOCENT_VICTORY = title("murder_run.game.innocent_victory", GREEN);
  NullComponent<Sender> MURDERER_VICTORY = title("murder_run.game.murderer_victory", RED);
  UniComponent<Sender, Long> FINAL_TIME = info("murder_run.game.time", null);

  UniComponent<Sender, String> PLAYER_DEATH = title("murder_run.game.death", null, AQUA);
  NullComponent<Sender> RESOURCEPACK_PROMPT = info("murder_run.resourcepack");

  NullComponent<Sender> NOT_PLAYER = error("murder_run.command.console");
  TriComponent<Sender, Integer, Integer, Integer> ARENA_FIRST_CORNER =
      info("murder_run.command.arena.set.first-corner", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_SECOND_CORNER =
      info("murder_run.command.arena.set.second-corner", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_SPAWN =
      info("murder_run.command.arena.set.spawn", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_TRUCK =
      info("murder_run.command.arena.set.truck", null, null, null);
  UniComponent<Sender, String> ARENA_NAME = info("murder_run.command.arena.set.name", null);
  NullComponent<Sender> ARENA_CORNER_ERROR = error("murder_run.command.arena.set.corner_error");
  NullComponent<Sender> ARENA_SPAWN_ERROR = error("murder_run.command.arena.set.spawn_error");
  NullComponent<Sender> ARENA_NAME_ERROR = error("murder_run.command.arena.set.name_error");
  NullComponent<Sender> ARENA_TRUCK_ERROR = error("murder_run.command.arena.set.truck_error");
  NullComponent<Sender> ARENA_BUILT = info("murder_run.command.arena.create");
  UniComponent<Sender, List<String>> ARENA_LIST = info("murder_run.command.arena.list", null);

  UniComponent<Sender, String> LOBBY_NAME = info("murder_run.command.lobby.set.name", null);
  TriComponent<Sender, Integer, Integer, Integer> LOBBY_SPAWN =
      info("murder_run.command.lobby.set.spawn", null, null, null);
  NullComponent<Sender> LOBBY_NAME_ERROR = info("murder_run.command.lobby.set.name_error");
  NullComponent<Sender> LOBBY_SPAWN_ERROR = info("murder_run.command.lobby.set.spawn_error");
  NullComponent<Sender> LOBBY_BUILT = info("murder_run.command.lobby.create");
  UniComponent<Sender, List<String>> LOBBY_LIST = info("murder_run.command.lobby.list", null);
}
