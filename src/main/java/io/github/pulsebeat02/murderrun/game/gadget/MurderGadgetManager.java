package io.github.pulsebeat02.murderrun.game.gadget;

import com.google.common.reflect.ClassPath;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class MurderGadgetManager {

  private static final String GADGETS_PACKAGE = "io.github.pulsebeat02.murderrun.gadget";
  public static final Map<String, Constructor<?>> GADGET_LOOK_UP_MAP = new HashMap<>();

  @SuppressWarnings("nullness")
  public static void init(final MurderRun plugin) {
    final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    try {
      for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
        if (info.getName().startsWith(GADGETS_PACKAGE)) {
          final Class<?> clazz = info.load();
          final Constructor<?> constructor = getConstructor(clazz);
          final MurderGadget gadget = invokeGadgetConstructor(plugin, constructor);
          final String name = gadget.getName();
          GADGET_LOOK_UP_MAP.put(name, constructor);
        }
      }
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Constructor<?> getConstructor(final Class<?> clazz) {
    final Constructor<?>[] constructors = clazz.getConstructors();
    if (constructors.length == 0) {
      throw new AssertionError(
          String.format("Couldn't find constructor of gadget class %s", clazz));
    }
    return constructors[0];
  }

  private static MurderGadget invokeGadgetConstructor(
      final MurderRun plugin, final Constructor<?> constructor)
      throws InvocationTargetException, InstantiationException, IllegalAccessException {
    final Class<?>[] arguments = constructor.getParameterTypes();
    final MurderGadget gadget;
    if (arguments.length == 1 && arguments[0].equals(MurderGame.class)) {
      gadget = (MurderGadget) constructor.newInstance(plugin);
    } else {
      gadget = (MurderGadget) constructor.newInstance();
    }
    return gadget;
  }

  private final MurderRun plugin;
  private final MurderGame game;
  private final Map<String, MurderGadget> gameGadgets;
  private final AtomicInteger activationRange;

  public MurderGadgetManager(final MurderGame game) {
    final MurderRun plugin = game.getPlugin();
    this.game = game;
    this.plugin = plugin;
    this.gameGadgets = this.getUsedGadgets(plugin);
    this.activationRange = new AtomicInteger(3);
  }

  private Map<String, MurderGadget> getUsedGadgets(
      @UnderInitialization MurderGadgetManager this, final MurderRun plugin) {
    final Collection<Constructor<?>> gadgetClasses = GADGET_LOOK_UP_MAP.values();
    final Map<String, MurderGadget> gadgets = new HashMap<>();
    for (final Constructor<?> constructor : gadgetClasses) {
      try {
        final MurderGadget gadget = invokeGadgetConstructor(plugin, constructor);
        final String name = gadget.getName();
        gadgets.put(name, gadget);
      } catch (final InvocationTargetException
          | InstantiationException
          | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }
    return gadgets;
  }

  public void start() {
    final MurderPlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllMurderers(killer -> {});
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public MurderGame getGame() {
    return this.game;
  }

  public Map<String, MurderGadget> getGameGadgets() {
    return this.gameGadgets;
  }

  public int getActivationRange() {
    return this.activationRange.get();
  }

  public void setActivationRange(final int range) {
    this.activationRange.getAndSet(range);
  }
}
