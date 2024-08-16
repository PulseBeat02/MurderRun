package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public final class FireworkTrap extends SurvivorTrap {

  public FireworkTrap() {
    super(
        "firework",
        Material.FIREWORK_ROCKET,
        Locale.FIREWORK_TRAP_NAME.build(),
        Locale.FIREWORK_TRAP_LORE.build(),
        Locale.FIREWORK_TRAP_ACTIVATE.build(),
        16,
        Color.RED);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final Location location = murderer.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnFirework(location, world), 0, 5, 5 * 20L);
  }

  private void spawnFirework(final Location location, final World world) {
    world.spawn(location, Firework.class, firework -> {
      firework.setShotAtAngle(false);
      final FireworkMeta meta = firework.getFireworkMeta();
      meta.setPower(RandomUtils.generateInt(10));
      meta.addEffect(this.generateRandomFireworkEffect());
      firework.setFireworkMeta(meta);
    });
  }

  private FireworkEffect generateRandomFireworkEffect() {
    final List<Color> primary = this.generateRandomColors();
    final List<Color> fade = this.generateRandomColors();
    return FireworkEffect.builder()
        .flicker(true)
        .trail(true)
        .withColor(primary)
        .withFade(fade)
        .build();
  }

  private ImmutableList<Color> generateRandomColors() {
    final int count = RandomUtils.generateInt(4);
    final List<Color> list = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      final int r = RandomUtils.generateInt(0, 256);
      final int g = RandomUtils.generateInt(0, 256);
      final int b = RandomUtils.generateInt(0, 256);
      final Color color = new Color(r, g, b);
      list.add(color);
    }
    return ImmutableList.copyOf(list);
  }
}
