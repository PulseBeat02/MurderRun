/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.game.gadget.GadgetDataBundle;

public interface GameProperties {
  static void init() {
    // init bundle
  }

  GadgetDataBundle GADGET_DATA_BUNDLE = new GadgetDataBundle();

  int GAME_EXPIRATION_TIME = num("game.expiration_time");
  String PLAYER_LEAVE_COMMANDS_AFTER = str("player_leave_commands_after");
  int BLOCKS_PER_TICK = num("worldedit.blocks_per_tick");
  double MINIATURIZER_SCALE = dec("minitaturizer.scale");
  String MINIATURIZER_SOUND = str("minitaturizer.sound");
  int MINIATURIZER_COST = num("minitaturizer.cost");
  double EXPANDER_SCALE = dec("expander.scale");
  String EXPANDER_SOUND = str("expander.sound");
  int EXPANDER_COST = num("expander.cost");
  String KILLER_WIN_COMMANDS_AFTER = str("killer_win_commands_after");
  String SURVIVOR_WIN_COMMANDS_AFTER = str("survivor_win_commands_after");
  boolean FORCE_RESOURCEPACK = bool("force_resourcepack");
  String BUILT_IN_RESOURCES = str("built_in_resources");
  int GAME_TIME_LIMIT = num("game.time_limit");
  int DORMAGOGG_EFFECT_DURATION = num("dormagogg.effect.duration");
  int DORMAGOGG_DURATION = num("dormagogg.duration");
  String SHOP_GUI_SOUND = str("shop.gui.sound");
  String GAME_STARTING_SOUND = str("game.start_sound");
  String LOBBY_TIMER_SOUND = str("lobby.timer_sound");
  int LOBBY_STARTING_TIME = num("lobby.starting_time");
  int CAR_PARTS_COUNT = num("car_parts.count");
  String KILLER_CANNOT_BREAK = str("killer.cannot_break");
  int DEATH_HOUND_DESPAWN = num("death_hound.despawn");
  int BEGINNING_STARTING_TIME = num("survivor.starting_time");
  int SURVIVOR_STARTING_CURRENCY = num("survivor.starting_currency");
  int KILLER_STARTING_CURRENCY = num("killer.starting_currency");
  String DISABLED_GADGETS = str("disabled_gadgets");
  int BEAR_DURATION = num("bear.duration");
  String BEAR_SOUND = str("bear.sound");
  int BLIND_DURATION = num("blind.duration");
  String BLIND_SOUND = str("blind.sound");
  int BURROW_DURATION = num("burrow.duration");
  String BURROW_SOUND = str("burrow.sound");
  int CAGE_DURATION = num("cage.duration");
  String CAGE_SOUND = str("cage.sound");
  int DISTORT_DURATION = num("distort.duration");
  String DISTORT_SOUND = str("distort.sound");
  int FART_EFFECT_DURATION = num("fart.effect.duration");
  int FART_DURATION = num("fart.duration");
  int FIREWORK_DURATION = num("firework.duration");
  String FIREWORK_SOUND = str("firework.sound");
  int FREEZE_DURATION = num("freeze.duration");
  int FREEZE_EFFECT_DURATION = num("freeze.effect.duration");
  String FREEZE_SOUND = str("freeze.sound");
  int GHOST_DURATION = num("ghost.duration");
  String GHOST_SOUND = str("ghost.sound");
  int GLOW_DURATION = num("glow.duration");
  String GLOW_SOUND = str("glow.sound");
  int HACK_DURATION = num("hack.duration");
  String HACK_SOUND = str("hack.sound");
  int HAUNT_DURATION = num("haunt.duration");
  String HAUNT_SOUND = str("haunt.sound");
  int JEB_DURATION = num("jeb.duration");
  int JEB_SHEEP_COUNT = num("jeb.sheep.count");
  String JEB_SOUND = str("jeb.sound");
  int JUMP_SCARE_DURATION = num("jump_scare.duration");
  int JUMP_SCARE_EFFECT_DURATION = num("jump_scare.effect.duration");
  int LEVITATION_DURATION = num("levitation.duration");
  String LEVITATION_SOUND = str("levitation.sound");
  int NECK_SNAP_DURATION = num("neck_snap.duration");
  String NECK_SNAP_SOUND = str("neck_snap.sound");
  double PONY_HORSE_SPEED = dec("pony.speed");
  String PONY_SOUND = str("pony.sound");
  double SHOCKWAVE_EXPLOSION_RADIUS = dec("shockwave.explosion.radius");
  double SHOCKWAVE_EXPLOSION_POWER = dec("shockwave.explosion.power");
  String SHOCKWAVE_SOUND = str("shockwave.sound");
  int SMOKE_DURATION = num("smoke.duration");
  String SMOKE_SOUND = str("smoke.sound");
  int SPASM_DURATION = num("spasm.duration");
  String SPASM_SOUND = str("spasm.sound");
  String SPAWN_SOUND = str("spawn.sound");
  int STAR_DURATION = num("star.duration");
  String STAR_SOUND = str("star.sound");
  String BLASTOFF_SOUND = str("blastoff.sound");
  int BUSH_DURATION = num("bush.duration");
  String BUSH_SOUND = str("bush.sound");
  int CHIPPED_DURATION = num("chipped.duration");
  String CHIPPED_SOUND = str("chipped.sound");
  int CLOAK_DURATION = num("cloak.duration");
  String CLOAK_SOUND = str("cloak.sound");
  String CORPUS_WARP_SOUND = str("corpus_warp.sound");
  int CRYO_FREEZE_RADIUS = num("cryo_freeze.radius");
  String CRYO_FREEZE_SOUND = str("cryo_freeze.sound");
  int DEADRINGER_DURATION = num("deadringer.duration");
  String DEADRINGER_SOUND = str("deadringer.sound");
  String DECOY_SOUND = str("decoy.sound");
  double DISTORTER_DESTROY_RADIUS = dec("distorter.destroy.radius");
  double DISTORTER_EFFECT_RADIUS = dec("distorter.effect.radius");
  String DISTORTER_SOUND = str("distorter.sound");
  int DRONE_DURATION = num("drone.duration");
  String DRONE_SOUND = str("drone.sound");
  double FLASHBANG_RADIUS = dec("flashbang.radius");
  int FLASHBANG_DURATION = num("flashbang.duration");
  double FLASHLIGHT_CONE_ANGLE = dec("flashlight.cone.angle");
  double FLASHLIGHT_CONE_LENGTH = dec("flashlight.cone.length");
  double FLASHLIGHT_RADIUS = dec("flashlight.radius");
  String FRIEND_WARP_SOUND = str("friend_warp.sound");
  int GHOSTING_WOOL_DELAY = num("ghosting.wool.delay");
  String GHOSTING_SOUND = str("ghosting.sound");
  String HORCRUX_SOUND = str("horcrux.sound");
  int ICE_SKATIN_DURATION = num("ice_skatin.duration");
  String ICE_SKATIN_SOUND = str("ice_skatin.sound");
  int ICE_SPIRIT_DURATION = num("ice_spirit.duration");
  String ICE_SPIRIT_SOUND = str("ice_spirit.sound");
  int KILLER_REWIND_COOLDOWN = num("killer_rewind.cooldown");
  String KILLER_REWIND_SOUND = str("killer_rewind.sound");
  int KILLER_TRACKER_USES = num("killer_tracker.uses");
  String KILLER_TRACKER_SOUND = str("killer_tracker.sound");
  double LIFE_INSURANCE_RADIUS = dec("life_insurance.radius");
  String LIFE_INSURANCE_SOUND = str("life_insurance.sound");
  int MAGNET_MODE_MULTIPLIER = num("magnet_mode.multiplier");
  String MAGNET_MODE_SOUND = str("magnet_mode.sound");
  double MED_BOT_RADIUS = dec("med_bot.radius");
  double MED_BOT_DESTROY_RADIUS = dec("med_bot.destroy.radius");
  String MED_BOT_SOUND = str("med_bot.sound");
  int MIND_CONTROL_DURATION = num("mind_control.duration");
  String MIND_CONTROL_SOUND = str("mind_control.sound");
  double PARASITE_DESTROY_RADIUS = dec("parasite.destroy.radius");
  double PARASITE_RADIUS = dec("parasite.radius");
  String PARASITE_SOUND = str("parasite.sound");
  String RANDOM_TELEPORT_SOUND = str("random_teleport.sound");
  String RESURRECTION_STONE_SOUND = str("resurrection_stone.sound");
  int RETALIATION_MAX_AMPLIFIER = num("retaliation.max_amplifier");
  String RETALIATION_SOUND = str("retaliation.sound");
  int REWIND_COOLDOWN = num("rewind.cooldown");
  double SIXTH_SENSE_RADIUS = dec("sixth_sense.radius");
  String SIXTH_SENSE_SOUND = str("sixth_sense.sound");
  double SMOKE_GRENADE_RADIUS = dec("smoke_grenade.radius");
  int SMOKE_GRENADE_DURATION = num("smoke_grenade.duration");
  String SUPPLY_DROP_MASKS = str("supply_drop.masks");
  double TRACKER_RADIUS = dec("tracker.radius");
  String TRACKER_SOUND = str("tracker.sound");
  String TRANSLOCATOR_SOUND = str("translocator.sound");
  double PART_SNIFFER_RADIUS = dec("part_sniffer.radius");
  String PART_SNIFFER_SOUND = str("part_sniffer.sound");
  String TRAP_VEST_SOUND = str("trap_vest.sound");
  String ALL_SEEING_EYE_SOUND = str("all_seeing_eye.sound");
  int ALL_SEEING_EYE_DURATION = num("all_seeing_eye.duration");
  String BLOOD_CURSE_SOUND = str("blood_curse.sound");
  double BURN_THE_BODY_RADIUS = dec("burn_the_body.radius");
  String BURN_THE_BODY_SOUND = str("burn_the_body.sound");
  double CURSED_NOTE_RADIUS = dec("cursed_note.radius");
  double CURSED_NOTE_EFFECT_RADIUS = dec("cursed_note.effect.radius");
  String CURSED_NOTE_SOUND = str("cursed_note.sound");
  String DEATH_HOUND_SOUND = str("death_hound.sound");
  String DEATH_STEED_SOUND = str("death_steed.sound");
  String DORMAGOGG_SOUND = str("dormagogg.sound");
  int EAGLE_EYE_DURATION = num("eagle_eye.duration");
  String EAGLE_EYE_SOUND = str("eagle_eye.sound");
  int EMP_BLAST_DURATION = num("emp_blast.duration");
  String ENDER_SHADOWS_SOUND = str("ender_shadows.sound");
  String FAKE_PART_SOUND = str("fake_part.sound");
  String FAKE_PART_EFFECT_SOUND = str("fake_part.effect.sound");
  double FAKE_PART_RADIUS = dec("fake_part.radius");
  int FAKE_PART_DURATION = num("fake_part.duration");
  String FIRE_TRAIL_SOUND = str("fire_trail.sound");
  String FLOOR_IS_LAVA_SOUND = str("floor_is_lava.sound");
  String FOREWARN_SOUND = str("forewarn.sound");
  int FRIGHT_DURATION = num("fright.duration");
  String GAMBLE_SOUND = str("gamble.sound");
  String HEALTH_CUT_SOUND = str("health_cut.sound");
  String HEAT_SEEKER_SOUND = str("heat_seeker.sound");
  double HEAT_SEEKER_RADIUS = dec("heat_seeker.radius");
  String ICE_PATH_SOUND = str("ice_path.sound");
  String INFRARED_VISION_SOUND = str("infrared_vision.sound");
  int INFRARED_VISION_DURATION = num("infrared_vision.duration");
  String MURDEROUS_WARP_SOUND = str("murderous_warp.sound");
  String PART_WARP_SOUND = str("part_warp.sound");
  String PHANTOM_SOUND = str("phantom.sound");
  int PHANTOM_DURATION = num("phantom.duration");
  String PLAYER_TRACKER_SOUND = str("player_tracker.sound");
  int PLAYER_TRACKER_USES = num("player_tracker.uses");
  String POISON_SMOG_SOUND = str("poison_smog.sound");
  double POISON_SMOG_RADIUS = dec("poison_smog.radius");
  int POISON_SMOG_DURATION = num("poison_smog.duration");
  String QUICK_BOMB_SOUND = str("quick_bomb.sound");
  int QUICK_BOMB_TICKS = num("quick_bomb_ticks");
  double QUICK_BOMB_DAMAGE = dec("quick_bomb_damage");
  String RED_ARROW_SOUND = str("red_arrow.sound");
  int RED_ARROW_DURATION = num("red_arrow.duration");
  String TRAP_SEEKER_SOUND = str("trap_seeker.sound");
  double TRAP_SEEKER_RADIUS = dec("trap_seeker.radius");
  String TRAP_WRECKER_SOUND = str("trap_wrecker.sound");
  int TRAP_WRECKER_DURATION = num("trap_wrecker.duration");
  String WARP_DISTORT_SOUND = str("warp_distort.sound");
  String CAMERA_SOUND = str("camera.sound");
  int HOOK_COST = num("hook.cost");
  int ALL_SEEING_EYE_COST = num("all_seeing_eye.cost");
  int BLOOD_CURSE_COST = num("blood_curse.cost");
  int BURN_THE_BODY_COST = num("burn_the_body.cost");
  int KILLER_CAMERA_COST = num("killer_camera.cost");
  int CAMERA_COST = num("camera.cost");
  int CORRUPTION_COST = num("corruption.cost");
  int CURSED_NOTE_COST = num("cursed_note.cost");
  int DEATH_HOUND_COST = num("death_hound.cost");
  int DEATH_STEED_COST = num("death_steed.cost");
  int DORMAGOGG_COST = num("dormagogg.cost");
  int EAGLE_EYE_COST = num("eagle_eye.cost");
  int EMP_BLAST_COST = num("emp_blast.cost");
  int ENDER_SHADOWS_COST = num("ender_shadows.cost");
  int FAKE_PART_COST = num("fake_part.cost");
  int FIRE_TRAIL_COST = num("fire_trail.cost");
  int FLOOR_IS_LAVA_COST = num("floor_is_lava.cost");
  int FOREWARN_COST = num("forewarn.cost");
  int FRIGHT_COST = num("fright.cost");
  int GAMBLE_COST = num("gamble.cost");
  int HEALTH_CUT_COST = num("health_cut.cost");
  int HEAT_SEEKER_COST = num("heat_seeker.cost");
  int ICE_PATH_COST = num("ice_path.cost");
  int INFRARED_VISION_COST = num("infrared_vision.cost");
  int MURDEROUS_WARP_COST = num("murderous_warp.cost");
  int PART_WARP_COST = num("part_warp.cost");
  int PHANTOM_COST = num("phantom.cost");
  int PLAYER_TRACKER_COST = num("player_tracker.cost");
  int POISON_SMOG_COST = num("poison_smog.cost");
  int PORTAL_GUN_COST = num("portal_gun.cost");
  int QUICK_BOMB_COST = num("quick_bomb.cost");
  int RED_ARROW_COST = num("red_arrow.cost");
  int TRAP_SEEKER_COST = num("trap_seeker.cost");
  int TRAP_WRECKER_COST = num("trap_wrecker.cost");
  int WARP_DISTORT_COST = num("warp_distort.cost");
  int SURVIVOR_GEAR_COST = num("survivor_gear.cost");
  int EXCAVATOR_COST = num("excavator.cost");
  int SHIELD_COST = num("shield.cost");
  int BEAR_COST = num("bear.cost");
  int BLIND_COST = num("blind.cost");
  int BURROW_COST = num("burrow.cost");
  int CAGE_COST = num("cage.cost");
  int DISTORT_COST = num("distort.cost");
  int FART_COST = num("fart.cost");
  int FIREWORK_COST = num("firework.cost");
  int FREEZE_COST = num("freeze.cost");
  int GHOST_COST = num("ghost.cost");
  int GLOW_COST = num("glow.cost");
  int HACK_COST = num("hack.cost");
  int HAUNT_COST = num("haunt.cost");
  int JEB_COST = num("jeb.cost");
  int JUMP_SCARE_COST = num("jump_scare.cost");
  int LEVITATION_COST = num("levitation.cost");
  int NECK_SNAP_COST = num("neck_snap.cost");
  int PONY_COST = num("pony.cost");
  int SHOCKWAVE_COST = num("shockwave.cost");
  int SMOKE_COST = num("smoke.cost");
  int SPASM_COST = num("spasm.cost");
  int SPAWN_COST = num("spawn.cost");
  int STAR_COST = num("star.cost");
  int BLAST_OFF_COST = num("blast_off.cost");
  int BUSH_COST = num("bush.cost");
  int CHIPPED_COST = num("chipped.cost");
  int CLOAK_COST = num("cloak.cost");
  int CORPUS_WARP_COST = num("corpus_warp.cost");
  int CRYO_FREEZE_COST = num("cryo_freeze.cost");
  int DEADRINGER_COST = num("deadringer.cost");
  int DECOY_COST = num("decoy.cost");
  int DISTORTER_COST = num("distorter.cost");
  int DRONE_COST = num("drone.cost");
  int FLASHBANG_COST = num("flashbang.cost");
  int FLASHLIGHT_COST = num("flashlight.cost");
  int FRIEND_WARP_COST = num("friend_warp.cost");
  int GHOSTING_COST = num("ghosting.cost");
  int HORCRUX_COST = num("horcrux.cost");
  int ICE_SKATIN_COST = num("ice_skatin.cost");
  int ICE_SPIRIT_COST = num("ice_spirit.cost");
  int KILLER_REWIND_COST = num("killer_rewind.cost");
  int KILLER_TRACKER_COST = num("killer_tracker.cost");
  int LIFE_INSURANCE_COST = num("life_insurance.cost");
  int MAGNET_MODE_COST = num("magnet_mode.cost");
  int MED_BOT_COST = num("med_bot.cost");
  int MED_KIT_COST = num("med_kit.cost");
  int MIND_CONTROL_COST = num("mind_control.cost");
  int PARASITE_COST = num("parasite.cost");
  int PORTAL_TRAP_COST = num("portal_trap.cost");
  int RANDOM_TELEPORT_COST = num("random_teleport.cost");
  int RANDOM_TRAP_COST = num("random_trap.cost");
  int RESURRECTION_STONE_COST = num("resurrection_stone.cost");
  int RETALIATION_COST = num("retaliation.cost");
  int REWIND_COST = num("rewind.cost");
  int SIXTH_SENSE_COST = num("sixth_sense.cost");
  int SMOKE_GRENADE_COST = num("smoke_grenade.cost");
  int SPEED_PENDANT_COST = num("speed_pendant.cost");
  int SUPPLY_DROP_COST = num("supply_drop.cost");
  int TRACKER_COST = num("tracker.cost");
  int TRANSLOCATOR_COST = num("translocator.cost");
  int PART_SNIFFER_COST = num("part_sniffer.cost");
  int TRAP_VEST_COST = num("trap_vest.cost");

  private static boolean bool(final String key) {
    return GADGET_DATA_BUNDLE.getBoolean(key);
  }

  private static int num(final String key) {
    return GADGET_DATA_BUNDLE.getInt(key);
  }

  private static double dec(final String key) {
    return GADGET_DATA_BUNDLE.getDouble(key);
  }

  private static String str(final String key) {
    return GADGET_DATA_BUNDLE.getString(key);
  }
}
