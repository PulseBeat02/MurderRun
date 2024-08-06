package io.github.pulsebeat02.murderrun.locale;

import static io.github.pulsebeat02.murderrun.locale.LocaleTooling.direct;

import java.util.List;

public interface Locale extends LocaleTooling {

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
      direct("murder_run.command.game.set.murderer", null);
  UniComponent<Sender, String> GAME_SET_INNOCENT =
      direct("murder_run.command.game.set.innocent", null);
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
  NullComponent<Sender> HACK_TRAP_NAME = direct("murder_run.game.trap.hack.name");
  NullComponent<Sender> GLOW_TRAP_NAME = direct("murder_run.game.trap.glow.name");
  NullComponent<Sender> BEAR_TRAP_NAME = direct("murder_run.game.trap.bear.name");
  NullComponent<Sender> PORTAL_TRAP_NAME = direct("murder_run.game.trap.portal.name");
  NullComponent<Sender> SPASM_TRAP_NAME = direct("murder_run.game.trap.spasm.name");
  NullComponent<Sender> REWIND_TRAP_NAME = direct("murder_run.game.trap.rewind.name");
  NullComponent<Sender> MURDERER_REWIND_TRAP_NAME =
      direct("murder_run.game.trap.murderer_rewind.name");
  NullComponent<Sender> RESURRECTION_STONE_TRAP_NAME =
      direct("murder_run.game.trap.resurrection_stone.name");
  NullComponent<Sender> CORPUS_WARP_TRAP_NAME = direct("murder_run.game.trap.corpus_warp.name");
  NullComponent<Sender> GHOSTING_TRAP_NAME = direct("murder_run.game.trap.ghosting.name");
  NullComponent<Sender> EXCAVATOR_TRAP_NAME = direct("murder_run.game.trap.excavator.name");
  NullComponent<Sender> DISTORT_TRAP_NAME = direct("murder_run.game.trap.distort.name");
  NullComponent<Sender> HORCRUX_TRAP_NAME = direct("murder_run.game.trap.horcrux.name");
  NullComponent<Sender> MED_BOT_TRAP_NAME = direct("murder_run.game.trap.med_bot.name");
  NullComponent<Sender> RETALIATION_TRAP_NAME = direct("murder_run.game.trap.retaliation.name");
  NullComponent<Sender> SUPPLY_DROP_TRAP_NAME = direct("murder_run.game.trap.supply_drop.name");
  NullComponent<Sender> MED_KIT_TRAP_NAME = direct("murder_run.game.trap.med_kit.name");
  NullComponent<Sender> JUMP_SCARE_TRAP_NAME = direct("murder_run.game.trap.jump_scare.name");
  NullComponent<Sender> SMOKE_TRAP_NAME = direct("murder_run.game.trap.smoke.name");
  NullComponent<Sender> LEVITATION_TRAP_NAME = direct("murder_run.game.trap.levitation.name");
  NullComponent<Sender> CAGE_TRAP_NAME = direct("murder_run.game.trap.cage.name");
  NullComponent<Sender> BLIND_TRAP_NAME = direct("murder_run.game.trap.blind.name");
  NullComponent<Sender> HAUNT_TRAP_NAME = direct("murder_run.game.trap.haunt.name");
  NullComponent<Sender> NECK_SNAP_TRAP_NAME = direct("murder_run.game.trap.neck_snap.name");
  NullComponent<Sender> DEADRINGER_TRAP_NAME = direct("murder_run.game.trap.deadringer.name");
  NullComponent<Sender> STAR_TRAP_NAME = direct("murder_run.game.trap.star.name");
  NullComponent<Sender> SPAWN_TRAP_NAME = direct("murder_run.game.trap.spawn.name");
  NullComponent<Sender> FREEZE_TRAP_NAME = direct("murder_run.game.trap.freeze.name");
  NullComponent<Sender> BURROW_TRAP_NAME = direct("murder_run.game.trap.burrow.name");
  NullComponent<Sender> GHOST_TRAP_NAME = direct("murder_run.game.trap.ghost.name");
  NullComponent<Sender> PONY_TRAP_NAME = direct("murder_run.game.trap.pony.name");
  NullComponent<Sender> FIREWORK_TRAP_NAME = direct("murder_run.game.trap.firework.name");
  NullComponent<Sender> FART_TRAP_NAME = direct("murder_run.game.trap.fart.name");
  NullComponent<Sender> TRAP_VEST_TRAP_NAME = direct("murder_run.game.trap.trap_vest.name");
  NullComponent<Sender> RANDOM_TRAP_NAME = direct("murder_run.game.trap.random.name");
  NullComponent<Sender> MAGNET_MODE_TRAP_NAME = direct("murder_run.game.trap.magnet_mode.name");
  NullComponent<Sender> TRANSLOCATOR_TRAP_NAME = direct("murder_run.game.trap.translocator.name");
  NullComponent<Sender> TRACKER_TRAP_NAME = direct("murder_run.game.trap.tracker.name");
  NullComponent<Sender> DECOY_TRAP_NAME = direct("murder_run.game.trap.decoy.name");
  NullComponent<Sender> SMOKE_BOMB_TRAP_NAME = direct("murder_run.game.trap.smoke_bomb.name");
  NullComponent<Sender> FRIEND_WARP_TRAP_NAME = direct("murder_run.game.trap.friend_warp.name");
  NullComponent<Sender> FLASHBANG_TRAP_NAME = direct("murder_run.game.trap.flashbang.name");
  NullComponent<Sender> CAMERA_TRAP_NAME = direct("murder_run.game.trap.camera.name");
  NullComponent<Sender> CLOAK_TRAP_NAME = direct("murder_run.game.trap.cloak.name");
  NullComponent<Sender> SHIELD_TRAP_NAME = direct("murder_run.game.trap.shield.name");
  NullComponent<Sender> TP_ME_AWAY_FROM_HERE_TRAP_NAME =
      direct("murder_run.game.trap.tp_me_away_from_here.name");
  NullComponent<Sender> SIXTH_SENSE_TRAP_NAME = direct("murder_run.game.trap.sixth_sense.name");
  NullComponent<Sender> BLAST_OFF_TRAP_NAME = direct("murder_run.game.trap.blast_off.name");
  NullComponent<Sender> DRONE_TRAP_NAME = direct("murder_run.game.trap.drone.name");
  NullComponent<Sender> TRAP_SNIFFER_TRAP_NAME = direct("murder_run.game.trap.trap_sniffer.name");
  NullComponent<Sender> CHIPPED_TRAP_NAME = direct("murder_run.game.trap.chipped.name");
  NullComponent<Sender> LIFE_INSURANCE_TRAP_NAME =
      direct("murder_run.game.trap.life_insurance.name");
  NullComponent<Sender> CRYO_FREEZE_TRAP_NAME = direct("murder_run.game.trap.cryo_freeze.name");
  NullComponent<Sender> ICE_SKATIN_TRAP_NAME = direct("murder_run.game.trap.ice_skatin.name");
  NullComponent<Sender> ICE_SPIRIT_TRAP_NAME = direct("murder_run.game.trap.ice_spirit.name");
  NullComponent<Sender> MIND_CONTROL_TRAP_NAME = direct("murder_run.game.trap.mind_control.name");
  NullComponent<Sender> JEB_TRAP_NAME = direct("murder_run.game.trap.jeb.name");
  NullComponent<Sender> BUSH_TRAP_NAME = direct("murder_run.game.trap.bush.name");
  NullComponent<Sender> KILLER_TRACKER_TRAP_NAME =
      direct("murder_run.game.trap.killer_tracker.name");
  NullComponent<Sender> FLASHLIGHT_TRAP_NAME = direct("murder_run.game.trap.flashlight.name");
  NullComponent<Sender> SHOCKWAVE_TRAP_NAME = direct("murder_run.game.trap.shockwave.name");
  NullComponent<Sender> PARASITE_TRAP_NAME = direct("murder_run.game.trap.parasite.name");
  NullComponent<Sender> DISTORTER_TRAP_NAME = direct("murder_run.game.trap.distorter.name");
  NullComponent<Sender> SPEED_PENDANT_NAME = direct("murder_run.game.trap.speed_pendant.name");
  NullComponent<Sender> GLOW_TRAP_LORE = direct("murder_run.game.trap.glow.lore");
  NullComponent<Sender> HACK_TRAP_LORE = direct("murder_run.game.trap.hack.lore");
  NullComponent<Sender> BEAR_TRAP_LORE = direct("murder_run.game.trap.bear.lore");
  NullComponent<Sender> PORTAL_TRAP_LORE = direct("murder_run.game.trap.portal.lore");
  NullComponent<Sender> SPASM_TRAP_LORE = direct("murder_run.game.trap.spasm.lore");
  NullComponent<Sender> REWIND_TRAP_LORE = direct("murder_run.game.trap.rewind.lore");
  NullComponent<Sender> MURDERER_REWIND_TRAP_LORE =
      direct("murder_run.game.trap.murderer_rewind.lore");
  NullComponent<Sender> RESURRECTION_STONE_TRAP_LORE =
      direct("murder_run.game.trap.resurrection_stone.lore");
  NullComponent<Sender> CORPUS_WARP_TRAP_LORE = direct("murder_run.game.trap.corpus_warp.lore");
  NullComponent<Sender> GHOSTING_TRAP_LORE = direct("murder_run.game.trap.ghosting.lore");
  NullComponent<Sender> EXCAVATOR_TRAP_LORE = direct("murder_run.game.trap.excavator.lore");
  NullComponent<Sender> DISTORT_TRAP_LORE = direct("murder_run.game.trap.distort.lore");
  NullComponent<Sender> HORCRUX_TRAP_LORE = direct("murder_run.game.trap.horcrux.lore");
  NullComponent<Sender> MED_BOT_TRAP_LORE = direct("murder_run.game.trap.med_bot.lore");
  NullComponent<Sender> RETALIATION_TRAP_LORE = direct("murder_run.game.trap.retaliation.lore");
  NullComponent<Sender> SUPPLY_DROP_TRAP_LORE = direct("murder_run.game.trap.supply_drop.lore");
  NullComponent<Sender> MED_KIT_TRAP_LORE = direct("murder_run.game.trap.med_kit.lore");
  NullComponent<Sender> JUMP_SCARE_TRAP_LORE = direct("murder_run.game.trap.jump_scare.lore");
  NullComponent<Sender> SMOKE_TRAP_LORE = direct("murder_run.game.trap.smoke.lore");
  NullComponent<Sender> LEVITATION_TRAP_LORE = direct("murder_run.game.trap.levitation.lore");
  NullComponent<Sender> CAGE_TRAP_LORE = direct("murder_run.game.trap.cage.lore");
  NullComponent<Sender> BLIND_TRAP_LORE = direct("murder_run.game.trap.blind.lore");
  NullComponent<Sender> HAUNT_TRAP_LORE = direct("murder_run.game.trap.haunt.lore");
  NullComponent<Sender> NECK_SNAP_TRAP_LORE = direct("murder_run.game.trap.neck_snap.lore");
  NullComponent<Sender> DEADRINGER_TRAP_LORE = direct("murder_run.game.trap.deadringer.lore");
  NullComponent<Sender> STAR_TRAP_LORE = direct("murder_run.game.trap.star.lore");
  NullComponent<Sender> SPAWN_TRAP_LORE = direct("murder_run.game.trap.spawn.lore");
  NullComponent<Sender> FREEZE_TRAP_LORE = direct("murder_run.game.trap.freeze.lore");
  NullComponent<Sender> BURROW_TRAP_LORE = direct("murder_run.game.trap.burrow.lore");
  NullComponent<Sender> GHOST_TRAP_LORE = direct("murder_run.game.trap.ghost.lore");
  NullComponent<Sender> PONY_TRAP_LORE = direct("murder_run.game.trap.pony.lore");
  NullComponent<Sender> FIREWORK_TRAP_LORE = direct("murder_run.game.trap.firework.lore");
  NullComponent<Sender> FART_TRAP_LORE = direct("murder_run.game.trap.fart.lore");
  NullComponent<Sender> TRAP_VEST_TRAP_LORE = direct("murder_run.game.trap.trap_vest.lore");
  NullComponent<Sender> RANDOM_TRAP_LORE = direct("murder_run.game.trap.random.lore");
  NullComponent<Sender> MAGNET_MODE_TRAP_LORE = direct("murder_run.game.trap.magnet_mode.lore");
  NullComponent<Sender> TRANSLOCATOR_TRAP_LORE = direct("murder_run.game.trap.translocator.lore");
  NullComponent<Sender> TRANSLOCATOR_TRAP_LORE1 = direct("murder_run.game.trap.translocator.lore1");
  NullComponent<Sender> TRACKER_TRAP_LORE = direct("murder_run.game.trap.tracker.lore");
  NullComponent<Sender> DECOY_TRAP_LORE = direct("murder_run.game.trap.decoy.lore");
  NullComponent<Sender> SMOKE_BOMB_TRAP_LORE = direct("murder_run.game.trap.smoke_bomb.lore");
  NullComponent<Sender> FRIEND_WARP_TRAP_LORE = direct("murder_run.game.trap.friend_warp.lore");
  NullComponent<Sender> FLASHBANG_TRAP_LORE = direct("murder_run.game.trap.flashbang.lore");
  NullComponent<Sender> CAMERA_TRAP_LORE = direct("murder_run.game.trap.camera.lore");
  NullComponent<Sender> CLOAK_TRAP_LORE = direct("murder_run.game.trap.cloak.lore");
  NullComponent<Sender> SHIELD_TRAP_LORE = direct("murder_run.game.trap.shield.lore");
  NullComponent<Sender> TP_ME_AWAY_FROM_HERE_TRAP_LORE =
      direct("murder_run.game.trap.tp_me_away_from_here.lore");
  NullComponent<Sender> SIXTH_SENSE_TRAP_LORE = direct("murder_run.game.trap.sixth_sense.lore");
  NullComponent<Sender> BLAST_OFF_TRAP_LORE = direct("murder_run.game.trap.blast_off.lore");
  NullComponent<Sender> DRONE_TRAP_LORE = direct("murder_run.game.trap.drone.lore");
  NullComponent<Sender> TRAP_SNIFFER_TRAP_LORE = direct("murder_run.game.trap.trap_sniffer.lore");
  NullComponent<Sender> CHIPPED_TRAP_LORE = direct("murder_run.game.trap.chipped.lore");
  NullComponent<Sender> LIFE_INSURANCE_TRAP_LORE =
      direct("murder_run.game.trap.life_insurance.lore");
  NullComponent<Sender> CRYO_FREEZE_TRAP_LORE = direct("murder_run.game.trap.cryo_freeze.lore");
  NullComponent<Sender> ICE_SKATIN_TRAP_LORE = direct("murder_run.game.trap.ice_skatin.lore");
  NullComponent<Sender> ICE_SPIRIT_TRAP_LORE = direct("murder_run.game.trap.ice_spirit.lore");
  NullComponent<Sender> MIND_CONTROL_TRAP_LORE = direct("murder_run.game.trap.mind_control.lore");
  NullComponent<Sender> JEB_TRAP_LORE = direct("murder_run.game.trap.jeb.lore");
  NullComponent<Sender> BUSH_TRAP_LORE = direct("murder_run.game.trap.bush.lore");
  NullComponent<Sender> KILLER_TRACKER_TRAP_LORE =
      direct("murder_run.game.trap.killer_tracker.lore");
  NullComponent<Sender> FLASHLIGHT_TRAP_LORE = direct("murder_run.game.trap.flashlight.lore");
  NullComponent<Sender> SHOCKWAVE_TRAP_LORE = direct("murder_run.game.trap.shockwave.lore");
  NullComponent<Sender> PARASITE_TRAP_LORE = direct("murder_run.game.trap.parasite.lore");
  NullComponent<Sender> DISTORTER_TRAP_LORE = direct("murder_run.game.trap.distorter.lore");
  NullComponent<Sender> GLOW_TRAP_ACTIVATE = direct("murder_run.game.trap.glow.activate");
  NullComponent<Sender> HACK_TRAP_ACTIVATE = direct("murder_run.game.trap.hack.activate");
  NullComponent<Sender> BEAR_TRAP_ACTIVATE = direct("murder_run.game.trap.bear.activate");
  NullComponent<Sender> SPASM_TRAP_ACTIVATE = direct("murder_run.game.trap.spasm.activate");
  NullComponent<Sender> RESURRECTION_STONE_TRAP_ACTIVATE =
      direct("murder_run.game.trap.resurrection_stone.activate");
  NullComponent<Sender> GHOSTING_TRAP_ACTIVATE = direct("murder_run.game.trap.ghosting.activate");
  NullComponent<Sender> DISTORT_TRAP_ACTIVATE = direct("murder_run.game.trap.distort.activate");
  NullComponent<Sender> HORCRUX_TRAP_ACTIVATE = direct("murder_run.game.trap.horcrux.activate");
  NullComponent<Sender> RETALIATION_TRAP_ACTIVATE =
      direct("murder_run.game.trap.retaliation.activate");
  NullComponent<Sender> JUMP_SCARE_TRAP_ACTIVATE =
      direct("murder_run.game.trap.jump_scare.activate");
  NullComponent<Sender> SMOKE_TRAP_ACTIVATE = direct("murder_run.game.trap.smoke.activate");
  NullComponent<Sender> LEVITATION_TRAP_ACTIVATE =
      direct("murder_run.game.trap.levitation.activate");
  NullComponent<Sender> CAGE_TRAP_ACTIVATE = direct("murder_run.game.trap.cage.activate");
  NullComponent<Sender> BLIND_TRAP_ACTIVATE = direct("murder_run.game.trap.blind.activate");
  NullComponent<Sender> HAUNT_TRAP_ACTIVATE = direct("murder_run.game.trap.haunt.activate");
  NullComponent<Sender> NECK_SNAP_TRAP_ACTIVATE = direct("murder_run.game.trap.neck_snap.activate");
  NullComponent<Sender> STAR_TRAP_ACTIVATE = direct("murder_run.game.trap.star.activate");
  NullComponent<Sender> SPAWN_TRAP_ACTIVATE = direct("murder_run.game.trap.spawn.activate");
  NullComponent<Sender> FREEZE_TRAP_ACTIVATE = direct("murder_run.game.trap.freeze.activate");
  NullComponent<Sender> BURROW_TRAP_ACTIVATE = direct("murder_run.game.trap.burrow.activate");
  NullComponent<Sender> GHOST_TRAP_ACTIVATE = direct("murder_run.game.trap.ghost.activate");
  NullComponent<Sender> FIREWORK_TRAP_ACTIVATE = direct("murder_run.game.trap.firework.activate");
  NullComponent<Sender> FART_TRAP_ACTIVATE = direct("murder_run.game.trap.fart.activate");
  NullComponent<Sender> TRAP_VEST_ACTIVATE = direct("murder_run.game.trap.trap_vest.activate");
  NullComponent<Sender> MAGNET_MODE_TRAP_ACTIVATE =
      direct("murder_run.game.trap.magnet_mode.activate");
  NullComponent<Sender> CLOAK_TRAP_ACTIVATE = direct("murder_run.game.trap.cloak.activate");
  NullComponent<Sender> SIXTH_SENSE_TRAP_ACTIVATE =
      direct("murder_run.game.trap.sixth_sense.activate");
  NullComponent<Sender> BLAST_OFF_TRAP_ACTIVATE = direct("murder_run.game.trap.blast_off.activate");
  NullComponent<Sender> TRAP_SNIFFER_TRAP_ACTIVATE =
      direct("murder_run.game.trap.trap_sniffer.activate");
  NullComponent<Sender> JEB_TRAP_ACTIVATE = direct("murder_run.game.trap.jeb.activate");
  NullComponent<Sender> SHOCKWAVE_TRAP_ACTIVATE = direct("murder_run.game.trap.shockwave.activate");
  NullComponent<Sender> MED_BOT_TRAP_DEACTIVATE = direct("murder_run.game.trap.med_bot.deactivate");
  NullComponent<Sender> PARASITE_TRAP_DEACTIVATE =
      direct("murder_run.game.trap.parasite.deactivate");
  NullComponent<Sender> DISTORTER_TRAP_DEACTIVATE =
      direct("murder_run.game.trap.distorter.deactivate");
  NullComponent<Sender> SURVIVOR_HELMET = direct("murder_run.game.trap.helmet");
  NullComponent<Sender> SURVIVOR_CHESTPLATE = direct("murder_run.game.trap.chestplate");
  NullComponent<Sender> SURVIVOR_LEGGINGS = direct("murder_run.game.trap.leggings");
  NullComponent<Sender> SURVIVOR_BOOTS = direct("murder_run.game.trap.boots");
  NullComponent<Sender> PONY_TRAP_ACTIVATE = direct("murder_run.game.trap.pony.activate");
  UniComponent<Sender, String> ARENA_REMOVE = direct("murder_run.command.arena.remove", null);
  UniComponent<Sender, String> LOBBY_REMOVE = direct("murder_run.command.lobby.remove", null);
  NullComponent<Sender> ARENA_REMOVE_ERROR = direct("murder_run.command.arena.remove_error");
  NullComponent<Sender> LOBBY_REMOVE_ERROR = direct("murder_run.command.lobby.remove_error");
  NullComponent<Sender> LIFE_INSURANCE_ACTIVATE =
      direct("murder_run.game.trap.life_insurance.activate");
  NullComponent<Sender> TRACKER_TRAP_ACTIVATE = direct("murder_run.game.trap.tracker.activate");
  NullComponent<Sender> TRACKER_TRAP_DEACTIVATE = direct("murder_run.game.trap.tracker.deactivate");
  UniComponent<Sender, Double> KILLER_TRACKER_ACTIVATE =
      direct("murder_run.game.trap.killer_tracker.activate", null);
  NullComponent<Sender> PLAYER_TRACKER_TRAP_NAME = direct("murder_run.game.trap.player_tracker.name");
  NullComponent<Sender> CAMERA_KILLER_TRAP_NAME = direct("murder_run.game.trap.camera_killer.name");
  NullComponent<Sender> WARP_DISTORT_TRAP_NAME = direct("murder_run.game.trap.warp_distort.name");
  NullComponent<Sender> PAINT_TRAP_NAME = direct("murder_run.game.trap.paint_trap.name");
  NullComponent<Sender> TRAP_WRECKER_TRAP_NAME = direct("murder_run.game.trap.trap_wrecker.name");
  NullComponent<Sender> TRAP_SEEKER_TRAP_NAME = direct("murder_run.game.trap.trap_seeker.name");
  NullComponent<Sender> INFRARED_VISION_TRAP_NAME = direct("murder_run.game.trap.infrared_vision.name");
  NullComponent<Sender> FRIGHT_TRAP_NAME = direct("murder_run.game.trap.fright.name");
  NullComponent<Sender> BLOOD_CURSE_TRAP_NAME = direct("murder_run.game.trap.blood_curse.name");
  NullComponent<Sender> DEATH_STEED_TRAP_NAME = direct("murder_run.game.trap.death_steed.name");
  NullComponent<Sender> ALL_SEEING_EYE_TRAP_NAME = direct("murder_run.game.trap.all_seeing_eye.name");
  NullComponent<Sender> PHANTOM_TRAP_NAME = direct("murder_run.game.trap.phantom.name");
  NullComponent<Sender> DEATH_HOUND_TRAP_NAME = direct("murder_run.game.trap.death_hound.name");
  NullComponent<Sender> CORRUPTION_TRAP_NAME = direct("murder_run.game.trap.corruption.name");
  NullComponent<Sender> MURDEROUS_WARP_TRAP_NAME = direct("murder_run.game.trap.murderous_warp.name");
  NullComponent<Sender> PORTAL_GUN_TRAP_NAME = direct("murder_run.game.trap.portal_gun.name");
  NullComponent<Sender> THE_REAPER_TRAP_NAME = direct("murder_run.game.trap.the_reaper.name");
  NullComponent<Sender> HEAT_SEEKER_TRAP_NAME = direct("murder_run.game.trap.heat_seeker.name");
  NullComponent<Sender> THE_FLOOR_IS_LAVA_TRAP_NAME = direct("murder_run.game.trap.the_floor_is_lava.name");
  NullComponent<Sender> EMP_BLAST_TRAP_NAME = direct("murder_run.game.trap.emp_blast.name");
  NullComponent<Sender> GAMBLE_TRAP_NAME = direct("murder_run.game.trap.gamble.name");
  NullComponent<Sender> QUICK_BOMB_TRAP_NAME = direct("murder_run.game.trap.quick_bomb.name");
  NullComponent<Sender> HEALTH_CUT_TRAP_NAME = direct("murder_run.game.trap.health_cut.name");
  NullComponent<Sender> POISON_SMOG_TRAP_NAME = direct("murder_run.game.trap.poison_smog.name");
  NullComponent<Sender> PART_WARP_TRAP_NAME = direct("murder_run.game.trap.part_warp.name");
  NullComponent<Sender> HOOK_TRAP_NAME = direct("murder_run.game.trap.hook.name");
  NullComponent<Sender> EAGLE_EYE_NAME = direct("murder_run.game.trap.eagle_eye.name");
  NullComponent<Sender> FAKE_PART_NAME = direct("murder_run.game.trap.fake_part.name");
  NullComponent<Sender> BURN_THE_BODY_NAME = direct("murder_run.game.trap.burn_the_body.name");
  NullComponent<Sender> PUMPKIN_DISEASE_TRAP_NAME = direct("murder_run.game.trap.pumpkin_disease.name");
  NullComponent<Sender> ENDER_SHADOWS_TRAP_NAME = direct("murder_run.game.trap.ender_shadows.name");
  NullComponent<Sender> FOREWARN_TRAP_NAME = direct("murder_run.game.trap.forewarn.name");
  NullComponent<Sender> FIRE_TRAIL_TRAP_NAME = direct("murder_run.game.trap.fire_trail.name");
  NullComponent<Sender> ICE_PATH_TRAP_NAME = direct("murder_run.game.trap.ice_path.name");
  NullComponent<Sender> DORMAGOGG_TRAP_NAME = direct("murder_run.game.trap.dormagogg.name");
  NullComponent<Sender> CURSED_NOTE_TRAP_NAME = direct("murder_run.game.trap.cursed_note.name");
  NullComponent<Sender> RED_ARROW_TRAP_NAME = direct("murder_run.game.trap.red_arrow.name");
  NullComponent<Sender> PLAYER_TRACKER_TRAP_LORE = direct("murder_run.game.trap.player_tracker.lore");
  NullComponent<Sender> KILLER_CAMERA_TRAP_LORE = direct("murder_run.game.trap.killer_camera.lore");
  NullComponent<Sender> WARP_DISTORT_TRAP_LORE = direct("murder_run.game.trap.warp_distort.lore");
  NullComponent<Sender> PAINT_TRAP_TRAP_LORE = direct("murder_run.game.trap.paint_trap.lore");
  NullComponent<Sender> TRAP_WRECKER_TRAP_LORE = direct("murder_run.game.trap.trap_wrecker.lore");
  NullComponent<Sender> TRAP_SEEKER_TRAP_LORE = direct("murder_run.game.trap.trap_seeker.lore");
  NullComponent<Sender> INFRARED_VISION_TRAP_LORE = direct("murder_run.game.trap.infrared_vision.lore");
  NullComponent<Sender> FRIGHT_TRAP_LORE = direct("murder_run.game.trap.fright.lore");
  NullComponent<Sender> BLOOD_CURSE_TRAP_LORE = direct("murder_run.game.trap.blood_curse.lore");
  NullComponent<Sender> DEATH_STEED_TRAP_LORE = direct("murder_run.game.trap.death_steed.lore");
  NullComponent<Sender> ALL_SEEING_EYE_TRAP_LORE = direct("murder_run.game.trap.all_seeing_eye.lore");
  NullComponent<Sender> PHANTOM_TRAP_LORE = direct("murder_run.game.trap.phantom.lore");
  NullComponent<Sender> DEATH_HOUND_TRAP_LORE = direct("murder_run.game.trap.death_hound.lore");
  NullComponent<Sender> CORRUPTION_TRAP_LORE = direct("murder_run.game.trap.corruption.lore");
  NullComponent<Sender> MURDEROUS_WARP_TRAP_LORE = direct("murder_run.game.trap.murderous_warp.lore");
  NullComponent<Sender> PORTAL_GUN_TRAP_LORE = direct("murder_run.game.trap.portal_gun.lore");
  NullComponent<Sender> THE_REAPER_TRAP_LORE = direct("murder_run.game.trap.the_reaper.lore");
  NullComponent<Sender> HEAT_SEEKER_TRAP_LORE = direct("murder_run.game.trap.heat_seeker.lore");
  NullComponent<Sender> THE_FLOOR_IS_LAVA_TRAP_LORE = direct("murder_run.game.trap.the_floor_is_lava.lore");
  NullComponent<Sender> EMP_BLAST_TRAP_LORE = direct("murder_run.game.trap.emp_blast.lore");
  NullComponent<Sender> GAMBLE_TRAP_LORE = direct("murder_run.game.trap.gamble.lore");
  NullComponent<Sender> QUICK_BOMB_TRAP_LORE = direct("murder_run.game.trap.quick_bomb.lore");
  NullComponent<Sender> HEALTH_CUT_TRAP_LORE = direct("murder_run.game.trap.health_cut.lore");
  NullComponent<Sender> POISON_SMOG_TRAP_LORE = direct("murder_run.game.trap.poison_smog.lore");
  NullComponent<Sender> PART_WARP_TRAP_LORE = direct("murder_run.game.trap.part_warp.lore");
  NullComponent<Sender> HOOK_TRAP_LORE = direct("murder_run.game.trap.hook.lore");
  NullComponent<Sender> EAGLE_EYE_TRAP_LORE = direct("murder_run.game.trap.eagle_eye.lore");
  NullComponent<Sender> FAKE_PART_TRAP_LORE = direct("murder_run.game.trap.fake_part.lore");
  NullComponent<Sender> BURN_THE_BODY_TRAP_LORE = direct("murder_run.game.trap.burn_the_body.lore");
  NullComponent<Sender> PUMPKIN_DISEASE_TRAP_LORE = direct("murder_run.game.trap.pumpkin_disease.lore");
  NullComponent<Sender> ENDER_SHADOWS_TRAP_LORE = direct("murder_run.game.trap.ender_shadows.lore");
  NullComponent<Sender> FOREWARN_TRAP_LORE = direct("murder_run.game.trap.forewarn.lore");
  NullComponent<Sender> FIRE_TRAIL_TRAP_LORE = direct("murder_run.game.trap.fire_trail.lore");
  NullComponent<Sender> ICE_PATH_TRAP_LORE = direct("murder_run.game.trap.ice_path.lore");
  NullComponent<Sender> DORMAGOGG_TRAP_LORE = direct("murder_run.game.trap.dormagogg.lore");
  NullComponent<Sender> CURSED_NOTE_TRAP_LORE = direct("murder_run.game.trap.cursed_note.lore");
  NullComponent<Sender> RED_ARROW_TRAP_LORE = direct("murder_run.game.trap.red_arrow.lore");
  NullComponent<Sender> PLAYER_TRACKER_ACTIVATE = direct("murder_run.game.trap.player_tracker.activate");
  NullComponent<Sender> KILLER_CAMERA_ACTIVATE = direct("murder_run.game.trap.killer_camera.activate");
  NullComponent<Sender> WARP_DISTORT_ACTIVATE = direct("murder_run.game.trap.warp_distort.activate");
  NullComponent<Sender> PAINT_TRAP_ACTIVATE = direct("murder_run.game.trap.paint_trap.activate");
  NullComponent<Sender> TRAP_WRECKER_ACTIVATE = direct("murder_run.game.trap.trap_wrecker.activate");
  NullComponent<Sender> TRAP_SEEKER_ACTIVATE = direct("murder_run.game.trap.trap_seeker.activate");
  NullComponent<Sender> INFRARED_VISION_ACTIVATE = direct("murder_run.game.trap.infrared_vision.activate");
  NullComponent<Sender> FRIGHT_ACTIVATE = direct("murder_run.game.trap.fright.activate");
  NullComponent<Sender> BLOOD_CURSE_ACTIVATE = direct("murder_run.game.trap.blood_curse.activate");
  NullComponent<Sender> DEATH_STEED_ACTIVATE = direct("murder_run.game.trap.death_steed.activate");
  NullComponent<Sender> ALL_SEEING_EYE_ACTIVATE = direct("murder_run.game.trap.all_seeing_eye.activate");
  NullComponent<Sender> PHANTOM_ACTIVATE = direct("murder_run.game.trap.phantom.activate");
  NullComponent<Sender> DEATH_HOUND_ACTIVATE = direct("murder_run.game.trap.death_hound.activate");
  NullComponent<Sender> CORRUPTION_ACTIVATE = direct("murder_run.game.trap.corruption.activate");
  NullComponent<Sender> MURDEROUS_WARP_ACTIVATE = direct("murder_run.game.trap.murderous_warp.activate");
  NullComponent<Sender> PORTAL_GUN_ACTIVATE = direct("murder_run.game.trap.portal_gun.activate");
  NullComponent<Sender> THE_REAPER_ACTIVATE = direct("murder_run.game.trap.the_reaper.activate");
  NullComponent<Sender> HEAT_SEEKER_ACTIVATE = direct("murder_run.game.trap.heat_seeker.activate");
  NullComponent<Sender> THE_FLOOR_IS_LAVA_ACTIVATE = direct("murder_run.game.trap.the_floor_is_lava.activate");
  NullComponent<Sender> EMP_BLAST_ACTIVATE = direct("murder_run.game.trap.emp_blast.activate");
  NullComponent<Sender> GAMBLE_ACTIVATE = direct("murder_run.game.trap.gamble.activate");
  NullComponent<Sender> QUICK_BOMB_ACTIVATE = direct("murder_run.game.trap.quick_bomb.activate");
  NullComponent<Sender> HEALTH_CUT_ACTIVATE = direct("murder_run.game.trap.health_cut.activate");
  NullComponent<Sender> POISON_SMOG_ACTIVATE = direct("murder_run.game.trap.poison_smog.activate");
  NullComponent<Sender> PART_WARP_ACTIVATE = direct("murder_run.game.trap.part_warp.activate");
  NullComponent<Sender> HOOK_ACTIVATE = direct("murder_run.game.trap.hook.activate");
  NullComponent<Sender> EAGLE_EYE_ACTIVATE = direct("murder_run.game.trap.eagle_eye.activate");
  NullComponent<Sender> FAKE_PART_ACTIVATE = direct("murder_run.game.trap.fake_part.activate");
  NullComponent<Sender> BURN_THE_BODY_ACTIVATE = direct("murder_run.game.trap.burn_the_body.activate");
  NullComponent<Sender> PUMPKIN_DISEASE_ACTIVATE = direct("murder_run.game.trap.pumpkin_disease.activate");
  NullComponent<Sender> ENDER_SHADOWS_ACTIVATE = direct("murder_run.game.trap.ender_shadows.activate");
  NullComponent<Sender> FOREWARN_ACTIVATE = direct("murder_run.game.trap.forewarn.activate");
  NullComponent<Sender> FIRE_TRAIL_ACTIVATE = direct("murder_run.game.trap.fire_trail.activate");
  NullComponent<Sender> ICE_PATH_ACTIVATE = direct("murder_run.game.trap.ice_path.activate");
  NullComponent<Sender> DORMAGOGG_ACTIVATE = direct("murder_run.game.trap.dormagogg.activate");
  NullComponent<Sender> CURSED_NOTE_ACTIVATE = direct("murder_run.game.trap.cursed_note.activate");
  NullComponent<Sender> RED_ARROW_ACTIVATE = direct("murder_run.game.trap.red_arrow.activate");
}
