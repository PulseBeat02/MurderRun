package io.github.pulsebeat02.murderrun.game.gadget.killer;

import io.github.pulsebeat02.murderrun.game.gadget.killer.utility.PlayerTracker;
import io.github.pulsebeat02.murderrun.game.gadget.killer.utility.RedArrow;

public enum KillerGadgets {
  RED_ARROW(RedArrow.class),
  PLAYER_TRACKER(PlayerTracker.class)

/*

KILLER TRAPS IMPLEMENTED

Red Arrow: Places a red beacon above any survivor nearby
Player Tracker: Tells the Killer how close opponents are to the Killer
Warp Distort: Swaps two opponents with each other to cause confusion
Murderous Warp: Swaps the Killer and opponent's positions with teleportation
Camera: Alerts the Killer if an opponent crosses the camera
Infrared Vision: Gives the Killer infrared vision, to see where someone is hiding
Fright: Puts an illusion in-front of everyone suddenly, jump scaring them
Blood Curse: Causes the opponent to bleed profusely permanently, leaving a trail behind for the killer to follow
Trap Seeker: Allows the Killer to detect hidden traps
Death Steed: Summons a Death Steed that follows where the survivors are, can ride on the horse
All-Seeing Eye: Allows the Killer to see through their opponent's eyes, allowing them to track their location and plans
Health Cut: Reduces the health of all opponents to 1, allowing for them to be one-shot even if they have comparable durability
Phantom: Allows the Killer to fly and float through walls for 15 seconds
Death Hound: Summons a wolf that sniffs out and kills opponents
Heat Seeker: Allows the killer to see all opponents within a certain range of him.
Ice Path: Leaves a trail of ice wherever the Killer walks, allowing them to move faster
Fire Trail: Leaves a trail of fire wherever the Killer walks
Quick Bomb: Creates a bomb near all opponents, they have 2 seconds to escape before being blown up.
Hook: Drags opponents closer to the killer
EMP Blast: Destroys all traps on the map and stuns all opponents.
Eagle Eye: Allows the killer an airborne view of the area, showing hiding spots of opponents.
Forewarn: If an opponent grabs a truck part, they will start to glow
Part Warp: Teleports the closest car part to the trap where the Killer put it.
The Floor is Lava: Any opponents standing still for an extended period of time will be illuminated, allowing the killer to see them no matter where they are.
Dormagogg: An evil being that blind, stu ns, and illuminates opponents
Gamble: Killing a player causes them to drop a random killer trap.
Fake Part: Places a fake part, anyone who tries to pick it up will be slowed.
Poison Smog: Covers the area in a poisonous smog

LIST OF ALL KILLER TRAPS TO IMPLEMENT:

Trap Wrecker: Nullifies and destroys all nearby traps for 30 seconds
Corruption: Can resurrect dead opponents and corrupt them into joining his side
Portal Gun: Allows the user to create portals wherever they please
Burn the Body: Completely destroys the body of an opponent
Ender Shadows: Creates animated shadows which causes opponents to light up when touched
Cursed Note: Curses all close-by car parts to sabotage opponents if they approach them. If the opponent wants to get rid of the curse they need to find the Cursed Note.

*/

/*

EXCLUDED TRAPS FROM SSUNDEE'S SERIES
Traps here are excluded for one of the following reasons:
- Trap isn't explained well
- Trap pertains to a specific theme too much (for example, Fortnite)
- Trap functions too similar to another trap
- Trap is not balanced or not worth implementing
- Trap has controversial topics (for example, clickbait, demonetization, etc).
- Trap was superseded by another trap (for example, paint trap -> infrared vision)

Super Saiyan: Grants the Killer a significant speed and strength boost and giving them the ability to fly
Fortnite Builder: Allows the Killer to generate, build, and place objects on the spot giving them an advantage.
Infinity Gauntlet: Allows the Killer to jump extremely high and create explosions
Animatronic Alarm: Blinds, slows, jump scares, and makes the opponent visible to the Killer
Brigette - Shield Bash: Bashing ground stuns survivors
Paint Trap: Covers opponents in glow-in-the-dark paint for a short time
Pumpkin Disease: Causes a disease in opponents which forces them to glow
The Reaper: Creates tentacles of darkness that slow/blind nearby opponents

*/ ;

  private final Class<?> clazz;

  KillerGadgets(final Class<?> clazz) {
    this.clazz = clazz;
  }

  public Class<?> getClazz() {
    return this.clazz;
  }
}
