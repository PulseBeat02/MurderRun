package io.github.pulsebeat02.murderrun.locale;

import static io.github.pulsebeat02.murderrun.locale.LocaleParent.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

import java.util.List;

public interface Locale extends LocaleParent {
  NullComponent<Sender> CAR_PART_ITEM_NAME = colored("murder_run.item.car_part.name", GOLD);
  NullComponent<Sender> CAR_PART_ITEM_LORE = colored("murder_run.item.car_part.lore", GRAY);
  UniComponent<Sender, Integer> CAR_PART_ITEM_RETRIEVAL =
      colored("murder_run.game.item.car_part.retrieval", GOLD, AQUA);

  NullComponent<Sender> PREPARATION_PHASE = colored("murder_run.game.survivor_preparation", GOLD);
  NullComponent<Sender> RELEASE_PHASE = colored("murder_run.game.murderer_released", RED);
  NullComponent<Sender> INNOCENT_VICTORY_INNOCENT =
      colored("murder_run.game.innocent_victory_innocent", GREEN);
  NullComponent<Sender> INNOCENT_VICTORY_MURDERER =
      colored("murder_run.game.innocent_victory_murderer", RED);
  NullComponent<Sender> MURDERER_VICTORY_INNOCENT =
      colored("murder_run.game.murderer_victory_innocent", RED);
  NullComponent<Sender> MURDERER_VICTORY_MURDERER =
      colored("murder_run.game.murderer_victory_murderer", GREEN);

  UniComponent<Sender, Long> FINAL_TIME = info("murder_run.game.time", null);
  BiComponent<Sender, Integer, Integer> BOSS_BAR =
      colored("murder_run.game.boss_bar", GOLD, AQUA, AQUA);

  UniComponent<Sender, String> PLAYER_DEATH = colored("murder_run.game.death", RED, AQUA);
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

  NullComponent<Sender> GAME_LEFT = info("murder_run.command.game.leave");
  NullComponent<Sender> GAME_CREATED = info("murder_run.command.game.create");
  UniComponent<Sender, String> GAME_OWNER_INVITE =
      info("murder_run.command.game.owner_invite", null);
  UniComponent<Sender, String> GAME_PLAYER_INVITE =
      info("murder_run.command.game.player_invite", null);
  NullComponent<Sender> GAME_CANCEL = info("murder_run.command.game.cancel");
  UniComponent<Sender, String> GAME_SET_MURDERER =
      info("murder_run.command.game.set.murderer", null);
  UniComponent<Sender, String> GAME_SET_INNOCENT =
      info("murder_run.command.game.set.innocent", null);
  UniComponent<Sender, Integer> GAME_SET_CAR_PART_COUNT =
      info("murder_run.command.game.set.car_part_count", null);
  UniComponent<Sender, String> GAME_OWNER_KICK = info("murder_run.command.game.owner_kick", null);
  NullComponent<Sender> GAME_PLAYER_KICK = info("murder_run.command.game.player_kick");
  UniComponent<Sender, List<String>> GAME_LIST = info("murder_run.command.game.list", null);
  UniComponent<Sender, String> GAME_JOIN = info("murder_run.command.game.join", null);

  NullComponent<Sender> GAME_ARENA_ERROR = error("murder_run.command.game.arena_error");
  NullComponent<Sender> GAME_LOBBY_ERROR = error("murder_run.command.game.lobby_error");
  NullComponent<Sender> GAME_LEAVE_ERROR = error("murder_run.command.game.leave_error");
  NullComponent<Sender> GAME_CREATE_ERROR = error("murder_run.command.game.create_error");
  NullComponent<Sender> GAME_NOT_OWNER_ERROR = error("murder_run.command.game.owner_error");
  NullComponent<Sender> GAME_INVALID_ERROR = error("murder_run.command.game.no_game_error");
  NullComponent<Sender> GAME_JOIN_ERROR = error("murder_run.command.game.join_error");
  NullComponent<Sender> GAME_INVALID_INVITE_ERROR =
      error("murder_run.command.game.invalid_invite_error");

  NullComponent<Sender> VILLAGER_SPAWN = info("murder_run.command.villager");

  NullComponent<Sender> GLOW_TRAP_NAME = colored("murder_run.game.trap.glow.name", GOLD);
  NullComponent<Sender> GLOW_TRAP_LORE = colored("murder_run.game.trap.glow.lore", GRAY);
  NullComponent<Sender> GLOW_TRAP_ACTIVATE = colored("murder_run.game.trap.glow.activate", GOLD);
}
