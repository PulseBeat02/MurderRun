package io.github.pulsebeat02.murderrun.locale;

import static io.github.pulsebeat02.murderrun.locale.LocaleTools.direct;

import java.util.List;

public interface Locale extends LocaleTools {

  NullComponent<Sender> PLUGIN_ENABLE = direct("murder_run.plugin.enable");
  NullComponent<Sender> PLUGIN_DEPENDENCY_ERROR = direct("murder_run.plugin.dependency");
  NullComponent<Sender> PLUGIN_DISABLE = direct("murder_run.plugin.disable");
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
      direct("murder_run.command.game.set.killer", null);
  UniComponent<Sender, String> GAME_SET_INNOCENT =
      direct("murder_run.command.game.set.survivor", null);
  UniComponent<Sender, Integer> GAME_SET_CAR_PART_COUNT =
      direct("murder_run.command.game.set.car_part_count", null);
  UniComponent<Sender, String> GAME_OWNER_KICK = direct("murder_run.command.game.owner_kick", null);
  NullComponent<Sender> GAME_PLAYER_KICK = direct("murder_run.command.game.player_kick");
  UniComponent<Sender, List<String>> GAME_LIST = direct("murder_run.command.game.list", null);
  UniComponent<Sender, String> GAME_JOIN = direct("murder_run.command.game.join", null);
  NullComponent<Sender> GAME_START = direct("murder_run.command.game.start");
  NullComponent<Sender> GAME_ARENA_ERROR = direct("murder_run.command.game.arena_error");
  NullComponent<Sender> GAME_LOBBY_ERROR = direct("murder_run.command.game.lobby_error");
  NullComponent<Sender> GAME_LEAVE_ERROR = direct("murder_run.command.game.leave_error");
  NullComponent<Sender> GAME_CREATE_ERROR = direct("murder_run.command.game.create_error");
  NullComponent<Sender> GAME_NOT_OWNER_ERROR = direct("murder_run.command.game.owner_error");
  NullComponent<Sender> GAME_INVALID_ERROR = direct("murder_run.command.game.no_game_error");
  NullComponent<Sender> GAME_JOIN_ERROR = direct("murder_run.command.game.join_error");
  NullComponent<Sender> GAME_INVALID_INVITE_ERROR =
      direct("murder_run.command.game.invalid_invite_error");
  NullComponent<Sender> GAME_LOW_PLAYER_COUNT_ERROR =
      direct("murder_run.command.game.low_player_count_error");
  NullComponent<Sender> GAME_INVITE_ERROR = direct("murder_run.command.game.invite_error");
  NullComponent<Sender> VILLAGER_SPAWN = direct("murder_run.command.villager");
  NullComponent<Sender> HACK_TRAP_NAME = direct("murder_run.game.gadget.hack.name");
  NullComponent<Sender> GLOW_TRAP_NAME = direct("murder_run.game.gadget.glow.name");
  NullComponent<Sender> BEAR_TRAP_NAME = direct("murder_run.game.gadget.bear.name");
  NullComponent<Sender> PORTAL_TRAP_NAME = direct("murder_run.game.gadget.portal.name");
  NullComponent<Sender> SPASM_TRAP_NAME = direct("murder_run.game.gadget.spasm.name");
  NullComponent<Sender> REWIND_TRAP_NAME = direct("murder_run.game.gadget.rewind.name");
  NullComponent<Sender> MURDERER_REWIND_TRAP_NAME =
      direct("murder_run.game.gadget.murderer_rewind.name");
  NullComponent<Sender> RESURRECTION_STONE_TRAP_NAME =
      direct("murder_run.game.gadget.resurrection_stone.name");
  NullComponent<Sender> CORPUS_WARP_TRAP_NAME = direct("murder_run.game.gadget.corpus_warp.name");
  NullComponent<Sender> GHOSTING_TRAP_NAME = direct("murder_run.game.gadget.ghosting.name");
  NullComponent<Sender> EXCAVATOR_TRAP_NAME = direct("murder_run.game.gadget.excavator.name");
  NullComponent<Sender> DISTORT_TRAP_NAME = direct("murder_run.game.gadget.distort.name");
  NullComponent<Sender> HORCRUX_TRAP_NAME = direct("murder_run.game.gadget.horcrux.name");
  NullComponent<Sender> MED_BOT_TRAP_NAME = direct("murder_run.game.gadget.med_bot.name");
  NullComponent<Sender> RETALIATION_TRAP_NAME = direct("murder_run.game.gadget.retaliation.name");
  NullComponent<Sender> SUPPLY_DROP_TRAP_NAME = direct("murder_run.game.gadget.supply_drop.name");
  NullComponent<Sender> MED_KIT_TRAP_NAME = direct("murder_run.game.gadget.med_kit.name");
  NullComponent<Sender> JUMP_SCARE_TRAP_NAME = direct("murder_run.game.gadget.jump_scare.name");
  NullComponent<Sender> SMOKE_TRAP_NAME = direct("murder_run.game.gadget.smoke.name");
  NullComponent<Sender> LEVITATION_TRAP_NAME = direct("murder_run.game.gadget.levitation.name");
  NullComponent<Sender> CAGE_TRAP_NAME = direct("murder_run.game.gadget.cage.name");
  NullComponent<Sender> BLIND_TRAP_NAME = direct("murder_run.game.gadget.blind.name");
  NullComponent<Sender> HAUNT_TRAP_NAME = direct("murder_run.game.gadget.haunt.name");
  NullComponent<Sender> NECK_SNAP_TRAP_NAME = direct("murder_run.game.gadget.neck_snap.name");
  NullComponent<Sender> DEADRINGER_TRAP_NAME = direct("murder_run.game.gadget.deadringer.name");
  NullComponent<Sender> STAR_TRAP_NAME = direct("murder_run.game.gadget.star.name");
  NullComponent<Sender> SPAWN_TRAP_NAME = direct("murder_run.game.gadget.spawn.name");
  NullComponent<Sender> FREEZE_TRAP_NAME = direct("murder_run.game.gadget.freeze.name");
  NullComponent<Sender> BURROW_TRAP_NAME = direct("murder_run.game.gadget.burrow.name");
  NullComponent<Sender> GHOST_TRAP_NAME = direct("murder_run.game.gadget.ghost.name");
  NullComponent<Sender> PONY_TRAP_NAME = direct("murder_run.game.gadget.pony.name");
  NullComponent<Sender> FIREWORK_TRAP_NAME = direct("murder_run.game.gadget.firework.name");
  NullComponent<Sender> FART_TRAP_NAME = direct("murder_run.game.gadget.fart.name");
  NullComponent<Sender> TRAP_VEST_TRAP_NAME = direct("murder_run.game.gadget.trap_vest.name");
  NullComponent<Sender> RANDOM_TRAP_NAME = direct("murder_run.game.gadget.random.name");
  NullComponent<Sender> MAGNET_MODE_TRAP_NAME = direct("murder_run.game.gadget.magnet_mode.name");
  NullComponent<Sender> TRANSLOCATOR_TRAP_NAME = direct("murder_run.game.gadget.translocator.name");
  NullComponent<Sender> TRACKER_TRAP_NAME = direct("murder_run.game.gadget.tracker.name");
  NullComponent<Sender> DECOY_TRAP_NAME = direct("murder_run.game.gadget.decoy.name");
  NullComponent<Sender> SMOKE_BOMB_TRAP_NAME = direct("murder_run.game.gadget.smoke_bomb.name");
  NullComponent<Sender> FRIEND_WARP_TRAP_NAME = direct("murder_run.game.gadget.friend_warp.name");
  NullComponent<Sender> FLASHBANG_TRAP_NAME = direct("murder_run.game.gadget.flashbang.name");
  NullComponent<Sender> CAMERA_TRAP_NAME = direct("murder_run.game.gadget.camera.name");
  NullComponent<Sender> CLOAK_TRAP_NAME = direct("murder_run.game.gadget.cloak.name");
  NullComponent<Sender> SHIELD_TRAP_NAME = direct("murder_run.game.gadget.shield.name");
  NullComponent<Sender> TP_ME_AWAY_FROM_HERE_TRAP_NAME =
      direct("murder_run.game.gadget.tp_me_away_from_here.name");
  NullComponent<Sender> SIXTH_SENSE_TRAP_NAME = direct("murder_run.game.gadget.sixth_sense.name");
  NullComponent<Sender> BLAST_OFF_TRAP_NAME = direct("murder_run.game.gadget.blast_off.name");
  NullComponent<Sender> DRONE_TRAP_NAME = direct("murder_run.game.gadget.drone.name");
  NullComponent<Sender> TRAP_SNIFFER_TRAP_NAME = direct("murder_run.game.gadget.trap_sniffer.name");
  NullComponent<Sender> CHIPPED_TRAP_NAME = direct("murder_run.game.gadget.chipped.name");
  NullComponent<Sender> LIFE_INSURANCE_TRAP_NAME =
      direct("murder_run.game.gadget.life_insurance.name");
  NullComponent<Sender> CRYO_FREEZE_TRAP_NAME = direct("murder_run.game.gadget.cryo_freeze.name");
  NullComponent<Sender> ICE_SKATIN_TRAP_NAME = direct("murder_run.game.gadget.ice_skatin.name");
  NullComponent<Sender> ICE_SPIRIT_TRAP_NAME = direct("murder_run.game.gadget.ice_spirit.name");
  NullComponent<Sender> MIND_CONTROL_TRAP_NAME = direct("murder_run.game.gadget.mind_control.name");
  NullComponent<Sender> JEB_TRAP_NAME = direct("murder_run.game.gadget.jeb.name");
  NullComponent<Sender> BUSH_TRAP_NAME = direct("murder_run.game.gadget.bush.name");
  NullComponent<Sender> KILLER_TRACKER_TRAP_NAME =
      direct("murder_run.game.gadget.killer_tracker.name");
  NullComponent<Sender> FLASHLIGHT_TRAP_NAME = direct("murder_run.game.gadget.flashlight.name");
  NullComponent<Sender> SHOCKWAVE_TRAP_NAME = direct("murder_run.game.gadget.shockwave.name");
  NullComponent<Sender> PARASITE_TRAP_NAME = direct("murder_run.game.gadget.parasite.name");
  NullComponent<Sender> DISTORTER_TRAP_NAME = direct("murder_run.game.gadget.distorter.name");
  NullComponent<Sender> SPEED_PENDANT_NAME = direct("murder_run.game.gadget.speed_pendant.name");
  NullComponent<Sender> GLOW_TRAP_LORE = direct("murder_run.game.gadget.glow.lore");
  NullComponent<Sender> HACK_TRAP_LORE = direct("murder_run.game.gadget.hack.lore");
  NullComponent<Sender> BEAR_TRAP_LORE = direct("murder_run.game.gadget.bear.lore");
  NullComponent<Sender> PORTAL_TRAP_LORE = direct("murder_run.game.gadget.portal.lore");
  NullComponent<Sender> SPASM_TRAP_LORE = direct("murder_run.game.gadget.spasm.lore");
  NullComponent<Sender> REWIND_TRAP_LORE = direct("murder_run.game.gadget.rewind.lore");
  NullComponent<Sender> MURDERER_REWIND_TRAP_LORE =
      direct("murder_run.game.gadget.murderer_rewind.lore");
  NullComponent<Sender> RESURRECTION_STONE_TRAP_LORE =
      direct("murder_run.game.gadget.resurrection_stone.lore");
  NullComponent<Sender> CORPUS_WARP_TRAP_LORE = direct("murder_run.game.gadget.corpus_warp.lore");
  NullComponent<Sender> GHOSTING_TRAP_LORE = direct("murder_run.game.gadget.ghosting.lore");
  NullComponent<Sender> EXCAVATOR_TRAP_LORE = direct("murder_run.game.gadget.excavator.lore");
  NullComponent<Sender> DISTORT_TRAP_LORE = direct("murder_run.game.gadget.distort.lore");
  NullComponent<Sender> HORCRUX_TRAP_LORE = direct("murder_run.game.gadget.horcrux.lore");
  NullComponent<Sender> MED_BOT_TRAP_LORE = direct("murder_run.game.gadget.med_bot.lore");
  NullComponent<Sender> RETALIATION_TRAP_LORE = direct("murder_run.game.gadget.retaliation.lore");
  NullComponent<Sender> SUPPLY_DROP_TRAP_LORE = direct("murder_run.game.gadget.supply_drop.lore");
  NullComponent<Sender> MED_KIT_TRAP_LORE = direct("murder_run.game.gadget.med_kit.lore");
  NullComponent<Sender> JUMP_SCARE_TRAP_LORE = direct("murder_run.game.gadget.jump_scare.lore");
  NullComponent<Sender> SMOKE_TRAP_LORE = direct("murder_run.game.gadget.smoke.lore");
  NullComponent<Sender> LEVITATION_TRAP_LORE = direct("murder_run.game.gadget.levitation.lore");
  NullComponent<Sender> CAGE_TRAP_LORE = direct("murder_run.game.gadget.cage.lore");
  NullComponent<Sender> BLIND_TRAP_LORE = direct("murder_run.game.gadget.blind.lore");
  NullComponent<Sender> HAUNT_TRAP_LORE = direct("murder_run.game.gadget.haunt.lore");
  NullComponent<Sender> NECK_SNAP_TRAP_LORE = direct("murder_run.game.gadget.neck_snap.lore");
  NullComponent<Sender> DEADRINGER_TRAP_LORE = direct("murder_run.game.gadget.deadringer.lore");
  NullComponent<Sender> STAR_TRAP_LORE = direct("murder_run.game.gadget.star.lore");
  NullComponent<Sender> SPAWN_TRAP_LORE = direct("murder_run.game.gadget.spawn.lore");
  NullComponent<Sender> FREEZE_TRAP_LORE = direct("murder_run.game.gadget.freeze.lore");
  NullComponent<Sender> BURROW_TRAP_LORE = direct("murder_run.game.gadget.burrow.lore");
  NullComponent<Sender> GHOST_TRAP_LORE = direct("murder_run.game.gadget.ghost.lore");
  NullComponent<Sender> PONY_TRAP_LORE = direct("murder_run.game.gadget.pony.lore");
  NullComponent<Sender> FIREWORK_TRAP_LORE = direct("murder_run.game.gadget.firework.lore");
  NullComponent<Sender> FART_TRAP_LORE = direct("murder_run.game.gadget.fart.lore");
  NullComponent<Sender> TRAP_VEST_TRAP_LORE = direct("murder_run.game.gadget.trap_vest.lore");
  NullComponent<Sender> RANDOM_TRAP_LORE = direct("murder_run.game.gadget.random.lore");
  NullComponent<Sender> MAGNET_MODE_TRAP_LORE = direct("murder_run.game.gadget.magnet_mode.lore");
  NullComponent<Sender> TRANSLOCATOR_TRAP_LORE = direct("murder_run.game.gadget.translocator.lore");
  NullComponent<Sender> TRANSLOCATOR_TRAP_LORE1 =
      direct("murder_run.game.gadget.translocator.lore1");
  NullComponent<Sender> TRACKER_TRAP_LORE = direct("murder_run.game.gadget.tracker.lore");
  NullComponent<Sender> DECOY_TRAP_LORE = direct("murder_run.game.gadget.decoy.lore");
  NullComponent<Sender> SMOKE_BOMB_TRAP_LORE = direct("murder_run.game.gadget.smoke_bomb.lore");
  NullComponent<Sender> FRIEND_WARP_TRAP_LORE = direct("murder_run.game.gadget.friend_warp.lore");
  NullComponent<Sender> FLASHBANG_TRAP_LORE = direct("murder_run.game.gadget.flashbang.lore");
  NullComponent<Sender> CAMERA_TRAP_LORE = direct("murder_run.game.gadget.camera.lore");
  NullComponent<Sender> CLOAK_TRAP_LORE = direct("murder_run.game.gadget.cloak.lore");
  NullComponent<Sender> SHIELD_TRAP_LORE = direct("murder_run.game.gadget.shield.lore");
  NullComponent<Sender> TP_ME_AWAY_FROM_HERE_TRAP_LORE =
      direct("murder_run.game.gadget.tp_me_away_from_here.lore");
  NullComponent<Sender> SIXTH_SENSE_TRAP_LORE = direct("murder_run.game.gadget.sixth_sense.lore");
  NullComponent<Sender> BLAST_OFF_TRAP_LORE = direct("murder_run.game.gadget.blast_off.lore");
  NullComponent<Sender> DRONE_TRAP_LORE = direct("murder_run.game.gadget.drone.lore");
  NullComponent<Sender> TRAP_SNIFFER_TRAP_LORE = direct("murder_run.game.gadget.trap_sniffer.lore");
  NullComponent<Sender> CHIPPED_TRAP_LORE = direct("murder_run.game.gadget.chipped.lore");
  NullComponent<Sender> LIFE_INSURANCE_TRAP_LORE =
      direct("murder_run.game.gadget.life_insurance.lore");
  NullComponent<Sender> CRYO_FREEZE_TRAP_LORE = direct("murder_run.game.gadget.cryo_freeze.lore");
  NullComponent<Sender> ICE_SKATIN_TRAP_LORE = direct("murder_run.game.gadget.ice_skatin.lore");
  NullComponent<Sender> ICE_SPIRIT_TRAP_LORE = direct("murder_run.game.gadget.ice_spirit.lore");
  NullComponent<Sender> MIND_CONTROL_TRAP_LORE = direct("murder_run.game.gadget.mind_control.lore");
  NullComponent<Sender> JEB_TRAP_LORE = direct("murder_run.game.gadget.jeb.lore");
  NullComponent<Sender> BUSH_TRAP_LORE = direct("murder_run.game.gadget.bush.lore");
  NullComponent<Sender> KILLER_TRACKER_TRAP_LORE =
      direct("murder_run.game.gadget.killer_tracker.lore");
  NullComponent<Sender> FLASHLIGHT_TRAP_LORE = direct("murder_run.game.gadget.flashlight.lore");
  NullComponent<Sender> SHOCKWAVE_TRAP_LORE = direct("murder_run.game.gadget.shockwave.lore");
  NullComponent<Sender> PARASITE_TRAP_LORE = direct("murder_run.game.gadget.parasite.lore");
  NullComponent<Sender> DISTORTER_TRAP_LORE = direct("murder_run.game.gadget.distorter.lore");
  NullComponent<Sender> GLOW_TRAP_ACTIVATE = direct("murder_run.game.gadget.glow.activate");
  NullComponent<Sender> HACK_TRAP_ACTIVATE = direct("murder_run.game.gadget.hack.activate");
  NullComponent<Sender> BEAR_TRAP_ACTIVATE = direct("murder_run.game.gadget.bear.activate");
  NullComponent<Sender> SPASM_TRAP_ACTIVATE = direct("murder_run.game.gadget.spasm.activate");
  NullComponent<Sender> RESURRECTION_STONE_TRAP_ACTIVATE =
      direct("murder_run.game.gadget.resurrection_stone.activate");
  NullComponent<Sender> GHOSTING_TRAP_ACTIVATE = direct("murder_run.game.gadget.ghosting.activate");
  NullComponent<Sender> DISTORT_TRAP_ACTIVATE = direct("murder_run.game.gadget.distort.activate");
  NullComponent<Sender> HORCRUX_TRAP_ACTIVATE = direct("murder_run.game.gadget.horcrux.activate");
  NullComponent<Sender> RETALIATION_TRAP_ACTIVATE =
      direct("murder_run.game.gadget.retaliation.activate");
  NullComponent<Sender> JUMP_SCARE_TRAP_ACTIVATE =
      direct("murder_run.game.gadget.jump_scare.activate");
  NullComponent<Sender> SMOKE_TRAP_ACTIVATE = direct("murder_run.game.gadget.smoke.activate");
  NullComponent<Sender> LEVITATION_TRAP_ACTIVATE =
      direct("murder_run.game.gadget.levitation.activate");
  NullComponent<Sender> CAGE_TRAP_ACTIVATE = direct("murder_run.game.gadget.cage.activate");
  NullComponent<Sender> BLIND_TRAP_ACTIVATE = direct("murder_run.game.gadget.blind.activate");
  NullComponent<Sender> HAUNT_TRAP_ACTIVATE = direct("murder_run.game.gadget.haunt.activate");
  NullComponent<Sender> NECK_SNAP_TRAP_ACTIVATE =
      direct("murder_run.game.gadget.neck_snap.activate");
  NullComponent<Sender> STAR_TRAP_ACTIVATE = direct("murder_run.game.gadget.star.activate");
  NullComponent<Sender> SPAWN_TRAP_ACTIVATE = direct("murder_run.game.gadget.spawn.activate");
  NullComponent<Sender> FREEZE_TRAP_ACTIVATE = direct("murder_run.game.gadget.freeze.activate");
  NullComponent<Sender> BURROW_TRAP_ACTIVATE = direct("murder_run.game.gadget.burrow.activate");
  NullComponent<Sender> GHOST_TRAP_ACTIVATE = direct("murder_run.game.gadget.ghost.activate");
  NullComponent<Sender> FIREWORK_TRAP_ACTIVATE = direct("murder_run.game.gadget.firework.activate");
  NullComponent<Sender> FART_TRAP_ACTIVATE = direct("murder_run.game.gadget.fart.activate");
  NullComponent<Sender> TRAP_VEST_ACTIVATE = direct("murder_run.game.gadget.trap_vest.activate");
  NullComponent<Sender> MAGNET_MODE_TRAP_ACTIVATE =
      direct("murder_run.game.gadget.magnet_mode.activate");
  NullComponent<Sender> CLOAK_TRAP_ACTIVATE = direct("murder_run.game.gadget.cloak.activate");
  NullComponent<Sender> SIXTH_SENSE_TRAP_ACTIVATE =
      direct("murder_run.game.gadget.sixth_sense.activate");
  NullComponent<Sender> BLAST_OFF_TRAP_ACTIVATE =
      direct("murder_run.game.gadget.blast_off.activate");
  NullComponent<Sender> TRAP_SNIFFER_TRAP_ACTIVATE =
      direct("murder_run.game.gadget.trap_sniffer.activate");
  NullComponent<Sender> JEB_TRAP_ACTIVATE = direct("murder_run.game.gadget.jeb.activate");
  NullComponent<Sender> SHOCKWAVE_TRAP_ACTIVATE =
      direct("murder_run.game.gadget.shockwave.activate");
  NullComponent<Sender> MED_BOT_TRAP_DEACTIVATE =
      direct("murder_run.game.gadget.med_bot.deactivate");
  NullComponent<Sender> PARASITE_TRAP_DEACTIVATE =
      direct("murder_run.game.gadget.parasite.deactivate");
  NullComponent<Sender> DISTORTER_TRAP_DEACTIVATE =
      direct("murder_run.game.gadget.distorter.deactivate");
  NullComponent<Sender> SURVIVOR_HELMET = direct("murder_run.game.gadget.helmet");
  NullComponent<Sender> SURVIVOR_CHESTPLATE = direct("murder_run.game.gadget.chestplate");
  NullComponent<Sender> SURVIVOR_LEGGINGS = direct("murder_run.game.gadget.leggings");
  NullComponent<Sender> SURVIVOR_BOOTS = direct("murder_run.game.gadget.boots");
  NullComponent<Sender> PONY_TRAP_ACTIVATE = direct("murder_run.game.gadget.pony.activate");
  UniComponent<Sender, String> ARENA_REMOVE = direct("murder_run.command.arena.remove", null);
  UniComponent<Sender, String> LOBBY_REMOVE = direct("murder_run.command.lobby.remove", null);
  NullComponent<Sender> ARENA_REMOVE_ERROR = direct("murder_run.command.arena.remove_error");
  NullComponent<Sender> LOBBY_REMOVE_ERROR = direct("murder_run.command.lobby.remove_error");
  NullComponent<Sender> LIFE_INSURANCE_ACTIVATE =
      direct("murder_run.game.gadget.life_insurance.activate");
  NullComponent<Sender> TRACKER_TRAP_ACTIVATE = direct("murder_run.game.gadget.tracker.activate");
  NullComponent<Sender> TRACKER_TRAP_DEACTIVATE =
      direct("murder_run.game.gadget.tracker.deactivate");
  UniComponent<Sender, Double> KILLER_TRACKER_ACTIVATE =
      direct("murder_run.game.gadget.killer_tracker.activate", null);
  NullComponent<Sender> PLAYER_TRACKER_TRAP_NAME =
      direct("murder_run.game.gadget.player_tracker.name");
  NullComponent<Sender> KILLER_CAMERA_TRAP_NAME =
      direct("murder_run.game.gadget.camera_killer.name");
  NullComponent<Sender> WARP_DISTORT_TRAP_NAME = direct("murder_run.game.gadget.warp_distort.name");
  NullComponent<Sender> PAINT_TRAP_NAME = direct("murder_run.game.gadget.paint_trap.name");
  NullComponent<Sender> TRAP_WRECKER_TRAP_NAME = direct("murder_run.game.gadget.trap_wrecker.name");
  NullComponent<Sender> TRAP_SEEKER_TRAP_NAME = direct("murder_run.game.gadget.trap_seeker.name");
  NullComponent<Sender> INFRARED_VISION_TRAP_NAME =
      direct("murder_run.game.gadget.infrared_vision.name");
  NullComponent<Sender> FRIGHT_TRAP_NAME = direct("murder_run.game.gadget.fright.name");
  NullComponent<Sender> BLOOD_CURSE_TRAP_NAME = direct("murder_run.game.gadget.blood_curse.name");
  NullComponent<Sender> DEATH_STEED_TRAP_NAME = direct("murder_run.game.gadget.death_steed.name");
  NullComponent<Sender> ALL_SEEING_EYE_TRAP_NAME =
      direct("murder_run.game.gadget.all_seeing_eye.name");
  NullComponent<Sender> PHANTOM_TRAP_NAME = direct("murder_run.game.gadget.phantom.name");
  NullComponent<Sender> DEATH_HOUND_TRAP_NAME = direct("murder_run.game.gadget.death_hound.name");
  NullComponent<Sender> CORRUPTION_TRAP_NAME = direct("murder_run.game.gadget.corruption.name");
  NullComponent<Sender> MURDEROUS_WARP_TRAP_NAME =
      direct("murder_run.game.gadget.murderous_warp.name");
  NullComponent<Sender> PORTAL_GUN_TRAP_NAME = direct("murder_run.game.gadget.portal_gun.name");
  NullComponent<Sender> THE_REAPER_TRAP_NAME = direct("murder_run.game.gadget.the_reaper.name");
  NullComponent<Sender> HEAT_SEEKER_TRAP_NAME = direct("murder_run.game.gadget.heat_seeker.name");
  NullComponent<Sender> THE_FLOOR_IS_LAVA_TRAP_NAME =
      direct("murder_run.game.gadget.the_floor_is_lava.name");
  NullComponent<Sender> EMP_BLAST_TRAP_NAME = direct("murder_run.game.gadget.emp_blast.name");
  NullComponent<Sender> GAMBLE_TRAP_NAME = direct("murder_run.game.gadget.gamble.name");
  NullComponent<Sender> QUICK_BOMB_TRAP_NAME = direct("murder_run.game.gadget.quick_bomb.name");
  NullComponent<Sender> HEALTH_CUT_TRAP_NAME = direct("murder_run.game.gadget.health_cut.name");
  NullComponent<Sender> POISON_SMOG_TRAP_NAME = direct("murder_run.game.gadget.poison_smog.name");
  NullComponent<Sender> PART_WARP_TRAP_NAME = direct("murder_run.game.gadget.part_warp.name");
  NullComponent<Sender> HOOK_TRAP_NAME = direct("murder_run.game.gadget.hook.name");
  NullComponent<Sender> EAGLE_EYE_NAME = direct("murder_run.game.gadget.eagle_eye.name");
  NullComponent<Sender> FAKE_PART_NAME = direct("murder_run.game.gadget.fake_part.name");
  NullComponent<Sender> BURN_THE_BODY_NAME = direct("murder_run.game.gadget.burn_the_body.name");
  NullComponent<Sender> PUMPKIN_DISEASE_TRAP_NAME =
      direct("murder_run.game.gadget.pumpkin_disease.name");
  NullComponent<Sender> ENDER_SHADOWS_TRAP_NAME =
      direct("murder_run.game.gadget.ender_shadows.name");
  NullComponent<Sender> FOREWARN_TRAP_NAME = direct("murder_run.game.gadget.forewarn.name");
  NullComponent<Sender> FIRE_TRAIL_TRAP_NAME = direct("murder_run.game.gadget.fire_trail.name");
  NullComponent<Sender> ICE_PATH_TRAP_NAME = direct("murder_run.game.gadget.ice_path.name");
  NullComponent<Sender> DORMAGOGG_TRAP_NAME = direct("murder_run.game.gadget.dormagogg.name");
  NullComponent<Sender> CURSED_NOTE_TRAP_NAME = direct("murder_run.game.gadget.cursed_note.name");
  NullComponent<Sender> RED_ARROW_TRAP_NAME = direct("murder_run.game.gadget.red_arrow.name");
  NullComponent<Sender> PLAYER_TRACKER_TRAP_LORE =
      direct("murder_run.game.gadget.player_tracker.lore");
  NullComponent<Sender> KILLER_CAMERA_TRAP_LORE =
      direct("murder_run.game.gadget.killer_camera.lore");
  NullComponent<Sender> WARP_DISTORT_TRAP_LORE = direct("murder_run.game.gadget.warp_distort.lore");
  NullComponent<Sender> PAINT_TRAP_TRAP_LORE = direct("murder_run.game.gadget.paint_trap.lore");
  NullComponent<Sender> TRAP_WRECKER_TRAP_LORE = direct("murder_run.game.gadget.trap_wrecker.lore");
  NullComponent<Sender> TRAP_SEEKER_TRAP_LORE = direct("murder_run.game.gadget.trap_seeker.lore");
  NullComponent<Sender> INFRARED_VISION_TRAP_LORE =
      direct("murder_run.game.gadget.infrared_vision.lore");
  NullComponent<Sender> FRIGHT_TRAP_LORE = direct("murder_run.game.gadget.fright.lore");
  NullComponent<Sender> BLOOD_CURSE_TRAP_LORE = direct("murder_run.game.gadget.blood_curse.lore");
  NullComponent<Sender> DEATH_STEED_TRAP_LORE = direct("murder_run.game.gadget.death_steed.lore");
  NullComponent<Sender> ALL_SEEING_EYE_TRAP_LORE =
      direct("murder_run.game.gadget.all_seeing_eye.lore");
  NullComponent<Sender> PHANTOM_TRAP_LORE = direct("murder_run.game.gadget.phantom.lore");
  NullComponent<Sender> DEATH_HOUND_TRAP_LORE = direct("murder_run.game.gadget.death_hound.lore");
  NullComponent<Sender> CORRUPTION_TRAP_LORE = direct("murder_run.game.gadget.corruption.lore");
  NullComponent<Sender> MURDEROUS_WARP_TRAP_LORE =
      direct("murder_run.game.gadget.murderous_warp.lore");
  NullComponent<Sender> PORTAL_GUN_TRAP_LORE = direct("murder_run.game.gadget.portal_gun.lore");
  NullComponent<Sender> THE_REAPER_TRAP_LORE = direct("murder_run.game.gadget.the_reaper.lore");
  NullComponent<Sender> HEAT_SEEKER_TRAP_LORE = direct("murder_run.game.gadget.heat_seeker.lore");
  NullComponent<Sender> THE_FLOOR_IS_LAVA_TRAP_LORE =
      direct("murder_run.game.gadget.the_floor_is_lava.lore");
  NullComponent<Sender> EMP_BLAST_TRAP_LORE = direct("murder_run.game.gadget.emp_blast.lore");
  NullComponent<Sender> GAMBLE_TRAP_LORE = direct("murder_run.game.gadget.gamble.lore");
  NullComponent<Sender> QUICK_BOMB_TRAP_LORE = direct("murder_run.game.gadget.quick_bomb.lore");
  NullComponent<Sender> HEALTH_CUT_TRAP_LORE = direct("murder_run.game.gadget.health_cut.lore");
  NullComponent<Sender> POISON_SMOG_TRAP_LORE = direct("murder_run.game.gadget.poison_smog.lore");
  NullComponent<Sender> PART_WARP_TRAP_LORE = direct("murder_run.game.gadget.part_warp.lore");
  NullComponent<Sender> HOOK_TRAP_LORE = direct("murder_run.game.gadget.hook.lore");
  NullComponent<Sender> EAGLE_EYE_TRAP_LORE = direct("murder_run.game.gadget.eagle_eye.lore");
  NullComponent<Sender> FAKE_PART_TRAP_LORE = direct("murder_run.game.gadget.fake_part.lore");
  NullComponent<Sender> BURN_THE_BODY_TRAP_LORE =
      direct("murder_run.game.gadget.burn_the_body.lore");
  NullComponent<Sender> PUMPKIN_DISEASE_TRAP_LORE =
      direct("murder_run.game.gadget.pumpkin_disease.lore");
  NullComponent<Sender> ENDER_SHADOWS_TRAP_LORE =
      direct("murder_run.game.gadget.ender_shadows.lore");
  NullComponent<Sender> FOREWARN_TRAP_LORE = direct("murder_run.game.gadget.forewarn.lore");
  NullComponent<Sender> FIRE_TRAIL_TRAP_LORE = direct("murder_run.game.gadget.fire_trail.lore");
  NullComponent<Sender> ICE_PATH_TRAP_LORE = direct("murder_run.game.gadget.ice_path.lore");
  NullComponent<Sender> DORMAGOGG_TRAP_LORE = direct("murder_run.game.gadget.dormagogg.lore");
  NullComponent<Sender> CURSED_NOTE_TRAP_LORE = direct("murder_run.game.gadget.cursed_note.lore");
  NullComponent<Sender> RED_ARROW_TRAP_LORE = direct("murder_run.game.gadget.red_arrow.lore");
  UniComponent<Sender, Double> PLAYER_TRACKER_ACTIVATE =
      direct("murder_run.game.gadget.player_tracker.activate", null);
  NullComponent<Sender> KILLER_CAMERA_ACTIVATE =
      direct("murder_run.game.gadget.killer_camera.activate");
  NullComponent<Sender> WARP_DISTORT_ACTIVATE =
      direct("murder_run.game.gadget.warp_distort.activate");
  NullComponent<Sender> PAINT_TRAP_ACTIVATE = direct("murder_run.game.gadget.paint_trap.activate");
  NullComponent<Sender> TRAP_WRECKER_ACTIVATE =
      direct("murder_run.game.gadget.trap_wrecker.activate");
  NullComponent<Sender> TRAP_SEEKER_ACTIVATE =
      direct("murder_run.game.gadget.trap_seeker.activate");
  NullComponent<Sender> INFRARED_VISION_ACTIVATE =
      direct("murder_run.game.gadget.infrared_vision.activate");
  NullComponent<Sender> FRIGHT_ACTIVATE = direct("murder_run.game.gadget.fright.activate");
  NullComponent<Sender> BLOOD_CURSE_ACTIVATE =
      direct("murder_run.game.gadget.blood_curse.activate");
  NullComponent<Sender> DEATH_STEED_ACTIVATE =
      direct("murder_run.game.gadget.death_steed.activate");
  NullComponent<Sender> ALL_SEEING_EYE_ACTIVATE =
      direct("murder_run.game.gadget.all_seeing_eye.activate");
  NullComponent<Sender> PHANTOM_ACTIVATE = direct("murder_run.game.gadget.phantom.activate");
  NullComponent<Sender> DEATH_HOUND_ACTIVATE =
      direct("murder_run.game.gadget.death_hound.activate");
  NullComponent<Sender> CORRUPTION_ACTIVATE = direct("murder_run.game.gadget.corruption.activate");
  NullComponent<Sender> MURDEROUS_WARP_ACTIVATE =
      direct("murder_run.game.gadget.murderous_warp.activate");
  NullComponent<Sender> PORTAL_GUN_ACTIVATE = direct("murder_run.game.gadget.portal_gun.activate");
  NullComponent<Sender> THE_REAPER_ACTIVATE = direct("murder_run.game.gadget.the_reaper.activate");
  NullComponent<Sender> HEAT_SEEKER_ACTIVATE =
      direct("murder_run.game.gadget.heat_seeker.activate");
  NullComponent<Sender> THE_FLOOR_IS_LAVA_ACTIVATE =
      direct("murder_run.game.gadget.the_floor_is_lava.activate");
  NullComponent<Sender> EMP_BLAST_ACTIVATE = direct("murder_run.game.gadget.emp_blast.activate");
  NullComponent<Sender> GAMBLE_ACTIVATE = direct("murder_run.game.gadget.gamble.activate");
  NullComponent<Sender> QUICK_BOMB_ACTIVATE = direct("murder_run.game.gadget.quick_bomb.activate");
  NullComponent<Sender> HEALTH_CUT_ACTIVATE = direct("murder_run.game.gadget.health_cut.activate");
  NullComponent<Sender> POISON_SMOG_ACTIVATE =
      direct("murder_run.game.gadget.poison_smog.activate");
  NullComponent<Sender> PART_WARP_ACTIVATE = direct("murder_run.game.gadget.part_warp.activate");
  NullComponent<Sender> HOOK_ACTIVATE = direct("murder_run.game.gadget.hook.activate");
  NullComponent<Sender> EAGLE_EYE_ACTIVATE = direct("murder_run.game.gadget.eagle_eye.activate");
  NullComponent<Sender> FAKE_PART_ACTIVATE = direct("murder_run.game.gadget.fake_part.activate");
  NullComponent<Sender> BURN_THE_BODY_ACTIVATE =
      direct("murder_run.game.gadget.burn_the_body.activate");
  NullComponent<Sender> PUMPKIN_DISEASE_ACTIVATE =
      direct("murder_run.game.gadget.pumpkin_disease.activate");
  NullComponent<Sender> ENDER_SHADOWS_ACTIVATE =
      direct("murder_run.game.gadget.ender_shadows.activate");
  NullComponent<Sender> FOREWARN_ACTIVATE = direct("murder_run.game.gadget.forewarn.activate");
  NullComponent<Sender> FIRE_TRAIL_ACTIVATE = direct("murder_run.game.gadget.fire_trail.activate");
  NullComponent<Sender> ICE_PATH_ACTIVATE = direct("murder_run.game.gadget.ice_path.activate");
  NullComponent<Sender> DORMAGOGG_ACTIVATE = direct("murder_run.game.gadget.dormagogg.activate");
  NullComponent<Sender> CURSED_NOTE_ACTIVATE =
      direct("murder_run.game.gadget.cursed_note.activate");
  NullComponent<Sender> RED_ARROW_ACTIVATE = direct("murder_run.game.gadget.red_arrow.activate");
}
