package io.github.pulsebeat02.murderrun.locale;

import static io.github.pulsebeat02.murderrun.locale.LocaleTools.direct;

import java.util.List;

public interface Message extends LocaleTools {
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
  NullComponent<Sender> HACK_NAME = direct("murder_run.game.gadget.hack.name");
  NullComponent<Sender> GLOW_NAME = direct("murder_run.game.gadget.glow.name");
  NullComponent<Sender> BEAR_NAME = direct("murder_run.game.gadget.bear.name");
  NullComponent<Sender> PORTAL_NAME = direct("murder_run.game.gadget.portal.name");
  NullComponent<Sender> SPASM_NAME = direct("murder_run.game.gadget.spasm.name");
  NullComponent<Sender> REWIND_NAME = direct("murder_run.game.gadget.rewind.name");
  NullComponent<Sender> MURDERER_REWIND_NAME =
      direct("murder_run.game.gadget.murderer_rewind.name");
  NullComponent<Sender> RESURRECTION_STONE_NAME =
      direct("murder_run.game.gadget.resurrection_stone.name");
  NullComponent<Sender> CORPUS_WARP_NAME = direct("murder_run.game.gadget.corpus_warp.name");
  NullComponent<Sender> GHOSTING_NAME = direct("murder_run.game.gadget.ghosting.name");
  NullComponent<Sender> EXCAVATOR_NAME = direct("murder_run.game.gadget.excavator.name");
  NullComponent<Sender> DISTORT_NAME = direct("murder_run.game.gadget.distort.name");
  NullComponent<Sender> HORCRUX_NAME = direct("murder_run.game.gadget.horcrux.name");
  NullComponent<Sender> MED_BOT_NAME = direct("murder_run.game.gadget.med_bot.name");
  NullComponent<Sender> RETALIATION_NAME = direct("murder_run.game.gadget.retaliation.name");
  NullComponent<Sender> SUPPLY_DROP_NAME = direct("murder_run.game.gadget.supply_drop.name");
  NullComponent<Sender> MED_KIT_NAME = direct("murder_run.game.gadget.med_kit.name");
  NullComponent<Sender> JUMP_SCARE_NAME = direct("murder_run.game.gadget.jump_scare.name");
  NullComponent<Sender> SMOKE_NAME = direct("murder_run.game.gadget.smoke.name");
  NullComponent<Sender> LEVITATION_NAME = direct("murder_run.game.gadget.levitation.name");
  NullComponent<Sender> CAGE_NAME = direct("murder_run.game.gadget.cage.name");
  NullComponent<Sender> BLIND_NAME = direct("murder_run.game.gadget.blind.name");
  NullComponent<Sender> HAUNT_NAME = direct("murder_run.game.gadget.haunt.name");
  NullComponent<Sender> NECK_SNAP_NAME = direct("murder_run.game.gadget.neck_snap.name");
  NullComponent<Sender> DEADRINGER_NAME = direct("murder_run.game.gadget.deadringer.name");
  NullComponent<Sender> STAR_NAME = direct("murder_run.game.gadget.star.name");
  NullComponent<Sender> SPAWN_NAME = direct("murder_run.game.gadget.spawn.name");
  NullComponent<Sender> FREEZE_NAME = direct("murder_run.game.gadget.freeze.name");
  NullComponent<Sender> BURROW_NAME = direct("murder_run.game.gadget.burrow.name");
  NullComponent<Sender> GHOST_NAME = direct("murder_run.game.gadget.ghost.name");
  NullComponent<Sender> PONY_NAME = direct("murder_run.game.gadget.pony.name");
  NullComponent<Sender> FIREWORK_NAME = direct("murder_run.game.gadget.firework.name");
  NullComponent<Sender> FART_NAME = direct("murder_run.game.gadget.fart.name");
  NullComponent<Sender> TRAP_VEST_NAME = direct("murder_run.game.gadget.trap_vest.name");
  NullComponent<Sender> RANDOM_NAME = direct("murder_run.game.gadget.random.name");
  NullComponent<Sender> MAGNET_MODE_NAME = direct("murder_run.game.gadget.magnet_mode.name");
  NullComponent<Sender> TRANSLOCATOR_NAME = direct("murder_run.game.gadget.translocator.name");
  NullComponent<Sender> TRACKER_NAME = direct("murder_run.game.gadget.tracker.name");
  NullComponent<Sender> DECOY_NAME = direct("murder_run.game.gadget.decoy.name");
  NullComponent<Sender> SMOKE_BOMB_NAME = direct("murder_run.game.gadget.smoke_bomb.name");
  NullComponent<Sender> FRIEND_WARP_NAME = direct("murder_run.game.gadget.friend_warp.name");
  NullComponent<Sender> FLASHBANG_NAME = direct("murder_run.game.gadget.flashbang.name");
  NullComponent<Sender> CAMERA_NAME = direct("murder_run.game.gadget.camera.name");
  NullComponent<Sender> CLOAK_NAME = direct("murder_run.game.gadget.cloak.name");
  NullComponent<Sender> SHIELD_NAME = direct("murder_run.game.gadget.shield.name");
  NullComponent<Sender> TP_ME_AWAY_FROM_HERE_NAME =
      direct("murder_run.game.gadget.tp_me_away_from_here.name");
  NullComponent<Sender> SIXTH_SENSE_NAME = direct("murder_run.game.gadget.sixth_sense.name");
  NullComponent<Sender> BLAST_OFF_NAME = direct("murder_run.game.gadget.blast_off.name");
  NullComponent<Sender> DRONE_NAME = direct("murder_run.game.gadget.drone.name");
  NullComponent<Sender> TRAP_SNIFFER_NAME = direct("murder_run.game.gadget.trap_sniffer.name");
  NullComponent<Sender> CHIPPED_NAME = direct("murder_run.game.gadget.chipped.name");
  NullComponent<Sender> LIFE_INSURANCE_NAME = direct("murder_run.game.gadget.life_insurance.name");
  NullComponent<Sender> CRYO_FREEZE_NAME = direct("murder_run.game.gadget.cryo_freeze.name");
  NullComponent<Sender> ICE_SKATIN_NAME = direct("murder_run.game.gadget.ice_skatin.name");
  NullComponent<Sender> ICE_SPIRIT_NAME = direct("murder_run.game.gadget.ice_spirit.name");
  NullComponent<Sender> MIND_CONTROL_NAME = direct("murder_run.game.gadget.mind_control.name");
  NullComponent<Sender> JEB_NAME = direct("murder_run.game.gadget.jeb.name");
  NullComponent<Sender> BUSH_NAME = direct("murder_run.game.gadget.bush.name");
  NullComponent<Sender> KILLER_TRACKER_NAME = direct("murder_run.game.gadget.killer_tracker.name");
  NullComponent<Sender> FLASHLIGHT_NAME = direct("murder_run.game.gadget.flashlight.name");
  NullComponent<Sender> SHOCKWAVE_NAME = direct("murder_run.game.gadget.shockwave.name");
  NullComponent<Sender> PARASITE_NAME = direct("murder_run.game.gadget.parasite.name");
  NullComponent<Sender> DISTORTER_NAME = direct("murder_run.game.gadget.distorter.name");
  NullComponent<Sender> SPEED_PENDANT_NAME = direct("murder_run.game.gadget.speed_pendant.name");
  NullComponent<Sender> GLOW_LORE = direct("murder_run.game.gadget.glow.lore");
  NullComponent<Sender> HACK_LORE = direct("murder_run.game.gadget.hack.lore");
  NullComponent<Sender> BEAR_LORE = direct("murder_run.game.gadget.bear.lore");
  NullComponent<Sender> PORTAL_LORE = direct("murder_run.game.gadget.portal.lore");
  NullComponent<Sender> SPASM_LORE = direct("murder_run.game.gadget.spasm.lore");
  NullComponent<Sender> REWIND_LORE = direct("murder_run.game.gadget.rewind.lore");
  NullComponent<Sender> MURDERER_REWIND_LORE =
      direct("murder_run.game.gadget.murderer_rewind.lore");
  NullComponent<Sender> RESURRECTION_STONE_LORE =
      direct("murder_run.game.gadget.resurrection_stone.lore");
  NullComponent<Sender> CORPUS_WARP_LORE = direct("murder_run.game.gadget.corpus_warp.lore");
  NullComponent<Sender> GHOSTING_LORE = direct("murder_run.game.gadget.ghosting.lore");
  NullComponent<Sender> EXCAVATOR_LORE = direct("murder_run.game.gadget.excavator.lore");
  NullComponent<Sender> DISTORT_LORE = direct("murder_run.game.gadget.distort.lore");
  NullComponent<Sender> HORCRUX_LORE = direct("murder_run.game.gadget.horcrux.lore");
  NullComponent<Sender> MED_BOT_LORE = direct("murder_run.game.gadget.med_bot.lore");
  NullComponent<Sender> RETALIATION_LORE = direct("murder_run.game.gadget.retaliation.lore");
  NullComponent<Sender> SUPPLY_DROP_LORE = direct("murder_run.game.gadget.supply_drop.lore");
  NullComponent<Sender> MED_KIT_LORE = direct("murder_run.game.gadget.med_kit.lore");
  NullComponent<Sender> JUMP_SCARE_LORE = direct("murder_run.game.gadget.jump_scare.lore");
  NullComponent<Sender> SMOKE_LORE = direct("murder_run.game.gadget.smoke.lore");
  NullComponent<Sender> LEVITATION_LORE = direct("murder_run.game.gadget.levitation.lore");
  NullComponent<Sender> CAGE_LORE = direct("murder_run.game.gadget.cage.lore");
  NullComponent<Sender> BLIND_LORE = direct("murder_run.game.gadget.blind.lore");
  NullComponent<Sender> HAUNT_LORE = direct("murder_run.game.gadget.haunt.lore");
  NullComponent<Sender> NECK_SNAP_LORE = direct("murder_run.game.gadget.neck_snap.lore");
  NullComponent<Sender> DEADRINGER_LORE = direct("murder_run.game.gadget.deadringer.lore");
  NullComponent<Sender> STAR_LORE = direct("murder_run.game.gadget.star.lore");
  NullComponent<Sender> SPAWN_LORE = direct("murder_run.game.gadget.spawn.lore");
  NullComponent<Sender> FREEZE_LORE = direct("murder_run.game.gadget.freeze.lore");
  NullComponent<Sender> BURROW_LORE = direct("murder_run.game.gadget.burrow.lore");
  NullComponent<Sender> GHOST_LORE = direct("murder_run.game.gadget.ghost.lore");
  NullComponent<Sender> PONY_LORE = direct("murder_run.game.gadget.pony.lore");
  NullComponent<Sender> FIREWORK_LORE = direct("murder_run.game.gadget.firework.lore");
  NullComponent<Sender> FART_LORE = direct("murder_run.game.gadget.fart.lore");
  NullComponent<Sender> TRAP_VEST_LORE = direct("murder_run.game.gadget.trap_vest.lore");
  NullComponent<Sender> RANDOM_LORE = direct("murder_run.game.gadget.random.lore");
  NullComponent<Sender> MAGNET_MODE_LORE = direct("murder_run.game.gadget.magnet_mode.lore");
  NullComponent<Sender> TRANSLOCATOR_LORE = direct("murder_run.game.gadget.translocator.lore");
  NullComponent<Sender> TRANSLOCATOR_LORE1 = direct("murder_run.game.gadget.translocator.lore1");
  NullComponent<Sender> TRACKER_LORE = direct("murder_run.game.gadget.tracker.lore");
  NullComponent<Sender> DECOY_LORE = direct("murder_run.game.gadget.decoy.lore");
  NullComponent<Sender> SMOKE_BOMB_LORE = direct("murder_run.game.gadget.smoke_bomb.lore");
  NullComponent<Sender> FRIEND_WARP_LORE = direct("murder_run.game.gadget.friend_warp.lore");
  NullComponent<Sender> FLASHBANG_LORE = direct("murder_run.game.gadget.flashbang.lore");
  NullComponent<Sender> CAMERA_LORE = direct("murder_run.game.gadget.camera.lore");
  NullComponent<Sender> CLOAK_LORE = direct("murder_run.game.gadget.cloak.lore");
  NullComponent<Sender> SHIELD_LORE = direct("murder_run.game.gadget.shield.lore");
  NullComponent<Sender> TP_ME_AWAY_FROM_HERE_LORE =
      direct("murder_run.game.gadget.tp_me_away_from_here.lore");
  NullComponent<Sender> SIXTH_SENSE_LORE = direct("murder_run.game.gadget.sixth_sense.lore");
  NullComponent<Sender> BLAST_OFF_LORE = direct("murder_run.game.gadget.blast_off.lore");
  NullComponent<Sender> DRONE_LORE = direct("murder_run.game.gadget.drone.lore");
  NullComponent<Sender> TRAP_SNIFFER_LORE = direct("murder_run.game.gadget.trap_sniffer.lore");
  NullComponent<Sender> CHIPPED_LORE = direct("murder_run.game.gadget.chipped.lore");
  NullComponent<Sender> LIFE_INSURANCE_LORE = direct("murder_run.game.gadget.life_insurance.lore");
  NullComponent<Sender> CRYO_FREEZE_LORE = direct("murder_run.game.gadget.cryo_freeze.lore");
  NullComponent<Sender> ICE_SKATIN_LORE = direct("murder_run.game.gadget.ice_skatin.lore");
  NullComponent<Sender> ICE_SPIRIT_LORE = direct("murder_run.game.gadget.ice_spirit.lore");
  NullComponent<Sender> MIND_CONTROL_LORE = direct("murder_run.game.gadget.mind_control.lore");
  NullComponent<Sender> JEB_LORE = direct("murder_run.game.gadget.jeb.lore");
  NullComponent<Sender> BUSH_LORE = direct("murder_run.game.gadget.bush.lore");
  NullComponent<Sender> KILLER_TRACKER_LORE = direct("murder_run.game.gadget.killer_tracker.lore");
  NullComponent<Sender> FLASHLIGHT_LORE = direct("murder_run.game.gadget.flashlight.lore");
  NullComponent<Sender> SHOCKWAVE_LORE = direct("murder_run.game.gadget.shockwave.lore");
  NullComponent<Sender> PARASITE_LORE = direct("murder_run.game.gadget.parasite.lore");
  NullComponent<Sender> DISTORTER_LORE = direct("murder_run.game.gadget.distorter.lore");
  NullComponent<Sender> GLOW_ACTIVATE = direct("murder_run.game.gadget.glow.activate");
  NullComponent<Sender> HACK_ACTIVATE = direct("murder_run.game.gadget.hack.activate");
  NullComponent<Sender> BEAR_ACTIVATE = direct("murder_run.game.gadget.bear.activate");
  NullComponent<Sender> SPASM_ACTIVATE = direct("murder_run.game.gadget.spasm.activate");
  NullComponent<Sender> RESURRECTION_STONE_ACTIVATE =
      direct("murder_run.game.gadget.resurrection_stone.activate");
  NullComponent<Sender> GHOSTING_ACTIVATE = direct("murder_run.game.gadget.ghosting.activate");
  NullComponent<Sender> DISTORT_ACTIVATE = direct("murder_run.game.gadget.distort.activate");
  NullComponent<Sender> HORCRUX_ACTIVATE = direct("murder_run.game.gadget.horcrux.activate");
  NullComponent<Sender> RETALIATION_ACTIVATE =
      direct("murder_run.game.gadget.retaliation.activate");
  NullComponent<Sender> JUMP_SCARE_ACTIVATE = direct("murder_run.game.gadget.jump_scare.activate");
  NullComponent<Sender> SMOKE_ACTIVATE = direct("murder_run.game.gadget.smoke.activate");
  NullComponent<Sender> LEVITATION_ACTIVATE = direct("murder_run.game.gadget.levitation.activate");
  NullComponent<Sender> CAGE_ACTIVATE = direct("murder_run.game.gadget.cage.activate");
  NullComponent<Sender> BLIND_ACTIVATE = direct("murder_run.game.gadget.blind.activate");
  NullComponent<Sender> HAUNT_ACTIVATE = direct("murder_run.game.gadget.haunt.activate");
  NullComponent<Sender> NECK_SNAP_ACTIVATE = direct("murder_run.game.gadget.neck_snap.activate");
  NullComponent<Sender> STAR_ACTIVATE = direct("murder_run.game.gadget.star.activate");
  NullComponent<Sender> SPAWN_ACTIVATE = direct("murder_run.game.gadget.spawn.activate");
  NullComponent<Sender> FREEZE_ACTIVATE = direct("murder_run.game.gadget.freeze.activate");
  NullComponent<Sender> BURROW_ACTIVATE = direct("murder_run.game.gadget.burrow.activate");
  NullComponent<Sender> GHOST_ACTIVATE = direct("murder_run.game.gadget.ghost.activate");
  NullComponent<Sender> FIREWORK_ACTIVATE = direct("murder_run.game.gadget.firework.activate");
  NullComponent<Sender> FART_ACTIVATE = direct("murder_run.game.gadget.fart.activate");
  NullComponent<Sender> TRAP_VEST_ACTIVATE = direct("murder_run.game.gadget.trap_vest.activate");
  NullComponent<Sender> MAGNET_MODE_ACTIVATE =
      direct("murder_run.game.gadget.magnet_mode.activate");
  NullComponent<Sender> CLOAK_ACTIVATE = direct("murder_run.game.gadget.cloak.activate");
  NullComponent<Sender> SIXTH_SENSE_ACTIVATE =
      direct("murder_run.game.gadget.sixth_sense.activate");
  NullComponent<Sender> BLAST_OFF_ACTIVATE = direct("murder_run.game.gadget.blast_off.activate");
  NullComponent<Sender> TRAP_SNIFFER_ACTIVATE =
      direct("murder_run.game.gadget.trap_sniffer.activate");
  NullComponent<Sender> JEB_ACTIVATE = direct("murder_run.game.gadget.jeb.activate");
  NullComponent<Sender> SHOCKWAVE_ACTIVATE = direct("murder_run.game.gadget.shockwave.activate");
  NullComponent<Sender> MED_BOT_DEACTIVATE = direct("murder_run.game.gadget.med_bot.deactivate");
  NullComponent<Sender> PARASITE_DEACTIVATE = direct("murder_run.game.gadget.parasite.deactivate");
  NullComponent<Sender> DISTORTER_DEACTIVATE =
      direct("murder_run.game.gadget.distorter.deactivate");
  NullComponent<Sender> SURVIVOR_HELMET = direct("murder_run.game.gadget.helmet");
  NullComponent<Sender> SURVIVOR_CHESTPLATE = direct("murder_run.game.gadget.chestplate");
  NullComponent<Sender> SURVIVOR_LEGGINGS = direct("murder_run.game.gadget.leggings");
  NullComponent<Sender> SURVIVOR_BOOTS = direct("murder_run.game.gadget.boots");
  NullComponent<Sender> PONY_ACTIVATE = direct("murder_run.game.gadget.pony.activate");
  UniComponent<Sender, String> ARENA_REMOVE = direct("murder_run.command.arena.remove", null);
  UniComponent<Sender, String> LOBBY_REMOVE = direct("murder_run.command.lobby.remove", null);
  NullComponent<Sender> ARENA_REMOVE_ERROR = direct("murder_run.command.arena.remove_error");
  NullComponent<Sender> LOBBY_REMOVE_ERROR = direct("murder_run.command.lobby.remove_error");
  NullComponent<Sender> LIFE_INSURANCE_ACTIVATE =
      direct("murder_run.game.gadget.life_insurance.activate");
  NullComponent<Sender> TRACKER_ACTIVATE = direct("murder_run.game.gadget.tracker.activate");
  NullComponent<Sender> TRACKER_DEACTIVATE = direct("murder_run.game.gadget.tracker.deactivate");
  UniComponent<Sender, Double> KILLER_TRACKER_ACTIVATE =
      direct("murder_run.game.gadget.killer_tracker.activate", null);
  NullComponent<Sender> PLAYER_TRACKER_NAME = direct("murder_run.game.gadget.player_tracker.name");
  NullComponent<Sender> KILLER_CAMERA_NAME = direct("murder_run.game.gadget.camera_killer.name");
  NullComponent<Sender> WARP_DISTORT_NAME = direct("murder_run.game.gadget.warp_distort.name");
  NullComponent<Sender> TRAP_WRECKER_NAME = direct("murder_run.game.gadget.trap_wrecker.name");
  NullComponent<Sender> TRAP_SEEKER_NAME = direct("murder_run.game.gadget.trap_seeker.name");
  NullComponent<Sender> INFRARED_VISION_NAME =
      direct("murder_run.game.gadget.infrared_vision.name");
  NullComponent<Sender> FRIGHT_NAME = direct("murder_run.game.gadget.fright.name");
  NullComponent<Sender> BLOOD_CURSE_NAME = direct("murder_run.game.gadget.blood_curse.name");
  NullComponent<Sender> DEATH_STEED_NAME = direct("murder_run.game.gadget.death_steed.name");
  NullComponent<Sender> ALL_SEEING_EYE_NAME = direct("murder_run.game.gadget.all_seeing_eye.name");
  NullComponent<Sender> PHANTOM_NAME = direct("murder_run.game.gadget.phantom.name");
  NullComponent<Sender> DEATH_HOUND_NAME = direct("murder_run.game.gadget.death_hound.name");
  NullComponent<Sender> CORRUPTION_NAME = direct("murder_run.game.gadget.corruption.name");
  NullComponent<Sender> MURDEROUS_WARP_NAME = direct("murder_run.game.gadget.murderous_warp.name");
  NullComponent<Sender> PORTAL_GUN_NAME = direct("murder_run.game.gadget.portal_gun.name");
  NullComponent<Sender> HEAT_SEEKER_NAME = direct("murder_run.game.gadget.heat_seeker.name");
  NullComponent<Sender> THE_FLOOR_IS_LAVA_NAME =
      direct("murder_run.game.gadget.the_floor_is_lava.name");
  NullComponent<Sender> EMP_BLAST_NAME = direct("murder_run.game.gadget.emp_blast.name");
  NullComponent<Sender> GAMBLE_NAME = direct("murder_run.game.gadget.gamble.name");
  NullComponent<Sender> QUICK_BOMB_NAME = direct("murder_run.game.gadget.quick_bomb.name");
  NullComponent<Sender> HEALTH_CUT_NAME = direct("murder_run.game.gadget.health_cut.name");
  NullComponent<Sender> POISON_SMOG_NAME = direct("murder_run.game.gadget.poison_smog.name");
  NullComponent<Sender> PART_WARP_NAME = direct("murder_run.game.gadget.part_warp.name");
  NullComponent<Sender> HOOK_NAME = direct("murder_run.game.gadget.hook.name");
  NullComponent<Sender> EAGLE_EYE_NAME = direct("murder_run.game.gadget.eagle_eye.name");
  NullComponent<Sender> FAKE_PART_NAME = direct("murder_run.game.gadget.fake_part.name");
  NullComponent<Sender> BURN_THE_BODY_NAME = direct("murder_run.game.gadget.burn_the_body.name");
  NullComponent<Sender> ENDER_SHADOWS_NAME = direct("murder_run.game.gadget.ender_shadows.name");
  NullComponent<Sender> FOREWARN_NAME = direct("murder_run.game.gadget.forewarn.name");
  NullComponent<Sender> FIRE_TRAIL_NAME = direct("murder_run.game.gadget.fire_trail.name");
  NullComponent<Sender> ICE_PATH_NAME = direct("murder_run.game.gadget.ice_path.name");
  NullComponent<Sender> DORMAGOGG_NAME = direct("murder_run.game.gadget.dormagogg.name");
  NullComponent<Sender> CURSED_NOTE_NAME = direct("murder_run.game.gadget.cursed_note.name");
  NullComponent<Sender> RED_ARROW_NAME = direct("murder_run.game.gadget.red_arrow.name");
  NullComponent<Sender> PLAYER_TRACKER_LORE = direct("murder_run.game.gadget.player_tracker.lore");
  NullComponent<Sender> KILLER_CAMERA_LORE = direct("murder_run.game.gadget.killer_camera.lore");
  NullComponent<Sender> WARP_DISTORT_LORE = direct("murder_run.game.gadget.warp_distort.lore");
  NullComponent<Sender> TRAP_WRECKER_LORE = direct("murder_run.game.gadget.trap_wrecker.lore");
  NullComponent<Sender> TRAP_SEEKER_LORE = direct("murder_run.game.gadget.trap_seeker.lore");
  NullComponent<Sender> INFRARED_VISION_LORE =
      direct("murder_run.game.gadget.infrared_vision.lore");
  NullComponent<Sender> FRIGHT_LORE = direct("murder_run.game.gadget.fright.lore");
  NullComponent<Sender> BLOOD_CURSE_LORE = direct("murder_run.game.gadget.blood_curse.lore");
  NullComponent<Sender> DEATH_STEED_LORE = direct("murder_run.game.gadget.death_steed.lore");
  NullComponent<Sender> ALL_SEEING_EYE_LORE = direct("murder_run.game.gadget.all_seeing_eye.lore");
  NullComponent<Sender> PHANTOM_LORE = direct("murder_run.game.gadget.phantom.lore");
  NullComponent<Sender> DEATH_HOUND_LORE = direct("murder_run.game.gadget.death_hound.lore");
  NullComponent<Sender> CORRUPTION_LORE = direct("murder_run.game.gadget.corruption.lore");
  NullComponent<Sender> MURDEROUS_WARP_LORE = direct("murder_run.game.gadget.murderous_warp.lore");
  NullComponent<Sender> PORTAL_GUN_LORE = direct("murder_run.game.gadget.portal_gun.lore");
  NullComponent<Sender> HEAT_SEEKER_LORE = direct("murder_run.game.gadget.heat_seeker.lore");
  NullComponent<Sender> THE_FLOOR_IS_LAVA_LORE =
      direct("murder_run.game.gadget.the_floor_is_lava.lore");
  NullComponent<Sender> EMP_BLAST_LORE = direct("murder_run.game.gadget.emp_blast.lore");
  NullComponent<Sender> GAMBLE_LORE = direct("murder_run.game.gadget.gamble.lore");
  NullComponent<Sender> QUICK_BOMB_LORE = direct("murder_run.game.gadget.quick_bomb.lore");
  NullComponent<Sender> HEALTH_CUT_LORE = direct("murder_run.game.gadget.health_cut.lore");
  NullComponent<Sender> POISON_SMOG_LORE = direct("murder_run.game.gadget.poison_smog.lore");
  NullComponent<Sender> PART_WARP_LORE = direct("murder_run.game.gadget.part_warp.lore");
  NullComponent<Sender> HOOK_LORE = direct("murder_run.game.gadget.hook.lore");
  NullComponent<Sender> EAGLE_EYE_LORE = direct("murder_run.game.gadget.eagle_eye.lore");
  NullComponent<Sender> FAKE_PART_LORE = direct("murder_run.game.gadget.fake_part.lore");
  NullComponent<Sender> BURN_THE_BODY_LORE = direct("murder_run.game.gadget.burn_the_body.lore");
  NullComponent<Sender> ENDER_SHADOWS_LORE = direct("murder_run.game.gadget.ender_shadows.lore");
  NullComponent<Sender> FOREWARN_LORE = direct("murder_run.game.gadget.forewarn.lore");
  NullComponent<Sender> FIRE_TRAIL_LORE = direct("murder_run.game.gadget.fire_trail.lore");
  NullComponent<Sender> ICE_PATH_LORE = direct("murder_run.game.gadget.ice_path.lore");
  NullComponent<Sender> DORMAGOGG_LORE = direct("murder_run.game.gadget.dormagogg.lore");
  NullComponent<Sender> CURSED_NOTE_LORE = direct("murder_run.game.gadget.cursed_note.lore");
  NullComponent<Sender> RED_ARROW_LORE = direct("murder_run.game.gadget.red_arrow.lore");
  UniComponent<Sender, Double> PLAYER_TRACKER_ACTIVATE =
      direct("murder_run.game.gadget.player_tracker.activate", null);
  NullComponent<Sender> WARP_DISTORT_ACTIVATE =
      direct("murder_run.game.gadget.warp_distort.activate");
  NullComponent<Sender> TRAP_WRECKER_ACTIVATE =
      direct("murder_run.game.gadget.trap_wrecker.activate");
  NullComponent<Sender> TRAP_SEEKER_ACTIVATE =
      direct("murder_run.game.gadget.trap_seeker.activate");
  NullComponent<Sender> INFRARED_VISION_ACTIVATE =
      direct("murder_run.game.gadget.infrared_vision.activate");
  NullComponent<Sender> BLOOD_CURSE_ACTIVATE =
      direct("murder_run.game.gadget.blood_curse.activate");
  NullComponent<Sender> CORRUPTION_ACTIVATE = direct("murder_run.game.gadget.corruption.activate");
  NullComponent<Sender> HEAT_SEEKER_ACTIVATE =
      direct("murder_run.game.gadget.heat_seeker.activate");
  NullComponent<Sender> THE_FLOOR_IS_LAVA_ACTIVATE =
      direct("murder_run.game.gadget.the_floor_is_lava.activate");
  NullComponent<Sender> EMP_BLAST_ACTIVATE = direct("murder_run.game.gadget.emp_blast.activate");
  NullComponent<Sender> GAMBLE_ACTIVATE = direct("murder_run.game.gadget.gamble.activate");
  NullComponent<Sender> HEALTH_CUT_ACTIVATE = direct("murder_run.game.gadget.health_cut.activate");
  NullComponent<Sender> FAKE_PART_ACTIVATE = direct("murder_run.game.gadget.fake_part.activate");
  NullComponent<Sender> ENDER_SHADOWS_ACTIVATE =
      direct("murder_run.game.gadget.ender_shadows.activate");
  NullComponent<Sender> FOREWARN_ACTIVATE = direct("murder_run.game.gadget.forewarn.activate");
  NullComponent<Sender> CURSED_NOTE_ACTIVATE =
      direct("murder_run.game.gadget.cursed_note.activate");
  NullComponent<Sender> RED_ARROW_ACTIVATE = direct("murder_run.game.gadget.red_arrow.activate");
  NullComponent<Sender> ENDER_SHADOWS_EFFECT =
      direct("murder_run.game.gadget.ender_shadows.effect");
  NullComponent<Sender> CURSED_NOTE_DROP = direct("murder_run.game.gadget.cursed_note.drop");
  NullComponent<Sender> ARROW_NAME = direct("murder_run.game.gadget.arrow.name");
  NullComponent<Sender> ARROW_LORE = direct("murder_run.game.gadget.arrow.lore");
  NullComponent<Sender> KILLER_SWORD = direct("murder_run.game.sword.name");
  NullComponent<Sender> MINEBUCKS = direct("murder_run.game.currency.name");
  NullComponent<Sender> GADGET_RETRIEVE_ERROR = direct("murder_run.command.gadget.retrieve.error");
  NullComponent<Sender> GAME_INVITE_ALREADY_ERROR =
      direct("murder_run.command.game.invite_already_error");
  NullComponent<Sender> GAME_STARTED_ERROR = direct("murder_run.command.game.start_error");
}
