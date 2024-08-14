package io.github.pulsebeat02.murderrun.game.gadget;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadgets;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadgets;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorTrap;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import io.github.pulsebeat02.murderrun.utils.StreamUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.type.tuple.Pair;

public final class GadgetLoadingMechanism {

  private static final Map<String, Pair<Gadget, Constructor<?>>> GADGET_LOOK_UP_MAP =
      new HashMap<>();

  static {
    final Plugin plugin = JavaPlugin.getProvidingPlugin(MurderRun.class);
    final SurvivorGadgets[] survivorGadgets = SurvivorGadgets.values();
    final KillerGadgets[] killerGadgets = KillerGadgets.values();
    for (final SurvivorGadgets gadget : survivorGadgets) {
      final Class<?> clazz = gadget.getClazz();
      handleGadgetClass(plugin, clazz);
    }
    for (final KillerGadgets gadget : killerGadgets) {
      final Class<?> clazz = gadget.getClazz();
      handleGadgetClass(plugin, clazz);
    }
  }

  public static void init() {}

  private static void handleGadgetClass(final Plugin plugin, final Class<?> clazz) {
    final Constructor<?> constructor = getConstructor(clazz);
    final Gadget gadget = invokeGadgetConstructor(plugin, constructor);
    final String name = gadget.getName();
    final Pair<Gadget, Constructor<?>> pair = Pair.of(gadget, constructor);
    GADGET_LOOK_UP_MAP.put(name, pair);
  }

  private static Constructor<?> getConstructor(final Class<?> clazz) {
    final Constructor<?>[] constructors = clazz.getConstructors();
    if (constructors.length == 0) {
      final String message = "Couldn't find constructor of gadget class %s".formatted(clazz);
      throw new AssertionError(message);
    }
    return constructors[0];
  }

  private static Gadget invokeGadgetConstructor(
      final Plugin plugin, final Constructor<?> constructor) {
    try {
      final Class<?>[] arguments = constructor.getParameterTypes();
      final boolean hasPlugin = arguments.length == 1;
      return (Gadget) (hasPlugin ? constructor.newInstance(plugin) : constructor.newInstance());
    } catch (final InvocationTargetException | InstantiationException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  private final GadgetManager manager;
  private final Map<String, Gadget> gameGadgets;
  private final Set<Gadget> killerGadgets;
  private final Set<Gadget> survivorGadgets;

  public GadgetLoadingMechanism(final GadgetManager manager) {
    final MurderRun run = manager.getPlugin();
    this.manager = manager;
    this.gameGadgets = this.getUsedGadgets(run);
    this.killerGadgets = this.getKillerGadgets();
    this.survivorGadgets = this.getSurvivorGadgets();
  }

  private Set<Gadget> getKillerGadgets(@UnderInitialization GadgetLoadingMechanism this) {
    final Collection<Gadget> gadgets = this.gameGadgets.values();
    return gadgets.stream()
        .filter(StreamUtils.isInstanceOf(KillerGadget.class)
            .or(StreamUtils.isInstanceOf(KillerTrap.class)))
        .collect(Collectors.toSet());
  }

  private Set<Gadget> getSurvivorGadgets(@UnderInitialization GadgetLoadingMechanism this) {
    final Collection<Gadget> gadgets = this.gameGadgets.values();
    return gadgets.stream()
        .filter(StreamUtils.isInstanceOf(SurvivorGadget.class)
            .or(StreamUtils.isInstanceOf(SurvivorTrap.class)))
        .collect(Collectors.toSet());
  }

  private Map<String, Gadget> getUsedGadgets(
      @UnderInitialization GadgetLoadingMechanism this, final MurderRun plugin) {
    final Server server = plugin.getServer();
    final PluginManager pluginManager = server.getPluginManager();
    final Collection<Pair<Gadget, Constructor<?>>> gadgetClasses = GADGET_LOOK_UP_MAP.values();
    final Map<String, Gadget> gadgets = new HashMap<>();
    for (final Pair<Gadget, Constructor<?>> pair : gadgetClasses) {
      final Constructor<?> constructor = pair.second();
      final Gadget gadget = invokeGadgetConstructor(plugin, constructor);
      if (gadget instanceof final Listener listener) {
        pluginManager.registerEvents(listener, plugin);
      }
      final String name = gadget.getName();
      gadgets.put(name, gadget);
    }
    return gadgets;
  }

  public void shutdown() {
    final Collection<Gadget> values = this.gameGadgets.values();
    for (final Gadget gadget : values) {
      if (!(gadget instanceof final Listener listener)) {
        continue;
      }
      HandlerList.unregisterAll(listener);
    }
  }

  public @Nullable Gadget getGadgetFromStack(final ItemStack stack) {
    final String data = requireNonNull(ItemUtils.getPersistentDataAttribute(
        stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING));
    return this.gameGadgets.get(data);
  }

  public Gadget getRandomInnocentGadget() {
    final List<Gadget> gadgets =
        this.survivorGadgets.stream().collect(StreamUtils.toShuffledList());
    return gadgets.getFirst();
  }

  public Gadget getRandomKillerGadget() {
    final List<Gadget> gadgets = this.killerGadgets.stream().collect(StreamUtils.toShuffledList());
    return gadgets.getFirst();
  }

  public GadgetManager getManager() {
    return this.manager;
  }

  public Map<String, Gadget> getGameGadgets() {
    return this.gameGadgets;
  }

  public static Map<String, Pair<Gadget, Constructor<?>>> getGadgetLookUpMap() {
    return GADGET_LOOK_UP_MAP;
  }
}
