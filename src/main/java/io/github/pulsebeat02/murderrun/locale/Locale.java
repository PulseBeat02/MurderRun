package io.github.pulsebeat02.murderrun.locale;

import static io.github.pulsebeat02.murderrun.locale.LocaleParent.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

import java.util.List;

public interface Locale extends LocaleParent {
  NullComponent<Sender> CAR_PART_ITEM_NAME = direct("murder_run.item.car_part.name");
  NullComponent<Sender> CAR_PART_ITEM_LORE = direct("murder_run.item.car_part.lore");
  UniComponent<Sender, Integer> CAR_PART_ITEM_RETRIEVAL =
      direct("murder_run.game.item.car_part.retrieval", null);

  NullComponent<Sender> PREPARATION_PHASE = direct("murder_run.game.survivor_preparation");
  NullComponent<Sender> RELEASE_PHASE = direct("murder_run.game.murderer_released");
  NullComponent<Sender> INNOCENT_VICTORY_INNOCENT =
      direct("murder_run.game.innocent_victory_innocent");
  NullComponent<Sender> INNOCENT_VICTORY_MURDERER =
      direct("murder_run.game.innocent_victory_murderer");
  NullComponent<Sender> MURDERER_VICTORY_INNOCENT =
      direct("murder_run.game.murderer_victory_innocent");
  NullComponent<Sender> MURDERER_VICTORY_MURDERER =
      direct("murder_run.game.murderer_victory_murderer");

  UniComponent<Sender, Long> FINAL_TIME = direct("murder_run.game.time", null);
  BiComponent<Sender, Integer, Integer> BOSS_BAR = direct("murder_run.game.boss_bar", null, null);

  UniComponent<Sender, String> PLAYER_DEATH = direct("murder_run.game.death", null);
  NullComponent<Sender> RESOURCEPACK_PROMPT = direct("murder_run.resourcepack");

  NullComponent<Sender> NOT_PLAYER = direct("murder_run.command.console");
  TriComponent<Sender, Integer, Integer, Integer> ARENA_FIRST_CORNER =
      direct("murder_run.command.arena.set.first-corner", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_SECOND_CORNER =
      direct("murder_run.command.arena.set.second-corner", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_SPAWN =
      direct("murder_run.command.arena.set.spawn", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_TRUCK =
      direct("murder_run.command.arena.set.truck", null, null, null);
  UniComponent<Sender, String> ARENA_NAME = direct("murder_run.command.arena.set.name", null);
  NullComponent<Sender> ARENA_CORNER_ERROR = direct("murder_run.command.arena.set.corner_error");
  NullComponent<Sender> ARENA_SPAWN_ERROR = direct("murder_run.command.arena.set.spawn_error");
  NullComponent<Sender> ARENA_NAME_ERROR = direct("murder_run.command.arena.set.name_error");
  NullComponent<Sender> ARENA_TRUCK_ERROR = direct("murder_run.command.arena.set.truck_error");
  NullComponent<Sender> ARENA_BUILT = direct("murder_run.command.arena.create");
  UniComponent<Sender, List<String>> ARENA_LIST = direct("murder_run.command.arena.list", null);

  UniComponent<Sender, String> LOBBY_NAME = direct("murder_run.command.lobby.set.name", null);
  TriComponent<Sender, Integer, Integer, Integer> LOBBY_SPAWN =
      direct("murder_run.command.lobby.set.spawn", null, null, null);
  NullComponent<Sender> LOBBY_NAME_ERROR = direct("murder_run.command.lobby.set.name_error");
  NullComponent<Sender> LOBBY_SPAWN_ERROR = direct("murder_run.command.lobby.set.spawn_error");
  NullComponent<Sender> LOBBY_BUILT = direct("murder_run.command.lobby.create");
  UniComponent<Sender, List<String>> LOBBY_LIST = direct("murder_run.command.lobby.list", null);

  NullComponent<Sender> GAME_LEFT = direct("murder_run.command.game.leave");
  NullComponent<Sender> GAME_CREATED = direct("murder_run.command.game.create");
  UniComponent<Sender, String> GAME_OWNER_INVITE =
      direct("murder_run.command.game.owner_invite", null);
  UniComponent<Sender, String> GAME_PLAYER_INVITE =
      direct("murder_run.command.game.player_invite", null);
  NullComponent<Sender> GAME_CANCEL = direct("murder_run.command.game.cancel");
  UniComponent<Sender, String> GAME_SET_MURDERER =
      direct("murder_run.command.game.set.murderer", null);
  UniComponent<Sender, String> GAME_SET_INNOCENT =
      direct("murder_run.command.game.set.innocent", null);
  UniComponent<Sender, Integer> GAME_SET_CAR_PART_COUNT =
      direct("murder_run.command.game.set.car_part_count", null);
  UniComponent<Sender, String> GAME_OWNER_KICK = direct("murder_run.command.game.owner_kick", null);
  NullComponent<Sender> GAME_PLAYER_KICK = direct("murder_run.command.game.player_kick");
  UniComponent<Sender, List<String>> GAME_LIST = direct("murder_run.command.game.list", null);
  UniComponent<Sender, String> GAME_JOIN = direct("murder_run.command.game.join", null);

  NullComponent<Sender> GAME_ARENA_ERROR = direct("murder_run.command.game.arena_error");
  NullComponent<Sender> GAME_LOBBY_ERROR = direct("murder_run.command.game.lobby_error");
  NullComponent<Sender> GAME_LEAVE_ERROR = direct("murder_run.command.game.leave_error");
  NullComponent<Sender> GAME_CREATE_ERROR = direct("murder_run.command.game.create_error");
  NullComponent<Sender> GAME_NOT_OWNER_ERROR = direct("murder_run.command.game.owner_error");
  NullComponent<Sender> GAME_INVALID_ERROR = direct("murder_run.command.game.no_game_error");
  NullComponent<Sender> GAME_JOIN_ERROR = direct("murder_run.command.game.join_error");
  NullComponent<Sender> GAME_INVALID_INVITE_ERROR =
      direct("murder_run.command.game.invalid_invite_error");

  NullComponent<Sender> VILLAGER_SPAWN = direct("murder_run.command.villager");

  NullComponent<Sender> GLOW_TRAP_NAME = direct("murder_run.game.trap.glow.name");
  NullComponent<Sender> GLOW_TRAP_LORE = direct("murder_run.game.trap.glow.lore");
  NullComponent<Sender> GLOW_TRAP_ACTIVATE = direct("murder_run.game.trap.glow.activate");
}
