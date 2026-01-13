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
package me.brandonli.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetRightClickPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.RandomUtils;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.inventory.meta.FireworkMeta;

public final class FireworkTrap extends SurvivorTrap {

  public FireworkTrap(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "firework_trap",
      properties.getFireworkCost(),
      ItemFactory.createGadget(
        "firework_trap",
        properties.getFireworkMaterial(),
        Message.FIREWORK_NAME.build(),
        Message.FIREWORK_LORE.build()
      ),
      Message.FIREWORK_ACTIVATE.build(),
      properties.getFireworkColor()
    );
  }

  @Override
  public boolean onGadgetRightClick(final GadgetRightClickPacket packet) {
    return true;
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final Location location = murderer.getLocation();
    final GameScheduler scheduler = game.getScheduler();
    final NullReference reference = NullReference.of();
    final GameProperties properties = game.getProperties();
    scheduler.scheduleRepeatedTask(() -> this.spawnFirework(location), 0, 5, properties.getFireworkDuration(), reference);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(properties.getFireworkSound());
  }

  private void spawnFirework(final Location location) {
    final World world = requireNonNull(location.getWorld());
    final Location random = this.randomLocation(location);
    world.spawn(random, Firework.class, firework -> {
      this.customizeMeta(firework);
      this.customizeProperties(firework);
    });
  }

  private void customizeProperties(final Firework firework) {
    firework.setShotAtAngle(false);
  }

  private void customizeMeta(final Firework firework) {
    final FireworkMeta meta = firework.getFireworkMeta();
    meta.setPower(RandomUtils.generateInt(1, 5));
    meta.addEffect(this.generateRandomFireworkEffect());
    firework.setFireworkMeta(meta);
  }

  private Location randomLocation(final Location location) {
    final double xOffset = (RandomUtils.generateFloat() * 2 - 1) * 3;
    final double zOffset = (RandomUtils.generateFloat() * 2 - 1) * 3;
    return location.add(xOffset, 0, zOffset);
  }

  private FireworkEffect generateRandomFireworkEffect() {
    final List<Color> primary = this.generateRandomColors();
    final List<Color> fade = this.generateRandomColors();
    final Type type = this.getRandomType();
    return FireworkEffect.builder().with(type).flicker(true).trail(true).withColor(primary).withFade(fade).build();
  }

  private Type getRandomType() {
    final Type[] types = Type.values();
    final int index = RandomUtils.generateInt(types.length);
    return types[index];
  }

  private ImmutableList<Color> generateRandomColors() {
    final int count = RandomUtils.generateInt(4);
    final List<Color> list = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      final int r = RandomUtils.generateInt(0, 256);
      final int g = RandomUtils.generateInt(0, 256);
      final int b = RandomUtils.generateInt(0, 256);
      final Color color = Color.fromRGB(r, g, b);
      list.add(color);
    }
    return ImmutableList.copyOf(list);
  }
}
