package io.github.pulsebeat02.murderrun.locale;

import java.util.List;

import static io.github.pulsebeat02.murderrun.locale.LocaleParent.direct;

public interface Locale extends LocaleParent {
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
  NullComponent<Sender> MURDERER_REWIND_TRAP_NAME = direct("murder_run.game.trap.murderer_rewind.name");
  NullComponent<Sender> RESURRECTION_STONE_TRAP_NAME = direct("murder_run.game.trap.resurrection_stone.name");
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
  NullComponent<Sender> DIAMOND_ARMOR_TRAP_NAME = direct("murder_run.game.trap.diamond_armor.name");
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
  NullComponent<Sender> TAGLOCK_NEEDLE_TRAP_NAME = direct("murder_run.game.trap.taglock_needle.name");
  NullComponent<Sender> DECOY_TRAP_NAME = direct("murder_run.game.trap.decoy.name");
  NullComponent<Sender> SMOKE_BOMB_TRAP_NAME = direct("murder_run.game.trap.smoke_bomb.name");
  NullComponent<Sender> FRIEND_WARP_TRAP_NAME = direct("murder_run.game.trap.friend_warp.name");
  NullComponent<Sender> FLASHBANG_TRAP_NAME = direct("murder_run.game.trap.flashbang.name");
  NullComponent<Sender> CAMERA_TRAP_NAME = direct("murder_run.game.trap.camera.name");
  NullComponent<Sender> CLOAK_TRAP_NAME = direct("murder_run.game.trap.cloak.name");
  NullComponent<Sender> SHIELD_TRAP_NAME = direct("murder_run.game.trap.shield.name");
  NullComponent<Sender> TP_ME_AWAY_FROM_HERE_TRAP_NAME = direct("murder_run.game.trap.tp_me_away_from_here.name");
  NullComponent<Sender> SIXTH_SENSE_TRAP_NAME = direct("murder_run.game.trap.sixth_sense.name");
  NullComponent<Sender> BLAST_OFF_TRAP_NAME = direct("murder_run.game.trap.blast_off.name");
  NullComponent<Sender> DRONE_TRAP_NAME = direct("murder_run.game.trap.drone.name");
  NullComponent<Sender> TRAP_SNIFFER_TRAP_NAME = direct("murder_run.game.trap.trap_sniffer.name");
  NullComponent<Sender> SOME_CAKE_IN_VEGAS_TRAP_NAME = direct("murder_run.game.trap.some_cake_in_vegas.name");
  NullComponent<Sender> CHIPPED_TRAP_NAME = direct("murder_run.game.trap.chipped.name");
  NullComponent<Sender> LIFE_INSURANCE_TRAP_NAME = direct("murder_run.game.trap.life_insurance.name");
  NullComponent<Sender> CRYO_FREEZE_TRAP_NAME = direct("murder_run.game.trap.cryo_freeze.name");
  NullComponent<Sender> ICE_SKATIN_TRAP_NAME = direct("murder_run.game.trap.ice_skatin.name");
  NullComponent<Sender> ICE_SPIRIT_TRAP_NAME = direct("murder_run.game.trap.ice_spirit.name");
  NullComponent<Sender> MIND_CONTROL_TRAP_NAME = direct("murder_run.game.trap.mind_control.name");
  NullComponent<Sender> JEB_TRAP_NAME = direct("murder_run.game.trap.jeb.name");
  NullComponent<Sender> BUSH_TRAP_NAME = direct("murder_run.game.trap.bush.name");
  NullComponent<Sender> FORTNITE_BUILDING_TRAP_NAME = direct("murder_run.game.trap.fortnite_building.name");
  NullComponent<Sender> JACK_JACK_LASER_EYES_TRAP_NAME = direct("murder_run.game.trap.jack_jack_laser_eyes.name");
  NullComponent<Sender> VIOLET_FORCE_FIELD_RIFT_TRAP_NAME = direct("murder_run.game.trap.violet_force_field_rift.name");
  NullComponent<Sender> KILLER_TRACKER_TRAP_NAME = direct("murder_run.game.trap.killer_tracker.name");
  NullComponent<Sender> FLASHLIGHT_TRAP_NAME = direct("murder_run.game.trap.flashlight.name");
  NullComponent<Sender> LAUNCH_PAD_TRAP_NAME = direct("murder_run.game.trap.launch_pad.name");
  NullComponent<Sender> IMPULSE_GRENADE_TRAP_NAME = direct("murder_run.game.trap.impulse_grenade.name");
  NullComponent<Sender> ZARYA_GRAVITRON_SURGE_TRAP_NAME = direct("murder_run.game.trap.zarya_gravitron_surge.name");
  NullComponent<Sender> LUCIO_CRANK_IT_UP_TRAP_NAME = direct("murder_run.game.trap.lucio_crank_it_up.name");
  NullComponent<Sender> SHOCKWAVE_TRAP_NAME = direct("murder_run.game.trap.shockwave.name");
  NullComponent<Sender> PARASITE_TRAP_NAME = direct("murder_run.game.trap.parasite.name");
  NullComponent<Sender> PORTA_FORT_TRAP_NAME = direct("murder_run.game.trap.porta_fort.name");
  NullComponent<Sender> DISTORTER_TRAP_NAME = direct("murder_run.game.trap.distorter.name");
  NullComponent<Sender> CLICKBAIT_TRAP_NAME = direct("murder_run.game.trap.clickbait.name");
  NullComponent<Sender> DEMONETIZED_TRAP_NAME = direct("murder_run.game.trap.demonetized.name");
  NullComponent<Sender> JETPACK_TRAP_NAME = direct("murder_run.game.trap.jetpack.name");

  NullComponent<Sender> GLOW_TRAP_LORE = direct("murder_run.game.trap.glow.lore");
  NullComponent<Sender> HACK_TRAP_LORE = direct("murder_run.game.trap.hack.lore");
  NullComponent<Sender> BEAR_TRAP_LORE = direct("murder_run.game.trap.bear.lore");
  NullComponent<Sender> PORTAL_TRAP_LORE = direct("murder_run.game.trap.portal.lore");
  NullComponent<Sender> SPASM_TRAP_LORE = direct("murder_run.game.trap.spasm.lore");
  NullComponent<Sender> REWIND_TRAP_LORE = direct("murder_run.game.trap.rewind.lore");
  NullComponent<Sender> MURDERER_REWIND_TRAP_LORE = direct("murder_run.game.trap.murderer_rewind.lore");
  NullComponent<Sender> RESURRECTION_STONE_TRAP_LORE = direct("murder_run.game.trap.resurrection_stone.lore");
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
  NullComponent<Sender> DIAMOND_ARMOR_TRAP_LORE = direct("murder_run.game.trap.diamond_armor.lore");
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
  NullComponent<Sender> TAGLOCK_NEEDLE_TRAP_LORE = direct("murder_run.game.trap.taglock_needle.lore");
  NullComponent<Sender> DECOY_TRAP_LORE = direct("murder_run.game.trap.decoy.lore");
  NullComponent<Sender> SMOKE_BOMB_TRAP_LORE = direct("murder_run.game.trap.smoke_bomb.lore");
  NullComponent<Sender> FRIEND_WARP_TRAP_LORE = direct("murder_run.game.trap.friend_warp.lore");
  NullComponent<Sender> FLASHBANG_TRAP_LORE = direct("murder_run.game.trap.flashbang.lore");
  NullComponent<Sender> CAMERA_TRAP_LORE = direct("murder_run.game.trap.camera.lore");
  NullComponent<Sender> CLOAK_TRAP_LORE = direct("murder_run.game.trap.cloak.lore");
  NullComponent<Sender> SHIELD_TRAP_LORE = direct("murder_run.game.trap.shield.lore");
  NullComponent<Sender> TP_ME_AWAY_FROM_HERE_TRAP_LORE = direct("murder_run.game.trap.tp_me_away_from_here.lore");
  NullComponent<Sender> SIXTH_SENSE_TRAP_LORE = direct("murder_run.game.trap.sixth_sense.lore");
  NullComponent<Sender> BLAST_OFF_TRAP_LORE = direct("murder_run.game.trap.blast_off.lore");
  NullComponent<Sender> DRONE_TRAP_LORE = direct("murder_run.game.trap.drone.lore");
  NullComponent<Sender> TRAP_SNIFFER_TRAP_LORE = direct("murder_run.game.trap.trap_sniffer.lore");
  NullComponent<Sender> SOME_CAKE_IN_VEGAS_TRAP_LORE = direct("murder_run.game.trap.some_cake_in_vegas.lore");
  NullComponent<Sender> CHIPPED_TRAP_LORE = direct("murder_run.game.trap.chipped.lore");
  NullComponent<Sender> LIFE_INSURANCE_TRAP_LORE = direct("murder_run.game.trap.life_insurance.lore");
  NullComponent<Sender> CRYO_FREEZE_TRAP_LORE = direct("murder_run.game.trap.cryo_freeze.lore");
  NullComponent<Sender> ICE_SKATIN_TRAP_LORE = direct("murder_run.game.trap.ice_skatin.lore");
  NullComponent<Sender> ICE_SPIRIT_TRAP_LORE = direct("murder_run.game.trap.ice_spirit.lore");
  NullComponent<Sender> MIND_CONTROL_TRAP_LORE = direct("murder_run.game.trap.mind_control.lore");
  NullComponent<Sender> JEB_TRAP_LORE = direct("murder_run.game.trap.jeb.lore");
  NullComponent<Sender> BUSH_TRAP_LORE = direct("murder_run.game.trap.bush.lore");
  NullComponent<Sender> FORTNITE_BUILDING_TRAP_LORE = direct("murder_run.game.trap.fortnite_building.lore");
  NullComponent<Sender> JACK_JACK_LASER_EYES_TRAP_LORE = direct("murder_run.game.trap.jack_jack_laser_eyes.lore");
  NullComponent<Sender> VIOLET_FORCE_FIELD_RIFT_TRAP_LORE = direct("murder_run.game.trap.violet_force_field_rift.lore");
  NullComponent<Sender> KILLER_TRACKER_TRAP_LORE = direct("murder_run.game.trap.killer_tracker.lore");
  NullComponent<Sender> FLASHLIGHT_TRAP_LORE = direct("murder_run.game.trap.flashlight.lore");
  NullComponent<Sender> LAUNCH_PAD_TRAP_LORE = direct("murder_run.game.trap.launch_pad.lore");
  NullComponent<Sender> IMPULSE_GRENADE_TRAP_LORE = direct("murder_run.game.trap.impulse_grenade.lore");
  NullComponent<Sender> ZARYA_GRAVITRON_SURGE_TRAP_LORE = direct("murder_run.game.trap.zarya_gravitron_surge.lore");
  NullComponent<Sender> LUCIO_CRANK_IT_UP_TRAP_LORE = direct("murder_run.game.trap.lucio_crank_it_up.lore");
  NullComponent<Sender> SHOCKWAVE_TRAP_LORE = direct("murder_run.game.trap.shockwave.lore");
  NullComponent<Sender> PARASITE_TRAP_LORE = direct("murder_run.game.trap.parasite.lore");
  NullComponent<Sender> PORTA_FORT_TRAP_LORE = direct("murder_run.game.trap.porta_fort.lore");
  NullComponent<Sender> DISTORTER_TRAP_LORE = direct("murder_run.game.trap.distorter.lore");
  NullComponent<Sender> CLICKBAIT_TRAP_LORE = direct("murder_run.game.trap.clickbait.lore");
  NullComponent<Sender> DEMONETIZED_TRAP_LORE = direct("murder_run.game.trap.demonetized.lore");
  NullComponent<Sender> JETPACK_TRAP_LORE = direct("murder_run.game.trap.jetpack.lore");

  NullComponent<Sender> GLOW_TRAP_ACTIVATE = direct("murder_run.game.trap.glow.activate");
  NullComponent<Sender> HACK_TRAP_ACTIVATE = direct("murder_run.game.trap.hack.activate");
  NullComponent<Sender> PORTAL_TRAP_ACTIVATE = direct("murder_run.game.trap.portal.activate");
  NullComponent<Sender> SPASM_TRAP_ACTIVATE = direct("murder_run.game.trap.spasm.activate");
  NullComponent<Sender> RESURRECTION_STONE_TRAP_ACTIVATE = direct("murder_run.game.trap.resurrection_stone.activate");
  NullComponent<Sender> GHOSTING_TRAP_ACTIVATE = direct("murder_run.game.trap.ghosting.activate");
  NullComponent<Sender> DISTORT_TRAP_ACTIVATE = direct("murder_run.game.trap.distort.activate");
  NullComponent<Sender> HORCRUX_TRAP_ACTIVATE = direct("murder_run.game.trap.horcrux.activate");
  NullComponent<Sender> RETALIATION_TRAP_ACTIVATE = direct("murder_run.game.trap.retaliation.activate");
  NullComponent<Sender> JUMP_SCARE_TRAP_ACTIVATE = direct("murder_run.game.trap.jump_scare.activate");
  NullComponent<Sender> SMOKE_TRAP_ACTIVATE = direct("murder_run.game.trap.smoke.activate");
  NullComponent<Sender> LEVITATION_TRAP_ACTIVATE = direct("murder_run.game.trap.levitation.activate");
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
  NullComponent<Sender> RANDOM_TRAP_ACTIVATE = direct("murder_run.game.trap.random.activate");
  NullComponent<Sender> MAGNET_MODE_TRAP_ACTIVATE = direct("murder_run.game.trap.magnet_mode.activate");
  NullComponent<Sender> TAGLOCK_NEEDLE_TRAP_ACTIVATE = direct("murder_run.game.trap.taglock_needle.activate");
  NullComponent<Sender> CLOAK_TRAP_ACTIVATE = direct("murder_run.game.trap.cloak.activate");
  NullComponent<Sender> SIXTH_SENSE_TRAP_ACTIVATE = direct("murder_run.game.trap.sixth_sense.activate");
  NullComponent<Sender> BLAST_OFF_TRAP_ACTIVATE = direct("murder_run.game.trap.blast_off.activate");
  NullComponent<Sender> TRAP_SNIFFER_TRAP_ACTIVATE = direct("murder_run.game.trap.trap_sniffer.activate");
  NullComponent<Sender> SOME_CAKE_IN_VEGAS_TRAP_ACTIVATE = direct("murder_run.game.trap.some_cake_in_vegas.activate");
  NullComponent<Sender> MIND_CONTROL_TRAP_ACTIVATE = direct("murder_run.game.trap.mind_control.activate");
  NullComponent<Sender> JEB_TRAP_ACTIVATE = direct("murder_run.game.trap.jeb.activate");
  NullComponent<Sender> VIOLET_FORCE_FIELD_RIFT_TRAP_ACTIVATE = direct("murder_run.game.trap.violet_force_field_rift.activate");
  NullComponent<Sender> ZARYA_GRAVITRON_SURGE_TRAP_ACTIVATE = direct("murder_run.game.trap.zarya_gravitron_surge.activate");
  NullComponent<Sender> SHOCKWAVE_TRAP_ACTIVATE = direct("murder_run.game.trap.shockwave.activate");
  NullComponent<Sender> DISTORTER_TRAP_ACTIVATE = direct("murder_run.game.trap.distorter.activate");
  NullComponent<Sender> CLICKBAIT_TRAP_ACTIVATE = direct("murder_run.game.trap.clickbait.activate");
  NullComponent<Sender> DEMONETIZED_TRAP_ACTIVATE = direct("murder_run.game.trap.demonetized.activate");
  NullComponent<Sender> MED_BOT_TRAP_DEACTIVATE = direct("murder_run.game.trap.med_bot.deactivate");
  NullComponent<Sender> PARASITE_TRAP_DEACTIVATE = direct("murder_run.game.trap.parasite.deactivate");
  NullComponent<Sender> DISTORTER_TRAP_DEACTIVATE = direct("murder_run.game.trap.distorter.deactivate");

}
