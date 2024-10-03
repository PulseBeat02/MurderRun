package io.github.pulsebeat02.murderrun.locale;

import static io.github.pulsebeat02.murderrun.locale.LocaleTools.direct;

import java.util.List;

public interface Message extends LocaleTools {
  NullComponent<Sender> PLUGIN_ENABLE = direct("murderrun.plugin.enable");
  NullComponent<Sender> PLUGIN_DEPENDENCY_ERROR = direct("murderrun.plugin.dependency");
  NullComponent<Sender> PLUGIN_DISABLE = direct("murderrun.plugin.disable");
  NullComponent<Sender> CAR_PART_ITEM_NAME = direct("murderrun.item.car_part.name");
  NullComponent<Sender> CAR_PART_ITEM_LORE = direct("murderrun.item.car_part.lore");
  UniComponent<Sender, Integer> CAR_PART_ITEM_RETRIEVAL =
      direct("murderrun.game.item.car_part.retrieval", null);
  NullComponent<Sender> PREPARATION_PHASE = direct("murderrun.game.survivor_preparation");
  NullComponent<Sender> RELEASE_PHASE = direct("murderrun.game.murderer_released");
  NullComponent<Sender> INNOCENT_VICTORY_INNOCENT =
      direct("murderrun.game.innocent_victory_innocent");
  NullComponent<Sender> INNOCENT_VICTORY_MURDERER =
      direct("murderrun.game.innocent_victory_murderer");
  NullComponent<Sender> MURDERER_VICTORY_INNOCENT =
      direct("murderrun.game.murderer_victory_innocent");
  NullComponent<Sender> MURDERER_VICTORY_MURDERER =
      direct("murderrun.game.murderer_victory_murderer");
  UniComponent<Sender, Long> FINAL_TIME = direct("murderrun.game.time", null);
  NullComponent<Sender> BOSS_BAR = direct("murderrun.game.boss_bar");
  UniComponent<Sender, String> PLAYER_DEATH = direct("murderrun.game.death", null);
  NullComponent<Sender> RESOURCEPACK_PROMPT = direct("murderrun.resourcepack");
  TriComponent<Sender, Integer, Integer, Integer> ARENA_FIRST_CORNER =
      direct("murderrun.command.arena.set.first-corner", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_SECOND_CORNER =
      direct("murderrun.command.arena.set.second-corner", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_SPAWN =
      direct("murderrun.command.arena.set.spawn", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_TRUCK =
      direct("murderrun.command.arena.set.truck", null, null, null);
  UniComponent<Sender, String> ARENA_NAME = direct("murderrun.command.arena.set.name", null);
  NullComponent<Sender> ARENA_CORNER_ERROR = direct("murderrun.command.arena.set.corner_error");
  NullComponent<Sender> ARENA_SPAWN_ERROR = direct("murderrun.command.arena.set.spawn_error");
  NullComponent<Sender> ARENA_NAME_ERROR = direct("murderrun.command.arena.set.name_error");
  NullComponent<Sender> ARENA_TRUCK_ERROR = direct("murderrun.command.arena.set.truck_error");
  NullComponent<Sender> ARENA_BUILT = direct("murderrun.command.arena.create");
  UniComponent<Sender, List<String>> ARENA_LIST = direct("murderrun.command.arena.list", null);
  UniComponent<Sender, String> LOBBY_NAME = direct("murderrun.command.lobby.set.name", null);
  TriComponent<Sender, Integer, Integer, Integer> LOBBY_SPAWN =
      direct("murderrun.command.lobby.set.spawn", null, null, null);
  NullComponent<Sender> LOBBY_NAME_ERROR = direct("murderrun.command.lobby.set.name_error");
  NullComponent<Sender> LOBBY_SPAWN_ERROR = direct("murderrun.command.lobby.set.spawn_error");
  NullComponent<Sender> LOBBY_BUILT = direct("murderrun.command.lobby.create");
  UniComponent<Sender, List<String>> LOBBY_LIST = direct("murderrun.command.lobby.list", null);
  NullComponent<Sender> GAME_LEFT = direct("murderrun.command.game.leave");
  NullComponent<Sender> GAME_CREATED = direct("murderrun.command.game.create");
  UniComponent<Sender, String> GAME_OWNER_INVITE =
      direct("murderrun.command.game.owner_invite", null);
  UniComponent<Sender, String> GAME_PLAYER_INVITE =
      direct("murderrun.command.game.player_invite", null);
  NullComponent<Sender> GAME_CANCEL = direct("murderrun.command.game.cancel");
  UniComponent<Sender, String> GAME_SET_MURDERER =
      direct("murderrun.command.game.set.killer", null);
  UniComponent<Sender, String> GAME_SET_INNOCENT =
      direct("murderrun.command.game.set.survivor", null);
  UniComponent<Sender, Integer> GAME_SET_CAR_PART_COUNT =
      direct("murderrun.command.game.set.car_part_count", null);
  UniComponent<Sender, String> GAME_OWNER_KICK = direct("murderrun.command.game.owner_kick", null);
  NullComponent<Sender> GAME_PLAYER_KICK = direct("murderrun.command.game.player_kick");
  UniComponent<Sender, List<String>> GAME_LIST = direct("murderrun.command.game.list", null);
  UniComponent<Sender, String> GAME_JOIN = direct("murderrun.command.game.join", null);
  NullComponent<Sender> GAME_START = direct("murderrun.command.game.start");
  NullComponent<Sender> GAME_ARENA_ERROR = direct("murderrun.command.game.arena_error");
  NullComponent<Sender> GAME_LOBBY_ERROR = direct("murderrun.command.game.lobby_error");
  NullComponent<Sender> GAME_LEAVE_ERROR = direct("murderrun.command.game.leave_error");
  NullComponent<Sender> GAME_CREATE_ERROR = direct("murderrun.command.game.create_error");
  NullComponent<Sender> GAME_NOT_OWNER_ERROR = direct("murderrun.command.game.owner_error");
  NullComponent<Sender> GAME_INVALID_ERROR = direct("murderrun.command.game.no_game_error");
  NullComponent<Sender> GAME_JOIN_ERROR = direct("murderrun.command.game.join_error");
  NullComponent<Sender> GAME_INVALID_INVITE_ERROR =
      direct("murderrun.command.game.invalid_invite_error");
  NullComponent<Sender> GAME_LOW_PLAYER_COUNT_ERROR =
      direct("murderrun.command.game.low_player_count_error");
  NullComponent<Sender> GAME_INVITE_ERROR = direct("murderrun.command.game.invite_error");
  NullComponent<Sender> VILLAGER_SPAWN = direct("murderrun.command.villager");
  NullComponent<Sender> HACK_NAME = direct("murderrun.game.gadget.hack.name");
  NullComponent<Sender> GLOW_NAME = direct("murderrun.game.gadget.glow.name");
  NullComponent<Sender> BEAR_NAME = direct("murderrun.game.gadget.bear.name");
  NullComponent<Sender> PORTAL_NAME = direct("murderrun.game.gadget.portal.name");
  NullComponent<Sender> SPASM_NAME = direct("murderrun.game.gadget.spasm.name");
  NullComponent<Sender> REWIND_NAME = direct("murderrun.game.gadget.rewind.name");
  NullComponent<Sender> MURDERER_REWIND_NAME = direct("murderrun.game.gadget.murderer_rewind.name");
  NullComponent<Sender> RESURRECTION_STONE_NAME =
      direct("murderrun.game.gadget.resurrection_stone.name");
  NullComponent<Sender> CORPUS_WARP_NAME = direct("murderrun.game.gadget.corpus_warp.name");
  NullComponent<Sender> GHOSTING_NAME = direct("murderrun.game.gadget.ghosting.name");
  NullComponent<Sender> EXCAVATOR_NAME = direct("murderrun.game.gadget.excavator.name");
  NullComponent<Sender> DISTORT_NAME = direct("murderrun.game.gadget.distort.name");
  NullComponent<Sender> HORCRUX_NAME = direct("murderrun.game.gadget.horcrux.name");
  NullComponent<Sender> MED_BOT_NAME = direct("murderrun.game.gadget.med_bot.name");
  NullComponent<Sender> RETALIATION_NAME = direct("murderrun.game.gadget.retaliation.name");
  NullComponent<Sender> SUPPLY_DROP_NAME = direct("murderrun.game.gadget.supply_drop.name");
  NullComponent<Sender> MED_KIT_NAME = direct("murderrun.game.gadget.med_kit.name");
  NullComponent<Sender> JUMP_SCARE_NAME = direct("murderrun.game.gadget.jump_scare.name");
  NullComponent<Sender> SMOKE_NAME = direct("murderrun.game.gadget.smoke.name");
  NullComponent<Sender> LEVITATION_NAME = direct("murderrun.game.gadget.levitation.name");
  NullComponent<Sender> CAGE_NAME = direct("murderrun.game.gadget.cage.name");
  NullComponent<Sender> BLIND_NAME = direct("murderrun.game.gadget.blind.name");
  NullComponent<Sender> HAUNT_NAME = direct("murderrun.game.gadget.haunt.name");
  NullComponent<Sender> NECK_SNAP_NAME = direct("murderrun.game.gadget.neck_snap.name");
  NullComponent<Sender> DEADRINGER_NAME = direct("murderrun.game.gadget.deadringer.name");
  NullComponent<Sender> STAR_NAME = direct("murderrun.game.gadget.star.name");
  NullComponent<Sender> SPAWN_NAME = direct("murderrun.game.gadget.spawn.name");
  NullComponent<Sender> FREEZE_NAME = direct("murderrun.game.gadget.freeze.name");
  NullComponent<Sender> BURROW_NAME = direct("murderrun.game.gadget.burrow.name");
  NullComponent<Sender> GHOST_NAME = direct("murderrun.game.gadget.ghost.name");
  NullComponent<Sender> PONY_NAME = direct("murderrun.game.gadget.pony.name");
  NullComponent<Sender> FIREWORK_NAME = direct("murderrun.game.gadget.firework.name");
  NullComponent<Sender> FART_NAME = direct("murderrun.game.gadget.fart.name");
  NullComponent<Sender> TRAP_VEST_NAME = direct("murderrun.game.gadget.trap_vest.name");
  NullComponent<Sender> RANDOM_NAME = direct("murderrun.game.gadget.random.name");
  NullComponent<Sender> MAGNET_MODE_NAME = direct("murderrun.game.gadget.magnet_mode.name");
  NullComponent<Sender> TRANSLOCATOR_NAME = direct("murderrun.game.gadget.translocator.name");
  NullComponent<Sender> TRACKER_NAME = direct("murderrun.game.gadget.tracker.name");
  NullComponent<Sender> DECOY_NAME = direct("murderrun.game.gadget.decoy.name");
  NullComponent<Sender> SMOKE_BOMB_NAME = direct("murderrun.game.gadget.smoke_bomb.name");
  NullComponent<Sender> FRIEND_WARP_NAME = direct("murderrun.game.gadget.friend_warp.name");
  NullComponent<Sender> FLASHBANG_NAME = direct("murderrun.game.gadget.flashbang.name");
  NullComponent<Sender> CAMERA_NAME = direct("murderrun.game.gadget.camera.name");
  NullComponent<Sender> CLOAK_NAME = direct("murderrun.game.gadget.cloak.name");
  NullComponent<Sender> SHIELD_NAME = direct("murderrun.game.gadget.shield.name");
  NullComponent<Sender> TP_ME_AWAY_FROM_HERE_NAME =
      direct("murderrun.game.gadget.tp_me_away_from_here.name");
  NullComponent<Sender> SIXTH_SENSE_NAME = direct("murderrun.game.gadget.sixth_sense.name");
  NullComponent<Sender> BLAST_OFF_NAME = direct("murderrun.game.gadget.blast_off.name");
  NullComponent<Sender> DRONE_NAME = direct("murderrun.game.gadget.drone.name");
  NullComponent<Sender> PART_SNIFFER_NAME = direct("murderrun.game.gadget.part_sniffer.name");
  NullComponent<Sender> CHIPPED_NAME = direct("murderrun.game.gadget.chipped.name");
  NullComponent<Sender> LIFE_INSURANCE_NAME = direct("murderrun.game.gadget.life_insurance.name");
  NullComponent<Sender> CRYO_FREEZE_NAME = direct("murderrun.game.gadget.cryo_freeze.name");
  NullComponent<Sender> ICE_SKATIN_NAME = direct("murderrun.game.gadget.ice_skatin.name");
  NullComponent<Sender> ICE_SPIRIT_NAME = direct("murderrun.game.gadget.ice_spirit.name");
  NullComponent<Sender> MIND_CONTROL_NAME = direct("murderrun.game.gadget.mind_control.name");
  NullComponent<Sender> JEB_NAME = direct("murderrun.game.gadget.jeb.name");
  NullComponent<Sender> BUSH_NAME = direct("murderrun.game.gadget.bush.name");
  NullComponent<Sender> KILLER_TRACKER_NAME = direct("murderrun.game.gadget.killer_tracker.name");
  NullComponent<Sender> FLASHLIGHT_NAME = direct("murderrun.game.gadget.flashlight.name");
  NullComponent<Sender> SHOCKWAVE_NAME = direct("murderrun.game.gadget.shockwave.name");
  NullComponent<Sender> PARASITE_NAME = direct("murderrun.game.gadget.parasite.name");
  NullComponent<Sender> DISTORTER_NAME = direct("murderrun.game.gadget.distorter.name");
  NullComponent<Sender> SPEED_PENDANT_NAME = direct("murderrun.game.gadget.speed_pendant.name");
  NullComponent<Sender> GLOW_LORE = direct("murderrun.game.gadget.glow.lore");
  NullComponent<Sender> HACK_LORE = direct("murderrun.game.gadget.hack.lore");
  NullComponent<Sender> BEAR_LORE = direct("murderrun.game.gadget.bear.lore");
  NullComponent<Sender> PORTAL_LORE = direct("murderrun.game.gadget.portal.lore");
  NullComponent<Sender> SPASM_LORE = direct("murderrun.game.gadget.spasm.lore");
  NullComponent<Sender> REWIND_LORE = direct("murderrun.game.gadget.rewind.lore");
  NullComponent<Sender> MURDERER_REWIND_LORE = direct("murderrun.game.gadget.murderer_rewind.lore");
  NullComponent<Sender> RESURRECTION_STONE_LORE =
      direct("murderrun.game.gadget.resurrection_stone.lore");
  NullComponent<Sender> CORPUS_WARP_LORE = direct("murderrun.game.gadget.corpus_warp.lore");
  NullComponent<Sender> GHOSTING_LORE = direct("murderrun.game.gadget.ghosting.lore");
  NullComponent<Sender> EXCAVATOR_LORE = direct("murderrun.game.gadget.excavator.lore");
  NullComponent<Sender> DISTORT_LORE = direct("murderrun.game.gadget.distort.lore");
  NullComponent<Sender> HORCRUX_LORE = direct("murderrun.game.gadget.horcrux.lore");
  NullComponent<Sender> MED_BOT_LORE = direct("murderrun.game.gadget.med_bot.lore");
  NullComponent<Sender> RETALIATION_LORE = direct("murderrun.game.gadget.retaliation.lore");
  NullComponent<Sender> SUPPLY_DROP_LORE = direct("murderrun.game.gadget.supply_drop.lore");
  NullComponent<Sender> MED_KIT_LORE = direct("murderrun.game.gadget.med_kit.lore");
  NullComponent<Sender> JUMP_SCARE_LORE = direct("murderrun.game.gadget.jump_scare.lore");
  NullComponent<Sender> SMOKE_LORE = direct("murderrun.game.gadget.smoke.lore");
  NullComponent<Sender> LEVITATION_LORE = direct("murderrun.game.gadget.levitation.lore");
  NullComponent<Sender> CAGE_LORE = direct("murderrun.game.gadget.cage.lore");
  NullComponent<Sender> BLIND_LORE = direct("murderrun.game.gadget.blind.lore");
  NullComponent<Sender> HAUNT_LORE = direct("murderrun.game.gadget.haunt.lore");
  NullComponent<Sender> NECK_SNAP_LORE = direct("murderrun.game.gadget.neck_snap.lore");
  NullComponent<Sender> DEADRINGER_LORE = direct("murderrun.game.gadget.deadringer.lore");
  NullComponent<Sender> STAR_LORE = direct("murderrun.game.gadget.star.lore");
  NullComponent<Sender> SPAWN_LORE = direct("murderrun.game.gadget.spawn.lore");
  NullComponent<Sender> FREEZE_LORE = direct("murderrun.game.gadget.freeze.lore");
  NullComponent<Sender> BURROW_LORE = direct("murderrun.game.gadget.burrow.lore");
  NullComponent<Sender> GHOST_LORE = direct("murderrun.game.gadget.ghost.lore");
  NullComponent<Sender> PONY_LORE = direct("murderrun.game.gadget.pony.lore");
  NullComponent<Sender> FIREWORK_LORE = direct("murderrun.game.gadget.firework.lore");
  NullComponent<Sender> FART_LORE = direct("murderrun.game.gadget.fart.lore");
  NullComponent<Sender> TRAP_VEST_LORE = direct("murderrun.game.gadget.trap_vest.lore");
  NullComponent<Sender> RANDOM_LORE = direct("murderrun.game.gadget.random.lore");
  NullComponent<Sender> MAGNET_MODE_LORE = direct("murderrun.game.gadget.magnet_mode.lore");
  NullComponent<Sender> TRANSLOCATOR_LORE = direct("murderrun.game.gadget.translocator.lore");
  NullComponent<Sender> TRANSLOCATOR_LORE1 = direct("murderrun.game.gadget.translocator.lore1");
  NullComponent<Sender> TRACKER_LORE = direct("murderrun.game.gadget.tracker.lore");
  NullComponent<Sender> DECOY_LORE = direct("murderrun.game.gadget.decoy.lore");
  NullComponent<Sender> SMOKE_BOMB_LORE = direct("murderrun.game.gadget.smoke_bomb.lore");
  NullComponent<Sender> FRIEND_WARP_LORE = direct("murderrun.game.gadget.friend_warp.lore");
  NullComponent<Sender> FLASHBANG_LORE = direct("murderrun.game.gadget.flashbang.lore");
  NullComponent<Sender> CAMERA_LORE = direct("murderrun.game.gadget.camera.lore");
  NullComponent<Sender> CLOAK_LORE = direct("murderrun.game.gadget.cloak.lore");
  NullComponent<Sender> SHIELD_LORE = direct("murderrun.game.gadget.shield.lore");
  NullComponent<Sender> TP_ME_AWAY_FROM_HERE_LORE =
      direct("murderrun.game.gadget.tp_me_away_from_here.lore");
  NullComponent<Sender> SIXTH_SENSE_LORE = direct("murderrun.game.gadget.sixth_sense.lore");
  NullComponent<Sender> BLAST_OFF_LORE = direct("murderrun.game.gadget.blast_off.lore");
  NullComponent<Sender> DRONE_LORE = direct("murderrun.game.gadget.drone.lore");
  NullComponent<Sender> PART_SNIFFER_LORE = direct("murderrun.game.gadget.part_sniffer.lore");
  NullComponent<Sender> CHIPPED_LORE = direct("murderrun.game.gadget.chipped.lore");
  NullComponent<Sender> LIFE_INSURANCE_LORE = direct("murderrun.game.gadget.life_insurance.lore");
  NullComponent<Sender> CRYO_FREEZE_LORE = direct("murderrun.game.gadget.cryo_freeze.lore");
  NullComponent<Sender> ICE_SKATIN_LORE = direct("murderrun.game.gadget.ice_skatin.lore");
  NullComponent<Sender> ICE_SPIRIT_LORE = direct("murderrun.game.gadget.ice_spirit.lore");
  NullComponent<Sender> MIND_CONTROL_LORE = direct("murderrun.game.gadget.mind_control.lore");
  NullComponent<Sender> JEB_LORE = direct("murderrun.game.gadget.jeb.lore");
  NullComponent<Sender> BUSH_LORE = direct("murderrun.game.gadget.bush.lore");
  NullComponent<Sender> KILLER_TRACKER_LORE = direct("murderrun.game.gadget.killer_tracker.lore");
  NullComponent<Sender> FLASHLIGHT_LORE = direct("murderrun.game.gadget.flashlight.lore");
  NullComponent<Sender> SHOCKWAVE_LORE = direct("murderrun.game.gadget.shockwave.lore");
  NullComponent<Sender> PARASITE_LORE = direct("murderrun.game.gadget.parasite.lore");
  NullComponent<Sender> DISTORTER_LORE = direct("murderrun.game.gadget.distorter.lore");
  NullComponent<Sender> GLOW_ACTIVATE = direct("murderrun.game.gadget.glow.activate");
  NullComponent<Sender> HACK_ACTIVATE = direct("murderrun.game.gadget.hack.activate");
  NullComponent<Sender> BEAR_ACTIVATE = direct("murderrun.game.gadget.bear.activate");
  NullComponent<Sender> SPASM_ACTIVATE = direct("murderrun.game.gadget.spasm.activate");
  NullComponent<Sender> RESURRECTION_STONE_ACTIVATE =
      direct("murderrun.game.gadget.resurrection_stone.activate");
  NullComponent<Sender> GHOSTING_ACTIVATE = direct("murderrun.game.gadget.ghosting.activate");
  NullComponent<Sender> DISTORT_ACTIVATE = direct("murderrun.game.gadget.distort.activate");
  NullComponent<Sender> HORCRUX_ACTIVATE = direct("murderrun.game.gadget.horcrux.activate");
  NullComponent<Sender> RETALIATION_ACTIVATE = direct("murderrun.game.gadget.retaliation.activate");
  NullComponent<Sender> JUMP_SCARE_ACTIVATE = direct("murderrun.game.gadget.jump_scare.activate");
  NullComponent<Sender> SMOKE_ACTIVATE = direct("murderrun.game.gadget.smoke.activate");
  NullComponent<Sender> LEVITATION_ACTIVATE = direct("murderrun.game.gadget.levitation.activate");
  NullComponent<Sender> CAGE_ACTIVATE = direct("murderrun.game.gadget.cage.activate");
  NullComponent<Sender> BLIND_ACTIVATE = direct("murderrun.game.gadget.blind.activate");
  NullComponent<Sender> HAUNT_ACTIVATE = direct("murderrun.game.gadget.haunt.activate");
  NullComponent<Sender> NECK_SNAP_ACTIVATE = direct("murderrun.game.gadget.neck_snap.activate");
  NullComponent<Sender> STAR_ACTIVATE = direct("murderrun.game.gadget.star.activate");
  NullComponent<Sender> SPAWN_ACTIVATE = direct("murderrun.game.gadget.spawn.activate");
  NullComponent<Sender> FREEZE_ACTIVATE = direct("murderrun.game.gadget.freeze.activate");
  NullComponent<Sender> BURROW_ACTIVATE = direct("murderrun.game.gadget.burrow.activate");
  NullComponent<Sender> GHOST_ACTIVATE = direct("murderrun.game.gadget.ghost.activate");
  NullComponent<Sender> FIREWORK_ACTIVATE = direct("murderrun.game.gadget.firework.activate");
  NullComponent<Sender> FART_ACTIVATE = direct("murderrun.game.gadget.fart.activate");
  NullComponent<Sender> TRAP_VEST_ACTIVATE = direct("murderrun.game.gadget.trap_vest.activate");
  NullComponent<Sender> MAGNET_MODE_ACTIVATE = direct("murderrun.game.gadget.magnet_mode.activate");
  NullComponent<Sender> CLOAK_ACTIVATE = direct("murderrun.game.gadget.cloak.activate");
  NullComponent<Sender> SIXTH_SENSE_ACTIVATE = direct("murderrun.game.gadget.sixth_sense.activate");
  NullComponent<Sender> PART_SNIFFER_ACTIVATE =
      direct("murderrun.game.gadget.part_sniffer.activate");
  NullComponent<Sender> JEB_ACTIVATE = direct("murderrun.game.gadget.jeb.activate");
  NullComponent<Sender> SHOCKWAVE_ACTIVATE = direct("murderrun.game.gadget.shockwave.activate");
  NullComponent<Sender> MED_BOT_DEACTIVATE = direct("murderrun.game.gadget.med_bot.deactivate");
  NullComponent<Sender> PARASITE_DEACTIVATE = direct("murderrun.game.gadget.parasite.deactivate");
  NullComponent<Sender> DISTORTER_DEACTIVATE = direct("murderrun.game.gadget.distorter.deactivate");
  NullComponent<Sender> SURVIVOR_HELMET = direct("murderrun.game.gadget.helmet");
  NullComponent<Sender> SURVIVOR_CHESTPLATE = direct("murderrun.game.gadget.chestplate");
  NullComponent<Sender> SURVIVOR_LEGGINGS = direct("murderrun.game.gadget.leggings");
  NullComponent<Sender> SURVIVOR_BOOTS = direct("murderrun.game.gadget.boots");
  NullComponent<Sender> PONY_ACTIVATE = direct("murderrun.game.gadget.pony.activate");
  UniComponent<Sender, String> ARENA_REMOVE = direct("murderrun.command.arena.remove", null);
  UniComponent<Sender, String> LOBBY_REMOVE = direct("murderrun.command.lobby.remove", null);
  NullComponent<Sender> ARENA_REMOVE_ERROR = direct("murderrun.command.arena.remove_error");
  NullComponent<Sender> LOBBY_REMOVE_ERROR = direct("murderrun.command.lobby.remove_error");
  NullComponent<Sender> LIFE_INSURANCE_ACTIVATE =
      direct("murderrun.game.gadget.life_insurance.activate");
  NullComponent<Sender> TRACKER_ACTIVATE = direct("murderrun.game.gadget.tracker.activate");
  NullComponent<Sender> TRACKER_DEACTIVATE = direct("murderrun.game.gadget.tracker.deactivate");
  UniComponent<Sender, Integer> KILLER_TRACKER_ACTIVATE =
      direct("murderrun.game.gadget.killer_tracker.activate", null);
  NullComponent<Sender> PLAYER_TRACKER_NAME = direct("murderrun.game.gadget.player_tracker.name");
  NullComponent<Sender> KILLER_CAMERA_NAME = direct("murderrun.game.gadget.camera_killer.name");
  NullComponent<Sender> WARP_DISTORT_NAME = direct("murderrun.game.gadget.warp_distort.name");
  NullComponent<Sender> TRAP_WRECKER_NAME = direct("murderrun.game.gadget.trap_wrecker.name");
  NullComponent<Sender> TRAP_SEEKER_NAME = direct("murderrun.game.gadget.trap_seeker.name");
  NullComponent<Sender> INFRARED_VISION_NAME = direct("murderrun.game.gadget.infrared_vision.name");
  NullComponent<Sender> FRIGHT_NAME = direct("murderrun.game.gadget.fright.name");
  NullComponent<Sender> BLOOD_CURSE_NAME = direct("murderrun.game.gadget.blood_curse.name");
  NullComponent<Sender> DEATH_STEED_NAME = direct("murderrun.game.gadget.death_steed.name");
  NullComponent<Sender> ALL_SEEING_EYE_NAME = direct("murderrun.game.gadget.all_seeing_eye.name");
  NullComponent<Sender> PHANTOM_NAME = direct("murderrun.game.gadget.phantom.name");
  NullComponent<Sender> DEATH_HOUND_NAME = direct("murderrun.game.gadget.death_hound.name");
  NullComponent<Sender> CORRUPTION_NAME = direct("murderrun.game.gadget.corruption.name");
  NullComponent<Sender> MURDEROUS_WARP_NAME = direct("murderrun.game.gadget.murderous_warp.name");
  NullComponent<Sender> PORTAL_GUN_NAME = direct("murderrun.game.gadget.portal_gun.name");
  NullComponent<Sender> HEAT_SEEKER_NAME = direct("murderrun.game.gadget.heat_seeker.name");
  NullComponent<Sender> THE_FLOOR_IS_LAVA_NAME =
      direct("murderrun.game.gadget.the_floor_is_lava.name");
  NullComponent<Sender> EMP_BLAST_NAME = direct("murderrun.game.gadget.emp_blast.name");
  NullComponent<Sender> GAMBLE_NAME = direct("murderrun.game.gadget.gamble.name");
  NullComponent<Sender> QUICK_BOMB_NAME = direct("murderrun.game.gadget.quick_bomb.name");
  NullComponent<Sender> HEALTH_CUT_NAME = direct("murderrun.game.gadget.health_cut.name");
  NullComponent<Sender> POISON_SMOG_NAME = direct("murderrun.game.gadget.poison_smog.name");
  NullComponent<Sender> PART_WARP_NAME = direct("murderrun.game.gadget.part_warp.name");
  NullComponent<Sender> HOOK_NAME = direct("murderrun.game.gadget.hook.name");
  NullComponent<Sender> EAGLE_EYE_NAME = direct("murderrun.game.gadget.eagle_eye.name");
  NullComponent<Sender> FAKE_PART_NAME = direct("murderrun.game.gadget.fake_part.name");
  NullComponent<Sender> BURN_THE_BODY_NAME = direct("murderrun.game.gadget.burn_the_body.name");
  NullComponent<Sender> ENDER_SHADOWS_NAME = direct("murderrun.game.gadget.ender_shadows.name");
  NullComponent<Sender> FOREWARN_NAME = direct("murderrun.game.gadget.forewarn.name");
  NullComponent<Sender> FIRE_TRAIL_NAME = direct("murderrun.game.gadget.fire_trail.name");
  NullComponent<Sender> ICE_PATH_NAME = direct("murderrun.game.gadget.ice_path.name");
  NullComponent<Sender> DORMAGOGG_NAME = direct("murderrun.game.gadget.dormagogg.name");
  NullComponent<Sender> CURSED_NOTE_NAME = direct("murderrun.game.gadget.cursed_note.name");
  NullComponent<Sender> RED_ARROW_NAME = direct("murderrun.game.gadget.red_arrow.name");
  NullComponent<Sender> PLAYER_TRACKER_LORE = direct("murderrun.game.gadget.player_tracker.lore");
  NullComponent<Sender> KILLER_CAMERA_LORE = direct("murderrun.game.gadget.killer_camera.lore");
  NullComponent<Sender> WARP_DISTORT_LORE = direct("murderrun.game.gadget.warp_distort.lore");
  NullComponent<Sender> TRAP_WRECKER_LORE = direct("murderrun.game.gadget.trap_wrecker.lore");
  NullComponent<Sender> TRAP_SEEKER_LORE = direct("murderrun.game.gadget.trap_seeker.lore");
  NullComponent<Sender> INFRARED_VISION_LORE = direct("murderrun.game.gadget.infrared_vision.lore");
  NullComponent<Sender> FRIGHT_LORE = direct("murderrun.game.gadget.fright.lore");
  NullComponent<Sender> BLOOD_CURSE_LORE = direct("murderrun.game.gadget.blood_curse.lore");
  NullComponent<Sender> DEATH_STEED_LORE = direct("murderrun.game.gadget.death_steed.lore");
  NullComponent<Sender> ALL_SEEING_EYE_LORE = direct("murderrun.game.gadget.all_seeing_eye.lore");
  NullComponent<Sender> PHANTOM_LORE = direct("murderrun.game.gadget.phantom.lore");
  NullComponent<Sender> DEATH_HOUND_LORE = direct("murderrun.game.gadget.death_hound.lore");
  NullComponent<Sender> CORRUPTION_LORE = direct("murderrun.game.gadget.corruption.lore");
  NullComponent<Sender> MURDEROUS_WARP_LORE = direct("murderrun.game.gadget.murderous_warp.lore");
  NullComponent<Sender> PORTAL_GUN_LORE = direct("murderrun.game.gadget.portal_gun.lore");
  NullComponent<Sender> HEAT_SEEKER_LORE = direct("murderrun.game.gadget.heat_seeker.lore");
  NullComponent<Sender> THE_FLOOR_IS_LAVA_LORE =
      direct("murderrun.game.gadget.the_floor_is_lava.lore");
  NullComponent<Sender> EMP_BLAST_LORE = direct("murderrun.game.gadget.emp_blast.lore");
  NullComponent<Sender> GAMBLE_LORE = direct("murderrun.game.gadget.gamble.lore");
  NullComponent<Sender> QUICK_BOMB_LORE = direct("murderrun.game.gadget.quick_bomb.lore");
  NullComponent<Sender> HEALTH_CUT_LORE = direct("murderrun.game.gadget.health_cut.lore");
  NullComponent<Sender> POISON_SMOG_LORE = direct("murderrun.game.gadget.poison_smog.lore");
  NullComponent<Sender> PART_WARP_LORE = direct("murderrun.game.gadget.part_warp.lore");
  NullComponent<Sender> HOOK_LORE = direct("murderrun.game.gadget.hook.lore");
  NullComponent<Sender> EAGLE_EYE_LORE = direct("murderrun.game.gadget.eagle_eye.lore");
  NullComponent<Sender> FAKE_PART_LORE = direct("murderrun.game.gadget.fake_part.lore");
  NullComponent<Sender> BURN_THE_BODY_LORE = direct("murderrun.game.gadget.burn_the_body.lore");
  NullComponent<Sender> ENDER_SHADOWS_LORE = direct("murderrun.game.gadget.ender_shadows.lore");
  NullComponent<Sender> FOREWARN_LORE = direct("murderrun.game.gadget.forewarn.lore");
  NullComponent<Sender> FIRE_TRAIL_LORE = direct("murderrun.game.gadget.fire_trail.lore");
  NullComponent<Sender> ICE_PATH_LORE = direct("murderrun.game.gadget.ice_path.lore");
  NullComponent<Sender> DORMAGOGG_LORE = direct("murderrun.game.gadget.dormagogg.lore");
  NullComponent<Sender> CURSED_NOTE_LORE = direct("murderrun.game.gadget.cursed_note.lore");
  NullComponent<Sender> RED_ARROW_LORE = direct("murderrun.game.gadget.red_arrow.lore");
  UniComponent<Sender, Integer> PLAYER_TRACKER_ACTIVATE =
      direct("murderrun.game.gadget.player_tracker.activate", null);
  NullComponent<Sender> WARP_DISTORT_ACTIVATE =
      direct("murderrun.game.gadget.warp_distort.activate");
  NullComponent<Sender> TRAP_WRECKER_ACTIVATE =
      direct("murderrun.game.gadget.trap_wrecker.activate");
  NullComponent<Sender> TRAP_SEEKER_ACTIVATE = direct("murderrun.game.gadget.trap_seeker.activate");
  NullComponent<Sender> INFRARED_VISION_ACTIVATE =
      direct("murderrun.game.gadget.infrared_vision.activate");
  NullComponent<Sender> BLOOD_CURSE_ACTIVATE = direct("murderrun.game.gadget.blood_curse.activate");
  NullComponent<Sender> CORRUPTION_ACTIVATE = direct("murderrun.game.gadget.corruption.activate");
  NullComponent<Sender> HEAT_SEEKER_ACTIVATE = direct("murderrun.game.gadget.heat_seeker.activate");
  NullComponent<Sender> THE_FLOOR_IS_LAVA_ACTIVATE =
      direct("murderrun.game.gadget.the_floor_is_lava.activate");
  NullComponent<Sender> EMP_BLAST_ACTIVATE = direct("murderrun.game.gadget.emp_blast.activate");
  NullComponent<Sender> GAMBLE_ACTIVATE = direct("murderrun.game.gadget.gamble.activate");
  NullComponent<Sender> HEALTH_CUT_ACTIVATE = direct("murderrun.game.gadget.health_cut.activate");
  NullComponent<Sender> FAKE_PART_ACTIVATE = direct("murderrun.game.gadget.fake_part.activate");
  NullComponent<Sender> ENDER_SHADOWS_ACTIVATE =
      direct("murderrun.game.gadget.ender_shadows.activate");
  NullComponent<Sender> FOREWARN_ACTIVATE = direct("murderrun.game.gadget.forewarn.activate");
  NullComponent<Sender> CURSED_NOTE_ACTIVATE = direct("murderrun.game.gadget.cursed_note.activate");
  NullComponent<Sender> RED_ARROW_ACTIVATE = direct("murderrun.game.gadget.red_arrow.activate");
  NullComponent<Sender> ENDER_SHADOWS_EFFECT = direct("murderrun.game.gadget.ender_shadows.effect");
  NullComponent<Sender> CURSED_NOTE_DROP = direct("murderrun.game.gadget.cursed_note.drop");
  NullComponent<Sender> ARROW_NAME = direct("murderrun.game.gadget.arrow.name");
  NullComponent<Sender> ARROW_LORE = direct("murderrun.game.gadget.arrow.lore");
  NullComponent<Sender> KILLER_SWORD = direct("murderrun.game.sword.name");
  NullComponent<Sender> MINEBUCKS = direct("murderrun.game.currency.name");
  NullComponent<Sender> GADGET_RETRIEVE_ERROR = direct("murderrun.command.gadget.retrieve.error");
  NullComponent<Sender> GAME_INVITE_ALREADY_ERROR =
      direct("murderrun.command.game.invite_already_error");
  NullComponent<Sender> GAME_STARTED_ERROR = direct("murderrun.command.game.start_error");
  UniComponent<Sender, String> MIND_CONTROL_ACTIVATE_KILLER =
      direct("murderrun.game.gadget.mind_control.activate_killer", null);
  UniComponent<Sender, String> MIND_CONTROL_ACTIVATE_SURVIVOR =
      direct("murderrun.game.gadget.mind_control.activate_survivor", null);
  NullComponent<Sender> SHOP_GUI_TITLE = direct("murderrun.gui.shop.name");
  NullComponent<Sender> SHOP_GUI_CANCEL = direct("murderrun.gui.shop.cancel");
  NullComponent<Sender> SHOP_GUI_FORWARD = direct("murderrun.gui.shop.forward");
  NullComponent<Sender> SHOP_GUI_BACK = direct("murderrun.gui.shop.back");
  UniComponent<Sender, Integer> SHOP_GUI_COST_LORE = direct("murderrun.gui.shop.lore", null);
  NullComponent<Sender> SHOP_GUI_ERROR = direct("murderrun.gui.shop.error");
  NullComponent<Sender> GRIM_REAPER_NPC = direct("murderrun.npc.killer.name");
  NullComponent<Sender> GUARDIAN_ANGEL_NPC = direct("murderrun.npc.survivor.name");
  TriComponent<Sender, Integer, Integer, Integer> ARENA_ITEM_ADD =
      direct("murderrun.command.arena.item.add", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> ARENA_ITEM_REMOVE =
      direct("murderrun.command.arena.item.remove", null, null, null);
  NullComponent<Sender> ARENA_ITEM_REMOVE_ERROR =
      direct("murderrun.command.arena.item.remove.error");
  UniComponent<Sender, List<String>> ARENA_ITEM_LIST =
      direct("murderrun.command.arena.item.list", null);
  NullComponent<Sender> ARENA_COPY = direct("murderrun.command.arena.copy");
  NullComponent<Sender> ITEM_ARENA_NAME = direct("murderrun.item.arena.wand.name");
  NullComponent<Sender> ITEM_ARENA_LORE = direct("murderrun.item.arena.wand.lore");
  NullComponent<Sender> CENTRAL_GUI_TITLE = direct("murderrun.gui.central.name");
  NullComponent<Sender> MANAGE_LOBBY_GUI_TITLE = direct("murderrun.gui.lobby.manage");
  NullComponent<Sender> MANAGE_LOBBY_GUI_CREATE = direct("murderrun.gui.lobby.manage.create");
  NullComponent<Sender> MANAGE_LOBBY_GUI_EDIT = direct("murderrun.gui.lobby.manage.edit");
  NullComponent<Sender> CREATE_LOBBY_GUI_TITLE = direct("murderrun.gui.lobby.create.title");
  UniComponent<Sender, String> CREATE_LOBBY_GUI_EDIT_NAME_DISPLAY =
      direct("murderrun.gui.lobby.create.edit_name_display", null);
  NullComponent<Sender> CREATE_LOBBY_GUI_EDIT_NAME_LORE =
      direct("murderrun.gui.lobby.create.edit_name_lore");
  TriComponent<Sender, Integer, Integer, Integer> CREATE_LOBBY_GUI_EDIT_SPAWN_DISPLAY =
      direct("murderrun.gui.lobby.create.edit_spawn_display", null, null, null);
  NullComponent<Sender> CREATE_LOBBY_GUI_EDIT_SPAWN_LORE =
      direct("murderrun.gui.lobby.create.edit_spawn_lore");
  NullComponent<Sender> CREATE_LOBBY_GUI_EDIT_NAME = direct("murderrun.gui.lobby.create.edit_name");
  NullComponent<Sender> CREATE_LOBBY_GUI_EDIT_SPAWN =
      direct("murderrun.gui.lobby.create.edit_spawn");
  NullComponent<Sender> CREATE_LOBBY_GUI_DELETE = direct("murderrun.gui.lobby.create.edit_delete");
  NullComponent<Sender> CREATE_LOBBY_GUI_APPLY =
      direct("murderrun.gui.lobby.create.edit_create_display");
  NullComponent<Sender> CHOOSE_LOBBY_GUI_TITLE = direct("murderrun.gui.lobby.choose.title");
  UniComponent<Sender, String> CHOOSE_LOBBY_GUI_LOBBY_DISPLAY =
      direct("murderrun.gui.lobby.choose.lobby_title", null);
  TriComponent<Sender, Integer, Integer, Integer> CHOOSE_LOBBY_GUI_LOBBY_LORE =
      direct("murderrun.gui.lobby.choose.lobby_lore", null, null, null);
  NullComponent<Sender> CENTRAL_GUI_LOBBY = direct("murderrun.gui.central.lobby");
  NullComponent<Sender> CENTRAL_GUI_ARENA = direct("murderrun.gui.central.arena");
  NullComponent<Sender> CENTRAL_GUI_GAME = direct("murderrun.gui.central.game");
  NullComponent<Sender> SHOP_NPC_ERROR = direct("murderrun.npc.error");
  NullComponent<Sender> MISUSE_GADGET_ERROR = direct("murderrun.gadget.error");
  NullComponent<Sender> CREATE_ARENA_GUI_TITLE = direct("murderrun.gui.arena.create.title");
  UniComponent<Sender, String> CREATE_ARENA_GUI_EDIT_NAME_DISPLAY =
      direct("murderrun.gui.arena.create.edit_name_display", null);
  NullComponent<Sender> CREATE_ARENA_GUI_EDIT_NAME_LORE =
      direct("murderrun.gui.arena.create.edit_name_lore");
  NullComponent<Sender> CREATE_ARENA_GUI_EDIT_NAME = direct("murderrun.gui.arena.create.edit_name");
  NullComponent<Sender> CREATE_ARENA_GUI_EDIT_LOCATIONS_DISPLAY =
      direct("murderrun.gui.arena.create.edit_locations_display");
  NullComponent<Sender> CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE1 =
      direct("murderrun.gui.arena.create.edit_locations_lore1");
  TriComponent<Sender, Integer, Integer, Integer> CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE2 =
      direct("murderrun.gui.arena.create.edit_locations_lore2", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE3 =
      direct("murderrun.gui.arena.create.edit_locations_lore3", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE4 =
      direct("murderrun.gui.arena.create.edit_locations_lore4", null, null, null);
  TriComponent<Sender, Integer, Integer, Integer> CREATE_ARENA_GUI_EDIT_LOCATIONS_LORE5 =
      direct("murderrun.gui.arena.create.edit_locations_lore5", null, null, null);
  NullComponent<Sender> CREATE_ARENA_GUI_EDIT_SPAWN =
      direct("murderrun.gui.arena.create.edit_spawn");
  NullComponent<Sender> CREATE_ARENA_GUI_EDIT_FIRST =
      direct("murderrun.gui.arena.create.edit_first");
  NullComponent<Sender> CREATE_ARENA_GUI_EDIT_SECOND =
      direct("murderrun.gui.arena.create.edit_second");
  NullComponent<Sender> CREATE_ARENA_GUI_EDIT_TRUCK =
      direct("murderrun.gui.arena.create.edit_truck");
  NullComponent<Sender> CREATE_ARENA_GUI_WAND_DISPLAY =
      direct("murderrun.gui.arena.create.edit_wand_display");
  NullComponent<Sender> CREATE_ARENA_GUI_WAND_LORE =
      direct("murderrun.gui.arena.create.edit_wand_lore");
  NullComponent<Sender> CREATE_ARENA_GUI_WAND = direct("murderrun.gui.arena.create.edit_wand");
  NullComponent<Sender> CREATE_ARENA_GUI_APPLY =
      direct("murderrun.gui.arena.create.edit_create_display");
  NullComponent<Sender> CREATE_ARENA_GUI_DELETE = direct("murderrun.gui.arena.create.edit_delete");
  UniComponent<Sender, String> CHOOSE_ARENA_GUI_ARENA_DISPLAY =
      direct("murderrun.gui.arena.choose.arena_title", null);
  TriComponent<Sender, Integer, Integer, Integer> CHOOSE_ARENA_GUI_ARENA_LORE =
      direct("murderrun.gui.arena.choose.arena_lore", null, null, null);
  NullComponent<Sender> CHOOSE_ARENA_GUI_TITLE = direct("murderrun.gui.arena.choose.title");
  NullComponent<Sender> MANAGE_ARENA_GUI_CREATE = direct("murderrun.gui.arena.manage.create");
  NullComponent<Sender> MANAGE_ARENA_GUI_EDIT = direct("murderrun.gui.arena.manage.edit");
  NullComponent<Sender> MANAGE_ARENA_GUI_TITLE = direct("murderrun.gui.arena.manage");
  NullComponent<Sender> REWIND_ACTIVATE = direct("murderrun.game.gadget.rewind.activate");
  UniComponent<Sender, String> CREATE_GAME_GUI_LOBBY_DISPLAY =
      direct("murderrun.gui.game.lobby_display", null);
  NullComponent<Sender> CREATE_GAME_GUI_LOBBY_LORE = direct("murderrun.gui.game.lobby_lore");
  UniComponent<Sender, String> CREATE_GAME_GUI_ARENA_DISPLAY =
      direct("murderrun.gui.game.arena_display", null);
  NullComponent<Sender> CREATE_GAME_GUI_ARENA_LORE = direct("murderrun.gui.game.arena_lore");
  NullComponent<Sender> CREATE_GAME_GUI_APPLY = direct("murderrun.gui.game.apply");
  NullComponent<Sender> CREATE_GAME_GUI_TITLE = direct("murderrun.gui.game.create");
  NullComponent<Sender> CREATE_GAME_GUI_ERROR = direct("murderrun.gui.game.create_error");
  UniComponent<Sender, String> INVITE_PLAYER_GUI_DISPLAY =
      direct("murderrun.gui.player.display", null);
  UniComponent<Sender, String> INVITE_PLAYER_GUI_SURVIVOR =
      direct("murderrun.gui.player.survivor", null);
  UniComponent<Sender, String> INVITE_PLAYER_GUI_KILLER =
      direct("murderrun.gui.player.killer", null);
  NullComponent<Sender> INVITE_PLAYER_GUI_LORE = direct("murderrun.gui.player.lore");
  NullComponent<Sender> INVITE_PLAYER_GUI_LORE_NORMAL = direct("murderrun.gui.player.lore_normal");
  UniComponent<Sender, Integer> LOBBY_TIMER = direct("murderrun.game.lobby.timer", null);
  NullComponent<Sender> LOBBY_TIMER_SKIP = direct("murderrun.game.lobby.timer.skip");
  NullComponent<Sender> MIMIC_NAME = direct("murderrun.game.gadget.mimic.name");
  NullComponent<Sender> MIMIC_LORE = direct("murderrun.game.gadget.mimic.lore");
  NullComponent<Sender> MIMIC_ACTIVATE = direct("murderrun.game.gadget.mimic.activate");
  UniComponent<Sender, String> DEAD_CHAT_PREFIX = direct("murderrun.chat.dead", null);
  NullComponent<Sender> SCOREBOARD_TITLE = direct("murderrun.game.scoreboard.title");
  NullComponent<Sender> SCOREBOARD_ROLE = direct("murderrun.game.scoreboard.role");
  NullComponent<Sender> SCOREBOARD_ROLE_SURVIVOR =
      direct("murderrun.game.scoreboard.role.survivor");
  NullComponent<Sender> SCOREBOARD_ROLE_KILLER = direct("murderrun.game.scoreboard.role.killer");
  NullComponent<Sender> SCOREBOARD_OBJECTIVE = direct("murderrun.game.scoreboard.objective");
  NullComponent<Sender> SCOREBOARD_OBJECTIVE_SURVIVOR =
      direct("murderrun.game.scoreboard.objective.survivor");
  NullComponent<Sender> SCOREBOARD_OBJECTIVE_KILLER =
      direct("murderrun.game.scoreboard.objective.killer");
  NullComponent<Sender> SCOREBOARD_PARTS = direct("murderrun.game.scoreboard.parts");
  UniComponent<Sender, Integer> SCOREBOARD_PARTS_COUNT =
      direct("murderrun.game.scoreboard.parts.count", null);
  UniComponent<Sender, Integer> SCOREBOARD_MIN_PLAYERS =
      direct("murderrun.game.scoreboard.min", null);
  NullComponent<Sender> KILLER_ASSIGN = direct("murderrun.game.killer_assign");
  NullComponent<Sender> LOBBY_TIMER_CANCEL = direct("murderrun.lobby.timer.cancel");
  NullComponent<Sender> LOBBY_SCOREBOARD_TITLE = direct("murdderun.lobby.scoreboard.title");
  UniComponent<Sender, String> LOBBY_SCOREBOARD_ARENA =
      direct("murderrun.lobby.scoreboard.arena", null);
  BiComponent<Sender, Integer, Integer> LOBBY_SCOREBOARD_PLAYERS =
      direct("murderrun.lobby.scoreboard.players", null, null);
  UniComponent<Sender, Integer> LOBBY_SCOREBOARD_TIME =
      direct("murderrun.lobby.scoreboard.time", null);
  NullComponent<Sender> LOBBY_SCOREBOARD_DOMAIN = direct("murderrun.lobby.scoreboard.domain");
  NullComponent<Sender> GAME_FULL = direct("murderrun.command.game.full");
  NullComponent<Sender> GAME_NONE = direct("murderrun.command.game.none");
  NullComponent<Sender> GAME_CREATE_EDIT_ID = direct("murderrun.gui.game.create.edit_id");
  NullComponent<Sender> GAME_CREATE_EDIT_MIN = direct("murderrun.gui.game.create.edit_min");
  NullComponent<Sender> GAME_CREATE_EDIT_MAX = direct("murderrun.gui.game.create.edit_max");
  NullComponent<Sender> GAME_CREATE_EDIT_COUNT_ERROR =
      direct("murderrun.gui.game.create.edit_count_error");
  UniComponent<Sender, String> GAME_CREATE_EDIT_ID_DISPLAY =
      direct("murderrun.gui.game.create.edit_id_display", null);
  UniComponent<Sender, Integer> GAME_CREATE_EDIT_MIN_DISPLAY =
      direct("murderrun.gui.game.create.edit_min_display", null);
  UniComponent<Sender, Integer> GAME_CREATE_EDIT_MAX_DISPLAY =
      direct("murderrun.gui.game.create.edit_max_display", null);
  NullComponent<Sender> GAME_CREATE_EDIT_ID_LORE = direct("murderrun.gui.game.create.edit_id_lore");
  NullComponent<Sender> GAME_CREATE_EDIT_MIN_LORE =
      direct("murderrun.gui.game.create.edit_min_lore");
  NullComponent<Sender> GAME_CREATE_EDIT_MAX_LORE =
      direct("murderrun.gui.game.create.edit_max_lore");
}
