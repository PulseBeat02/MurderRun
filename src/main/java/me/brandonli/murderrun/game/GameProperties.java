/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game;

import static java.util.Objects.requireNonNull;

import java.awt.*;
import org.bukkit.Material;
import org.intellij.lang.annotations.Subst;

public final class GameProperties {

  public static final GameProperties DEFAULT = new GameProperties(GameMode.DEFAULT);
  public static final GameProperties ONE_BOUNCE = new GameProperties(GameMode.ONE_BOUNCE);
  public static final GameProperties FREEZE_TAG = new GameProperties(GameMode.FREEZE_TAG);
  public static final GameProperties COMMON = new GameProperties("common"); // for others

  public static void init() {
    // init bundle
  }

  private final GameBundle bundle;

  GameProperties(final GameMode mode) {
    final String name = mode.getModeName();
    this(name);
  }

  GameProperties(final String name) {
    this.bundle = new GameBundle(name);
  }

  public boolean isRandomEventsEnabled() {
    return this.bool("game.random_events.enabled");
  }

  public int getFreezeTagReviveRadius() {
    return this.num("freeze_tag.revive.radius");
  }

  public int getFreezeTagReviveTimer() {
    return this.num("freeze_tag.revive.timer");
  }

  public int getFreezeTagSurvivorLives() {
    return this.num("freeze_tag.lives");
  }

  public String getKillerNearSound() {
    return this.str("killer.near_sound");
  }

  public int getFreezeTagRevivalTime() {
    return this.num("freeze_tag.revive.time");
  }

  public String getCraftEngineCurrency() {
    return this.str("craftengine.currency");
  }

  public String getCraftEngineGhostBone() {
    return this.str("craftengine.ghost.bone");
  }

  public String getCraftEngineKillerArrow() {
    return this.str("craftengine.killer.arrow");
  }

  public String getCraftEngineKillerSword() {
    return this.str("craftengine.killer.sword");
  }

  public String getCraftEngineKillerHelmet() {
    return this.str("craftengine.killer.helmet");
  }

  public String getCraftEngineKillerChestplate() {
    return this.str("craftengine.killer.chestplate");
  }

  public String getCraftEngineKillerLeggings() {
    return this.str("craftengine.killer.leggings");
  }

  public String getCraftEngineKillerBoots() {
    return this.str("craftengine.killer.boots");
  }

  public String getCraftEngineSurvivorHelmet() {
    return this.str("craftengine.survivor.helmet");
  }

  public String getCraftEngineSurvivorChestplate() {
    return this.str("craftengine.survivor.chestplate");
  }

  public String getCraftEngineSurvivorLeggings() {
    return this.str("craftengine.survivor.leggings");
  }

  public String getCraftEngineSurvivorBoots() {
    return this.str("craftengine.survivor.boots");
  }

  public String getRandomTeleportBlacklistedBlocks() {
    return this.str("random_teleport.blacklisted_blocks");
  }

  public double getVaultReward() {
    return this.dec("vault.reward");
  }

  public double getMoonGravity() {
    return this.dec("moon.gravity");
  }

  public int getMoonDuration() {
    return this.num("moon.duration");
  }

  public String getMoonSound() {
    return this.str("moon.sound");
  }

  public int getMoonCost() {
    return this.num("moon.cost");
  }

  public Material getMoonMaterial() {
    return this.mat("moon.material");
  }

  public double getReachDistance() {
    return this.dec("reach.distance");
  }

  public int getEtherwarpMaxDistance() {
    return this.num("etherwarp.max");
  }

  public double getEtherwarpCooldown() {
    return this.dec("etherwarp.cooldown");
  }

  public String getGuardianAngelTextureSignature() {
    return this.str("guardian_angel.texture.signature");
  }

  public String getGuardianAngelTextureData() {
    return this.str("guardian_angel.texture.data");
  }

  public String getGrimReaperTextureSignature() {
    return this.str("grim_reaper.texture.signature");
  }

  public String getGrimReaperTextureData() {
    return this.str("grim_reaper.texture.data");
  }

  public String getAngelSpiritTextureSignature() {
    return this.str("angel_spirit.texture.signature");
  }

  public String getAngelSpiritTextureData() {
    return this.str("angel_spirit.texture.data");
  }

  public String getWeepingAngelTextureSignature() {
    return this.str("weeping_angel.texture.signature");
  }

  public String getWeepingAngelTextureData() {
    return this.str("weeping_angel.texture.data");
  }

  public boolean getGameUtilitiesRandom() {
    return this.bool("game.utilities.random");
  }

  public int getGameUtilitiesKillerGadgets() {
    return this.num("game.utilities.killer.gadgets");
  }

  public int getGameUtilitiesSurvivorGadgets() {
    return this.num("game.utilities.survivor.gadgets");
  }

  public int getGameUtilitiesKillerAbilities() {
    return this.num("game.utilities.killer.abilities");
  }

  public int getGameUtilitiesSurvivorAbilities() {
    return this.num("game.utilities.survivor.abilities");
  }

  public String getPhaseBlacklistedBlocks() {
    return this.str("phase.blacklisted_blocks");
  }

  public double getPhaseDistance() {
    return this.dec("phase.distance");
  }

  public double getPhaseCooldown() {
    return this.dec("phase.cooldown");
  }

  public double getKillerSprintTime() {
    return this.dec("killer.sprint_time");
  }

  public double getSurvivorSprintTime() {
    return this.dec("survivor.sprint_time");
  }

  public double getTrapVestVelocity() {
    return this.dec("trap_vest.velocity");
  }

  public double getSonicBoomKnockback() {
    return this.dec("sonicboom.knockback");
  }

  public double getSonicBoomRadius() {
    return this.dec("sonicboom.radius");
  }

  public double getSonicBoomCooldown() {
    return this.dec("sonicboom.cooldown");
  }

  public double getCannonVelocity() {
    return this.dec("cannon.velocity");
  }

  public int getCannonFuse() {
    return this.num("cannon.fuse");
  }

  public double getCannonCooldown() {
    return this.dec("cannon.cooldown");
  }

  public int getAbsorptionLevel() {
    return this.num("absorption.level");
  }

  public String getAbilityGuiSound() {
    return this.str("select.gui.sound");
  }

  public String getDisabledAbilities() {
    return this.str("disabled_abilities");
  }

  public double getDoubleJumpCooldown() {
    return this.dec("doublejump.cooldown");
  }

  public double getDoubleJumpVelocity() {
    return this.dec("doublejump.velocity");
  }

  public double getKillerParticleRadius() {
    return this.dec("killer.particles.radius");
  }

  public int getFlashlightDuration() {
    return this.num("flashlight.duration");
  }

  public double getFlashlightCooldown() {
    return this.dec("flashlight.cooldown");
  }

  public Material getTranslocatorMaterial() {
    return this.mat("translocator.material");
  }

  public Material getTrackerMaterial() {
    return this.mat("tracker.material");
  }

  public Material getSupplyDropMaterial() {
    return this.mat("supply_drop.material");
  }

  public Material getSpeedPendantMaterial() {
    return this.mat("speed_pendant.material");
  }

  public Material getSmokeGrenadeMaterial() {
    return this.mat("smoke_grenade.material");
  }

  public Material getSixthSenseMaterial() {
    return this.mat("sixth_sense.material");
  }

  public Material getRewindMaterial() {
    return this.mat("rewind.material");
  }

  public Material getRetaliationMaterial() {
    return this.mat("retaliation.material");
  }

  public Material getResurrectionStoneMaterial() {
    return this.mat("resurrection_stone.material");
  }

  public Material getRandomTrapMaterial() {
    return this.mat("random_trap.material");
  }

  public Material getRandomTeleportMaterial() {
    return this.mat("random_teleport.material");
  }

  public Material getPortalTrapMaterial() {
    return this.mat("portal_trap.material");
  }

  public Material getParsiteMaterial() {
    return this.mat("parasite.material");
  }

  public Material getMiniturizerMaterial() {
    return this.mat("minitaturizer.material");
  }

  public Material getMindControlMaterial() {
    return this.mat("mind_control.material");
  }

  public Material getMedKitMaterial() {
    return this.mat("med_kit.material");
  }

  public Material getMedBotMaterial() {
    return this.mat("med_bot.material");
  }

  public Material getMagnetModeMaterial() {
    return this.mat("magnet_mode.material");
  }

  public Material getLifeInsuranceMaterial() {
    return this.mat("life_insurance.material");
  }

  public Material getKillerRewindMaterial() {
    return this.mat("killer_rewind.material");
  }

  public Material getIceSpiritMaterial() {
    return this.mat("ice_spirit.material");
  }

  public Material getIceSkatinMaterial() {
    return this.mat("ice_skatin.material");
  }

  public Material getHorcruxMaterial() {
    return this.mat("horcrux.material");
  }

  public Material getFriendWarpMaterial() {
    return this.mat("friend_warp.material");
  }

  public Material getFlashbangMaterial() {
    return this.mat("flashbang.material");
  }

  public Material getDroneMaterial() {
    return this.mat("drone.material");
  }

  public Material getDistorterMaterial() {
    return this.mat("distorter.material");
  }

  public Material getDecoyMaterial() {
    return this.mat("decoy.material");
  }

  public Material getDeadringerMaterial() {
    return this.mat("deadringer.material");
  }

  public Material getCryoFreezeMaterial() {
    return this.mat("cryo_freeze.material");
  }

  public Material getCorpusWarpMaterial() {
    return this.mat("corpus_warp.material");
  }

  public Material getCloakMaterial() {
    return this.mat("cloak.material");
  }

  public Material getChippedMaterial() {
    return this.mat("chipped.material");
  }

  public Material getCameraMaterial() {
    return this.mat("camera.material");
  }

  public Material getBushMaterial() {
    return this.mat("bush.material");
  }

  public Material getBlastOffMaterial() {
    return this.mat("blastoff.material");
  }

  public Material getStarMaterial() {
    return this.mat("star.material");
  }

  public Material getSpawnMaterial() {
    return this.mat("spawn.material");
  }

  public Material getSpasmMaterial() {
    return this.mat("spasm.material");
  }

  public Material getSmokeMaterial() {
    return this.mat("smoke.material");
  }

  public Material getShockwaveMaterial() {
    return this.mat("shockwave.material");
  }

  public Material getPonyMaterial() {
    return this.mat("pony.material");
  }

  public Material getNeckSnapMaterial() {
    return this.mat("neck_snap.material");
  }

  public Material getLevitationMaterial() {
    return this.mat("levitation.material");
  }

  public Material getJumpScareMaterial() {
    return this.mat("jump_scare.material");
  }

  public Material getJebMaterial() {
    return this.mat("jeb.material");
  }

  public Material getHauntMaterial() {
    return this.mat("haunt.material");
  }

  public Material getHackMaterial() {
    return this.mat("hack.material");
  }

  public Material getGlowMaterial() {
    return this.mat("glow.material");
  }

  public Material getGhostMaterial() {
    return this.mat("ghost.material");
  }

  public Material getFreezeMaterial() {
    return this.mat("freeze.material");
  }

  public Material getFireworkMaterial() {
    return this.mat("firework.material");
  }

  public Material getFartMaterial() {
    return this.mat("fart.material");
  }

  public Material getDistortMaterial() {
    return this.mat("distort.material");
  }

  public Material getCageMaterial() {
    return this.mat("cage.material");
  }

  public Material getBurrowMaterial() {
    return this.mat("burrow.material");
  }

  public Material getBlindMaterial() {
    return this.mat("blind.material");
  }

  public Material getBearMaterial() {
    return this.mat("bear.material");
  }

  public Material getShieldMaterial() {
    return this.mat("shield.material");
  }

  public Material getKillerTrackerMaterial() {
    return this.mat("killer_tracker.material");
  }

  public Material getFlashlightMaterial() {
    return this.mat("flashlight.material");
  }

  public Material getExcavatorMaterial() {
    return this.mat("excavator.material");
  }

  public Material getSurvivorLeggingsMaterial() {
    return this.mat("survivor_leggings.material");
  }

  public Material getSurvivorChestplateMaterial() {
    return this.mat("survivor_chestplate.material");
  }

  public Material getSurvivorHelmetMaterial() {
    return this.mat("survivor_helmet.material");
  }

  public Material getSurvivorBootsMaterial() {
    return this.mat("survivor_boots.material");
  }

  public Material getWarpDistortMaterial() {
    return this.mat("warp_distort.material");
  }

  public Material getTrapWreckerMaterial() {
    return this.mat("trap_wrecker.material");
  }

  public Material getTrapSeekerMaterial() {
    return this.mat("trap_seeker.material");
  }

  public Material getRedArrowMaterial() {
    return this.mat("red_arrow.material");
  }

  public Material getQuickBombMaterial() {
    return this.mat("quick_bomb.material");
  }

  public Material getPosionSmogMaterial() {
    return this.mat("poison_smog.material");
  }

  public Material getPhantomMaterial() {
    return this.mat("phantom.material");
  }

  public Material getPartWarpMaterial() {
    return this.mat("part_warp.material");
  }

  public Material getMurderousWarpMaterial() {
    return this.mat("murderous_warp.material");
  }

  public Material getMimicMaterial() {
    return this.mat("mimic.material");
  }

  public Material getInfraredVisionMaterial() {
    return this.mat("infrared_vision.material");
  }

  public Material getIcePathMaterial() {
    return this.mat("ice_path.material");
  }

  public Material getHeatSeekerMaterial() {
    return this.mat("heat_seeker.material");
  }

  public Material getHealthCutMaterial() {
    return this.mat("health_cut.material");
  }

  public Material getFrightMaterial() {
    return this.mat("fright.material");
  }

  public Material getForewarnMaterial() {
    return this.mat("forewarn.material");
  }

  public Material getFloorIsLavaMaterial() {
    return this.mat("floor_is_lava.material");
  }

  public Material getFireTrailMaterial() {
    return this.mat("fire_trail.material");
  }

  public Material getFakePartMaterial() {
    return this.mat("fake_part.material");
  }

  public Material getExpanderMaterial() {
    return this.mat("expander.material");
  }

  public Material getEnderShadowsMaterial() {
    return this.mat("ender_shadows.material");
  }

  public Material getEmpBlastMaterial() {
    return this.mat("emp_blast.material");
  }

  public Material getEagleEyeMaterial() {
    return this.mat("eagle_eye.material");
  }

  public Material getDormagoggMaterial() {
    return this.mat("dormagogg.material");
  }

  public Material getDeathSteedMaterial() {
    return this.mat("death_steed.material");
  }

  public Material getDeathHoundMaterial() {
    return this.mat("death_hound.material");
  }

  public Material getCursedNoteMaterial() {
    return this.mat("cursed_note.material");
  }

  public Material getCorruptionMaterial() {
    return this.mat("corruption.material");
  }

  public Material getKillerCameraMaterial() {
    return this.mat("killer_camera.material");
  }

  public Material getBurnTheBodyMaterial() {
    return this.mat("burn_the_body.material");
  }

  public Material getBloodCurseMaterial() {
    return this.mat("blood_curse.material");
  }

  public Material getAllSeeingEyeMaterial() {
    return this.mat("all_seeing_eye.material");
  }

  public Material getPortalGunMaterial() {
    return this.mat("portal_gun.material");
  }

  public Material getPlayerTrackerMaterial() {
    return this.mat("player_tracker.material");
  }

  public Material getHookMaterial() {
    return this.mat("hook.material");
  }

  public Color getStarColor() {
    return this.rgb("star.color");
  }

  public Color getSpawnColor() {
    return this.rgb("spawn.color");
  }

  public Color getSpasmColor() {
    return this.rgb("spasm.color");
  }

  public Color getSmokeColor() {
    return this.rgb("smoke.color");
  }

  public Color getShockwaveColor() {
    return this.rgb("shockwave.color");
  }

  public Color getPonyColor() {
    return this.rgb("pony.color");
  }

  public Color getNeckSnapColor() {
    return this.rgb("neck_snap.color");
  }

  public Color getLevitationColor() {
    return this.rgb("levitation.color");
  }

  public Color getJumpScareColor() {
    return this.rgb("jump_scare.color");
  }

  public Color getJebColor() {
    return this.rgb("jeb.color");
  }

  public Color getHauntColor() {
    return this.rgb("haunt.color");
  }

  public Color getHackColor() {
    return this.rgb("hack.color");
  }

  public Color getGlowColor() {
    return this.rgb("glow.color");
  }

  public Color getGhostColor() {
    return this.rgb("ghost.color");
  }

  public Color getFreezeColor() {
    return this.rgb("freeze.color");
  }

  public Color getFireworkColor() {
    return this.rgb("firework.color");
  }

  public Color getFartColor() {
    return this.rgb("fart.color");
  }

  public Color getDistortColor() {
    return this.rgb("distort.color");
  }

  public Color getCageColor() {
    return this.rgb("cage.color");
  }

  public Color getBurrowColor() {
    return this.rgb("burrow.color");
  }

  public Color getBlindColor() {
    return this.rgb("blind.color");
  }

  public Color getBearColor() {
    return this.rgb("bear.color");
  }

  public String getNexoCurrency() {
    return this.str("nexo.currency");
  }

  public String getNexoGhostBone() {
    return this.str("nexo.ghost.bone");
  }

  public String getNexoKillerArrow() {
    return this.str("nexo.killer.arrow");
  }

  public int getMimicCost() {
    return this.num("mimic.cost");
  }

  public String getNexoKillerSword() {
    return this.str("nexo.killer.sword");
  }

  public String getNexoKillerHelmet() {
    return this.str("nexo.killer.helmet");
  }

  public String getNexoKillerChestplate() {
    return this.str("nexo.killer.chestplate");
  }

  public String getNexoKillerLeggings() {
    return this.str("nexo.killer.leggings");
  }

  public String getNexoKillerBoots() {
    return this.str("nexo.killer.boots");
  }

  public String getNexoSurvivorHelmet() {
    return this.str("nexo.survivor.helmet");
  }

  public String getNexoSurvivorChestplate() {
    return this.str("nexo.survivor.chestplate");
  }

  public String getNexoSurvivorLeggings() {
    return this.str("nexo.survivor.leggings");
  }

  public String getNexoSurvivorBoots() {
    return this.str("nexo.survivor.boots");
  }

  public int getCarPartsRequired() {
    return this.num("car_parts.required");
  }

  public double getCarPartTruckRadius() {
    return this.dec("car_parts.truck_radius");
  }

  public int getMiniaturizerDuration() {
    return this.num("minitaturizer.duration");
  }

  public int getExpanderDuration() {
    return this.num("expander.duration");
  }

  public int getWorldeditMaxChunksPerTick() {
    return this.num("worldedit.chunks-per-tick");
  }

  public int getGameExpirationTime() {
    return this.num("game.expiration_time");
  }

  public String getPlayerLeaveCommandsAfter() {
    return this.str("player_leave_commands_after");
  }

  public int getBlocksPerTick() {
    return this.num("worldedit.blocks_per_tick");
  }

  public double getMiniaturizerScale() {
    return this.dec("minitaturizer.scale");
  }

  public String getMiniaturizerSound() {
    return this.str("minitaturizer.sound");
  }

  public int getMiniaturizerCost() {
    return this.num("minitaturizer.cost");
  }

  public double getExpanderScale() {
    return this.dec("expander.scale");
  }

  public String getExpanderSound() {
    return this.str("expander.sound");
  }

  public int getExpanderCost() {
    return this.num("expander.cost");
  }

  public String getKillerWinCommandsAfter() {
    return this.str("killer_win_commands_after");
  }

  public String getSurvivorWinCommandsAfter() {
    return this.str("survivor_win_commands_after");
  }

  public boolean getForceResourcepack() {
    return this.bool("force_resourcepack");
  }

  public String getBuiltInResources() {
    return this.str("built_in_resources");
  }

  public int getGameTimeLimit() {
    return this.num("game.time_limit");
  }

  public int getDormagoggEffectDuration() {
    return this.num("dormagogg.effect.duration");
  }

  public int getDormagoggDuration() {
    return this.num("dormagogg.duration");
  }

  public String getShopGuiSound() {
    return this.str("shop.gui.sound");
  }

  public String getGameStartingSound() {
    return this.str("game.start_sound");
  }

  public String getLobbyTimerSound() {
    return this.str("lobby.timer_sound");
  }

  public int getLobbyStartingTime() {
    return this.num("lobby.starting_time");
  }

  public int getCarPartsCount() {
    return this.num("car_parts.count");
  }

  public String getKillerCannotBreak() {
    return this.str("killer.cannot_break");
  }

  public int getDeathHoundDespawn() {
    return this.num("death_hound.despawn");
  }

  public int getBeginningStartingTime() {
    return this.num("survivor.starting_time");
  }

  public int getSurvivorStartingCurrency() {
    return this.num("survivor.starting_currency");
  }

  public int getKillerStartingCurrency() {
    return this.num("killer.starting_currency");
  }

  public String getDisabledGadgets() {
    return this.str("disabled_gadgets");
  }

  public int getBearDuration() {
    return this.num("bear.duration");
  }

  public String getBearSound() {
    return this.str("bear.sound");
  }

  public int getBlindDuration() {
    return this.num("blind.duration");
  }

  public String getBlindSound() {
    return this.str("blind.sound");
  }

  public int getBurrowDuration() {
    return this.num("burrow.duration");
  }

  public String getBurrowSound() {
    return this.str("burrow.sound");
  }

  public int getCageDuration() {
    return this.num("cage.duration");
  }

  public String getCageSound() {
    return this.str("cage.sound");
  }

  public int getDistortDuration() {
    return this.num("distort.duration");
  }

  public String getDistortSound() {
    return this.str("distort.sound");
  }

  public int getFartEffectDuration() {
    return this.num("fart.effect.duration");
  }

  public int getFartDuration() {
    return this.num("fart.duration");
  }

  public int getFireworkDuration() {
    return this.num("firework.duration");
  }

  public String getFireworkSound() {
    return this.str("firework.sound");
  }

  public int getFreezeDuration() {
    return this.num("freeze.duration");
  }

  public int getFreezeEffectDuration() {
    return this.num("freeze.effect.duration");
  }

  public String getFreezeSound() {
    return this.str("freeze.sound");
  }

  public int getGhostDuration() {
    return this.num("ghost.duration");
  }

  public String getGhostSound() {
    return this.str("ghost.sound");
  }

  public int getGlowDuration() {
    return this.num("glow.duration");
  }

  public String getGlowSound() {
    return this.str("glow.sound");
  }

  public int getHackDuration() {
    return this.num("hack.duration");
  }

  public String getHackSound() {
    return this.str("hack.sound");
  }

  public int getHauntDuration() {
    return this.num("haunt.duration");
  }

  public String getHauntSound() {
    return this.str("haunt.sound");
  }

  public int getJebDuration() {
    return this.num("jeb.duration");
  }

  public int getJebSheepCount() {
    return this.num("jeb.sheep.count");
  }

  public String getJebSound() {
    return this.str("jeb.sound");
  }

  public int getJumpScareDuration() {
    return this.num("jump_scare.duration");
  }

  public int getJumpScareEffectDuration() {
    return this.num("jump_scare.effect.duration");
  }

  public int getLevitationDuration() {
    return this.num("levitation.duration");
  }

  public String getLevitationSound() {
    return this.str("levitation.sound");
  }

  public int getNeckSnapDuration() {
    return this.num("neck_snap.duration");
  }

  public String getNeckSnapSound() {
    return this.str("neck_snap.sound");
  }

  public double getPonyHorseSpeed() {
    return this.dec("pony.speed");
  }

  public String getPonySound() {
    return this.str("pony.sound");
  }

  public double getShockwaveExplosionRadius() {
    return this.dec("shockwave.explosion.radius");
  }

  public double getShockwaveExplosionPower() {
    return this.dec("shockwave.explosion.power");
  }

  public String getShockwaveSound() {
    return this.str("shockwave.sound");
  }

  public int getSmokeDuration() {
    return this.num("smoke.duration");
  }

  public String getSmokeSound() {
    return this.str("smoke.sound");
  }

  public int getSpasmDuration() {
    return this.num("spasm.duration");
  }

  public String getSpasmSound() {
    return this.str("spasm.sound");
  }

  public String getSpawnSound() {
    return this.str("spawn.sound");
  }

  public int getStarDuration() {
    return this.num("star.duration");
  }

  public String getStarSound() {
    return this.str("star.sound");
  }

  public String getBlastoffSound() {
    return this.str("blastoff.sound");
  }

  public int getBushDuration() {
    return this.num("bush.duration");
  }

  public String getBushSound() {
    return this.str("bush.sound");
  }

  public int getChippedDuration() {
    return this.num("chipped.duration");
  }

  public String getChippedSound() {
    return this.str("chipped.sound");
  }

  public int getCloakDuration() {
    return this.num("cloak.duration");
  }

  public String getCloakSound() {
    return this.str("cloak.sound");
  }

  public String getCorpusWarpSound() {
    return this.str("corpus_warp.sound");
  }

  public int getCryoFreezeRadius() {
    return this.num("cryo_freeze.radius");
  }

  public String getCryoFreezeSound() {
    return this.str("cryo_freeze.sound");
  }

  public int getDeadringerDuration() {
    return this.num("deadringer.duration");
  }

  public String getDeadringerSound() {
    return this.str("deadringer.sound");
  }

  public String getDecoySound() {
    return this.str("decoy.sound");
  }

  public double getDistorterDestroyRadius() {
    return this.dec("distorter.destroy.radius");
  }

  public double getDistorterEffectRadius() {
    return this.dec("distorter.effect.radius");
  }

  public String getDistorterSound() {
    return this.str("distorter.sound");
  }

  public int getDroneDuration() {
    return this.num("drone.duration");
  }

  public String getDroneSound() {
    return this.str("drone.sound");
  }

  public double getFlashbangRadius() {
    return this.dec("flashbang.radius");
  }

  public int getFlashbangDuration() {
    return this.num("flashbang.duration");
  }

  public double getFlashlightConeAngle() {
    return this.dec("flashlight.cone.angle");
  }

  public double getFlashlightConeLength() {
    return this.dec("flashlight.cone.length");
  }

  public double getFlashlightRadius() {
    return this.dec("flashlight.radius");
  }

  public String getFriendWarpSound() {
    return this.str("friend_warp.sound");
  }

  public int getGhostingWoolDelay() {
    return this.num("ghosting.wool.delay");
  }

  public String getHorcruxSound() {
    return this.str("horcrux.sound");
  }

  public int getIceSkatinDuration() {
    return this.num("ice_skatin.duration");
  }

  public String getIceSkatinSound() {
    return this.str("ice_skatin.sound");
  }

  public int getIceSpiritDuration() {
    return this.num("ice_spirit.duration");
  }

  public String getIceSpiritSound() {
    return this.str("ice_spirit.sound");
  }

  public int getKillerRewindCooldown() {
    return this.num("killer_rewind.cooldown");
  }

  public String getKillerRewindSound() {
    return this.str("killer_rewind.sound");
  }

  public int getKillerTrackerUses() {
    return this.num("killer_tracker.uses");
  }

  public String getKillerTrackerSound() {
    return this.str("killer_tracker.sound");
  }

  public double getLifeInsuranceRadius() {
    return this.dec("life_insurance.radius");
  }

  public String getLifeInsuranceSound() {
    return this.str("life_insurance.sound");
  }

  public int getMagnetModeMultiplier() {
    return this.num("magnet_mode.multiplier");
  }

  public String getMagnetModeSound() {
    return this.str("magnet_mode.sound");
  }

  public double getMedBotRadius() {
    return this.dec("med_bot.radius");
  }

  public double getMedBotDestroyRadius() {
    return this.dec("med_bot.destroy.radius");
  }

  public String getMedBotSound() {
    return this.str("med_bot.sound");
  }

  public int getMindControlDuration() {
    return this.num("mind_control.duration");
  }

  public String getMindControlSound() {
    return this.str("mind_control.sound");
  }

  public double getParasiteDestroyRadius() {
    return this.dec("parasite.destroy.radius");
  }

  public double getParasiteRadius() {
    return this.dec("parasite.radius");
  }

  public String getParasiteSound() {
    return this.str("parasite.sound");
  }

  public String getRandomTeleportSound() {
    return this.str("random_teleport.sound");
  }

  public String getResurrectionStoneSound() {
    return this.str("resurrection_stone.sound");
  }

  public int getRetaliationMaxAmplifier() {
    return this.num("retaliation.max_amplifier");
  }

  public String getRetaliationSound() {
    return this.str("retaliation.sound");
  }

  public int getRewindCooldown() {
    return this.num("rewind.cooldown");
  }

  public double getSixthSenseRadius() {
    return this.dec("sixth_sense.radius");
  }

  public String getSixthSenseSound() {
    return this.str("sixth_sense.sound");
  }

  public double getSmokeGrenadeRadius() {
    return this.dec("smoke_grenade.radius");
  }

  public int getSmokeGrenadeDuration() {
    return this.num("smoke_grenade.duration");
  }

  public String getSupplyDropMasks() {
    return this.str("supply_drop.masks");
  }

  public double getTrackerRadius() {
    return this.dec("tracker.radius");
  }

  public String getTrackerSound() {
    return this.str("tracker.sound");
  }

  public String getTranslocatorSound() {
    return this.str("translocator.sound");
  }

  public double getPartSnifferRadius() {
    return this.dec("part_sniffer.radius");
  }

  public String getAllSeeingEyeSound() {
    return this.str("all_seeing_eye.sound");
  }

  public int getAllSeeingEyeDuration() {
    return this.num("all_seeing_eye.duration");
  }

  public String getBloodCurseSound() {
    return this.str("blood_curse.sound");
  }

  public double getBurnTheBodyRadius() {
    return this.dec("burn_the_body.radius");
  }

  public String getBurnTheBodySound() {
    return this.str("burn_the_body.sound");
  }

  public double getCursedNoteRadius() {
    return this.dec("cursed_note.radius");
  }

  public double getCursedNoteEffectRadius() {
    return this.dec("cursed_note.effect.radius");
  }

  public String getCursedNoteSound() {
    return this.str("cursed_note.sound");
  }

  public String getDeathHoundSound() {
    return this.str("death_hound.sound");
  }

  public String getDeathSteedSound() {
    return this.str("death_steed.sound");
  }

  public String getDormagoggSound() {
    return this.str("dormagogg.sound");
  }

  public int getEagleEyeDuration() {
    return this.num("eagle_eye.duration");
  }

  public String getEagleEyeSound() {
    return this.str("eagle_eye.sound");
  }

  public int getEmpBlastDuration() {
    return this.num("emp_blast.duration");
  }

  public String getEnderShadowsSound() {
    return this.str("ender_shadows.sound");
  }

  public String getFakePartSound() {
    return this.str("fake_part.sound");
  }

  public String getFakePartEffectSound() {
    return this.str("fake_part.effect.sound");
  }

  public double getFakePartRadius() {
    return this.dec("fake_part.radius");
  }

  public int getFakePartDuration() {
    return this.num("fake_part.duration");
  }

  public String getFireTrailSound() {
    return this.str("fire_trail.sound");
  }

  public String getFloorIsLavaSound() {
    return this.str("floor_is_lava.sound");
  }

  public String getForewarnSound() {
    return this.str("forewarn.sound");
  }

  public int getFrightDuration() {
    return this.num("fright.duration");
  }

  public String getHealthCutSound() {
    return this.str("health_cut.sound");
  }

  public String getHeatSeekerSound() {
    return this.str("heat_seeker.sound");
  }

  public double getHeatSeekerRadius() {
    return this.dec("heat_seeker.radius");
  }

  public String getIcePathSound() {
    return this.str("ice_path.sound");
  }

  public String getInfraredVisionSound() {
    return this.str("infrared_vision.sound");
  }

  public int getInfraredVisionDuration() {
    return this.num("infrared_vision.duration");
  }

  public String getMurderousWarpSound() {
    return this.str("murderous_warp.sound");
  }

  public String getPartWarpSound() {
    return this.str("part_warp.sound");
  }

  public String getPhantomSound() {
    return this.str("phantom.sound");
  }

  public int getPhantomDuration() {
    return this.num("phantom.duration");
  }

  public String getPlayerTrackerSound() {
    return this.str("player_tracker.sound");
  }

  public int getPlayerTrackerUses() {
    return this.num("player_tracker.uses");
  }

  public String getPoisonSmogSound() {
    return this.str("poison_smog.sound");
  }

  public double getPoisonSmogRadius() {
    return this.dec("poison_smog.radius");
  }

  public int getPoisonSmogDuration() {
    return this.num("poison_smog.duration");
  }

  public String getQuickBombSound() {
    return this.str("quick_bomb.sound");
  }

  public int getQuickBombTicks() {
    return this.num("quick_bomb_ticks");
  }

  public double getQuickBombDamage() {
    return this.dec("quick_bomb_damage");
  }

  public String getRedArrowSound() {
    return this.str("red_arrow.sound");
  }

  public int getRedArrowDuration() {
    return this.num("red_arrow.duration");
  }

  public String getTrapSeekerSound() {
    return this.str("trap_seeker.sound");
  }

  public double getTrapSeekerRadius() {
    return this.dec("trap_seeker.radius");
  }

  public String getTrapWreckerSound() {
    return this.str("trap_wrecker.sound");
  }

  public int getTrapWreckerDuration() {
    return this.num("trap_wrecker.duration");
  }

  public String getWarpDistortSound() {
    return this.str("warp_distort.sound");
  }

  public String getCameraSound() {
    return this.str("camera.sound");
  }

  public int getHookCost() {
    return this.num("hook.cost");
  }

  public int getAllSeeingEyeCost() {
    return this.num("all_seeing_eye.cost");
  }

  public int getBloodCurseCost() {
    return this.num("blood_curse.cost");
  }

  public int getBurnTheBodyCost() {
    return this.num("burn_the_body.cost");
  }

  public int getKillerCameraCost() {
    return this.num("killer_camera.cost");
  }

  public int getCameraCost() {
    return this.num("camera.cost");
  }

  public int getCorruptionCost() {
    return this.num("corruption.cost");
  }

  public int getCursedNoteCost() {
    return this.num("cursed_note.cost");
  }

  public int getDeathHoundCost() {
    return this.num("death_hound.cost");
  }

  public int getDeathSteedCost() {
    return this.num("death_steed.cost");
  }

  public int getDormagoggCost() {
    return this.num("dormagogg.cost");
  }

  public int getEagleEyeCost() {
    return this.num("eagle_eye.cost");
  }

  public int getEmpBlastCost() {
    return this.num("emp_blast.cost");
  }

  public int getEnderShadowsCost() {
    return this.num("ender_shadows.cost");
  }

  public int getFakePartCost() {
    return this.num("fake_part.cost");
  }

  public int getFireTrailCost() {
    return this.num("fire_trail.cost");
  }

  public int getFloorIsLavaCost() {
    return this.num("floor_is_lava.cost");
  }

  public int getForewarnCost() {
    return this.num("forewarn.cost");
  }

  public int getFrightCost() {
    return this.num("fright.cost");
  }

  public int getHealthCutCost() {
    return this.num("health_cut.cost");
  }

  public int getHeatSeekerCost() {
    return this.num("heat_seeker.cost");
  }

  public int getIcePathCost() {
    return this.num("ice_path.cost");
  }

  public int getInfraredVisionCost() {
    return this.num("infrared_vision.cost");
  }

  public int getMurderousWarpCost() {
    return this.num("murderous_warp.cost");
  }

  public int getPartWarpCost() {
    return this.num("part_warp.cost");
  }

  public int getPhantomCost() {
    return this.num("phantom.cost");
  }

  public int getPlayerTrackerCost() {
    return this.num("player_tracker.cost");
  }

  public int getPoisonSmogCost() {
    return this.num("poison_smog.cost");
  }

  public int getPortalGunCost() {
    return this.num("portal_gun.cost");
  }

  public int getQuickBombCost() {
    return this.num("quick_bomb.cost");
  }

  public int getRedArrowCost() {
    return this.num("red_arrow.cost");
  }

  public int getTrapSeekerCost() {
    return this.num("trap_seeker.cost");
  }

  public int getTrapWreckerCost() {
    return this.num("trap_wrecker.cost");
  }

  public int getWarpDistortCost() {
    return this.num("warp_distort.cost");
  }

  public int getSurvivorGearCost() {
    return this.num("survivor_gear.cost");
  }

  public int getExcavatorCost() {
    return this.num("excavator.cost");
  }

  public int getShieldCost() {
    return this.num("shield.cost");
  }

  public int getBearCost() {
    return this.num("bear.cost");
  }

  public int getBlindCost() {
    return this.num("blind.cost");
  }

  public int getBurrowCost() {
    return this.num("burrow.cost");
  }

  public int getCageCost() {
    return this.num("cage.cost");
  }

  public int getDistortCost() {
    return this.num("distort.cost");
  }

  public int getFartCost() {
    return this.num("fart.cost");
  }

  public int getFireworkCost() {
    return this.num("firework.cost");
  }

  public int getFreezeCost() {
    return this.num("freeze.cost");
  }

  public int getGhostCost() {
    return this.num("ghost.cost");
  }

  public int getGlowCost() {
    return this.num("glow.cost");
  }

  public int getHackCost() {
    return this.num("hack.cost");
  }

  public int getHauntCost() {
    return this.num("haunt.cost");
  }

  public int getJebCost() {
    return this.num("jeb.cost");
  }

  public int getJumpScareCost() {
    return this.num("jump_scare.cost");
  }

  public int getLevitationCost() {
    return this.num("levitation.cost");
  }

  public int getNeckSnapCost() {
    return this.num("neck_snap.cost");
  }

  public int getPonyCost() {
    return this.num("pony.cost");
  }

  public int getShockwaveCost() {
    return this.num("shockwave.cost");
  }

  public int getSmokeCost() {
    return this.num("smoke.cost");
  }

  public int getSpasmCost() {
    return this.num("spasm.cost");
  }

  public int getSpawnCost() {
    return this.num("spawn.cost");
  }

  public int getStarCost() {
    return this.num("star.cost");
  }

  public int getBlastOffCost() {
    return this.num("blast_off.cost");
  }

  public int getBushCost() {
    return this.num("bush.cost");
  }

  public int getChippedCost() {
    return this.num("chipped.cost");
  }

  public int getCloakCost() {
    return this.num("cloak.cost");
  }

  public int getCorpusWarpCost() {
    return this.num("corpus_warp.cost");
  }

  public int getCryoFreezeCost() {
    return this.num("cryo_freeze.cost");
  }

  public int getDeadringerCost() {
    return this.num("deadringer.cost");
  }

  public int getDecoyCost() {
    return this.num("decoy.cost");
  }

  public int getDistorterCost() {
    return this.num("distorter.cost");
  }

  public int getDroneCost() {
    return this.num("drone.cost");
  }

  public int getFlashbangCost() {
    return this.num("flashbang.cost");
  }

  public int getFlashlightCost() {
    return this.num("flashlight.cost");
  }

  public int getFriendWarpCost() {
    return this.num("friend_warp.cost");
  }

  public int getHorcruxCost() {
    return this.num("horcrux.cost");
  }

  public int getIceSkatinCost() {
    return this.num("ice_skatin.cost");
  }

  public int getIceSpiritCost() {
    return this.num("ice_spirit.cost");
  }

  public int getKillerRewindCost() {
    return this.num("killer_rewind.cost");
  }

  public int getKillerTrackerCost() {
    return this.num("killer_tracker.cost");
  }

  public int getLifeInsuranceCost() {
    return this.num("life_insurance.cost");
  }

  public int getMagnetModeCost() {
    return this.num("magnet_mode.cost");
  }

  public int getMedBotCost() {
    return this.num("med_bot.cost");
  }

  public int getMedKitCost() {
    return this.num("med_kit.cost");
  }

  public int getMindControlCost() {
    return this.num("mind_control.cost");
  }

  public int getParasiteCost() {
    return this.num("parasite.cost");
  }

  public int getPortalTrapCost() {
    return this.num("portal_trap.cost");
  }

  public int getRandomTeleportCost() {
    return this.num("random_teleport.cost");
  }

  public int getRandomTrapCost() {
    return this.num("random_trap.cost");
  }

  public int getResurrectionStoneCost() {
    return this.num("resurrection_stone.cost");
  }

  public int getRetaliationCost() {
    return this.num("retaliation.cost");
  }

  public int getRewindCost() {
    return this.num("rewind.cost");
  }

  public int getSixthSenseCost() {
    return this.num("sixth_sense.cost");
  }

  public int getSmokeGrenadeCost() {
    return this.num("smoke_grenade.cost");
  }

  public int getSpeedPendantCost() {
    return this.num("speed_pendant.cost");
  }

  public int getSupplyDropCost() {
    return this.num("supply_drop.cost");
  }

  public int getTrackerCost() {
    return this.num("tracker.cost");
  }

  public int getTranslocatorCost() {
    return this.num("translocator.cost");
  }

  private Material mat(final String key) {
    final String material = this.str(key);
    final Material mat = Material.getMaterial(material);
    final String error = "Invalid material for key: %s".formatted(key);
    return requireNonNull(mat, error);
  }

  private Color rgb(final String key) {
    final String[] split = this.str(key).split(",");
    if (split.length != 3) {
      final String msg = "Invalid color format for key: %s".formatted(key);
      throw new AssertionError(msg);
    }
    final int r = Integer.parseInt(split[0]);
    final int g = Integer.parseInt(split[1]);
    final int b = Integer.parseInt(split[2]);
    return new Color(r, g, b);
  }

  private boolean bool(final String key) {
    return this.bundle.getBoolean(key);
  }

  private int num(final String key) {
    return this.bundle.getInt(key);
  }

  private double dec(final String key) {
    return this.bundle.getDouble(key);
  }

  @Subst("")
  private String str(final String key) {
    return this.bundle.getString(key);
  }
}
