package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.inventory.meta.FireworkMeta;

public final class FireworkTrap extends SurvivorTrap {

  public FireworkTrap() {
    super(
      "firework",
      Material.FIREWORK_ROCKET,
      Message.FIREWORK_NAME.build(),
      Message.FIREWORK_LORE.build(),
      Message.FIREWORK_ACTIVATE.build(),
      GameProperties.FIREWORK_COST,
      java.awt.Color.RED
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final Location location = murderer.getLocation();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnFirework(location), 0, 5, GameProperties.FIREWORK_DURATION);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.FIREWORK_SOUND);
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
