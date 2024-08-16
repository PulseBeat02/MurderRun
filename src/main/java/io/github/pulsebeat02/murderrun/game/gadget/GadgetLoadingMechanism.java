package io.github.pulsebeat02.murderrun.game.gadget;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerApparatus;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadgets;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorApparatus;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadgets;
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
import org.bukkit.plugin.PluginManager;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.type.tuple.Pair;

public final class GadgetLoadingMechanism {

  private static final Map<String, Pair<Gadget, Constructor<Object>>> GADGET_LOOK_UP_MAP =
      new HashMap<>();

  static {
    final SurvivorGadgets[] survivorGadgets = SurvivorGadgets.values();
    final KillerGadgets[] killerGadgets = KillerGadgets.values();
    for (final SurvivorGadgets gadget : survivorGadgets) {
      final Class<?> clazz = gadget.getClazz();
      handleGadgetClass(clazz);
    }
    for (final KillerGadgets gadget : killerGadgets) {
      final Class<?> clazz = gadget.getClazz();
      handleGadgetClass(clazz);
    }
  }

  public static void init() {
    // initialize all static fields
  }

  private static void handleGadgetClass(final Class<?> clazz) {
    final Constructor<Object> constructor = getConstructor(clazz);
    final Gadget gadget = invokeGadgetConstructor(constructor);
    final String name = gadget.getName();
    final Pair<Gadget, Constructor<Object>> pair = Pair.of(gadget, constructor);
    GADGET_LOOK_UP_MAP.put(name, pair);
  }

  private static Constructor<Object> getConstructor(final Class<?> clazz) {
    final Constructor<Object>[] constructors = (Constructor<Object>[]) clazz.getConstructors();
    if (constructors.length == 0) {
      final String message = "Couldn't find constructor of gadget class %s".formatted(clazz);
      throw new AssertionError(message);
    }
    return constructors[0];
  }

  private static Gadget invokeGadgetConstructor(final Constructor<?> constructor) {
    try {
      return (Gadget) constructor.newInstance();
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
    this.killerGadgets = this.getKillerGadgets(this.gameGadgets);
    this.survivorGadgets = this.getSurvivorGadgets(this.gameGadgets);
  }

  private Set<Gadget> getKillerGadgets(
      @UnderInitialization GadgetLoadingMechanism this, final Map<String, Gadget> gameGadgets) {
    final Collection<Gadget> gadgets = gameGadgets.values();
    return gadgets.stream()
        .filter(StreamUtils.isInstanceOf(KillerApparatus.class))
        .collect(Collectors.toSet());
  }

  private Set<Gadget> getSurvivorGadgets(
      @UnderInitialization GadgetLoadingMechanism this, final Map<String, Gadget> gameGadgets) {
    final Collection<Gadget> gadgets = gameGadgets.values();
    return gadgets.stream()
        .filter(StreamUtils.isInstanceOf(SurvivorApparatus.class))
        .collect(Collectors.toSet());
  }

  private Map<String, Gadget> getUsedGadgets(
      @UnderInitialization GadgetLoadingMechanism this, final MurderRun plugin) {
    final Server server = plugin.getServer();
    final PluginManager pluginManager = server.getPluginManager();
    final Collection<Pair<Gadget, Constructor<Object>>> gadgetClasses = GADGET_LOOK_UP_MAP.values();
    final Map<String, Gadget> gadgets = new HashMap<>();
    for (final Pair<Gadget, Constructor<Object>> pair : gadgetClasses) {
      final Constructor<?> constructor = pair.second();
      final Gadget gadget = invokeGadgetConstructor(constructor);
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

  public static Map<String, Pair<Gadget, Constructor<Object>>> getGadgetLookUpMap() {
    return GADGET_LOOK_UP_MAP;
  }
}
