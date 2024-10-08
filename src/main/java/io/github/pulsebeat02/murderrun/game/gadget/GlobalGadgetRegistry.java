package io.github.pulsebeat02.murderrun.game.gadget;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadgets;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadgets;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.type.tuple.Pair;

public final class GlobalGadgetRegistry {

  private static final GlobalGadgetRegistry GLOBAL_GADGET_REGISTRY = new GlobalGadgetRegistry();

  public static void init() {
    // load registry
  }

  private final Map<String, Pair<Gadget, MethodHandle>> gadgetRegistry;
  private final Collection<String> disabled;
  private final AtomicBoolean frozen;

  private GlobalGadgetRegistry() {
    this.gadgetRegistry = new HashMap<>();
    this.frozen = new AtomicBoolean(true);
    this.disabled = this.getDisabledGadgets();
    this.load();
  }

  private Collection<String> getDisabledGadgets(@UnderInitialization GlobalGadgetRegistry this) {
    final String raw = GameProperties.DISABLED_GADGETS;
    final String[] split = raw.split(",");
    if (split[0].equals("none")) {
      return List.of();
    } else {
      return Arrays.asList(split);
    }
  }

  public void addGadget(final Gadget gadget) {
    this.checkState();
    final Class<?> clazz = gadget.getClass();
    this.handleGadgetClass(clazz);
  }

  public void removeGadget(final Gadget gadget) {
    final String name = gadget.getName();
    this.removeGadget(name);
  }

  public void removeGadget(final String gadgetName) {
    this.checkState();
    this.gadgetRegistry.remove(gadgetName);
  }

  public @Nullable Gadget getGadget(final String name) {
    final Pair<Gadget, MethodHandle> pair = this.gadgetRegistry.get(name);
    if (pair == null) {
      return null;
    }
    return pair.first();
  }

  public void unfreeze() {
    this.frozen.set(false);
  }

  public void freeze() {
    this.frozen.set(true);
  }

  public boolean isFrozen() {
    return this.frozen.get();
  }

  private void checkState() {
    final boolean state = this.frozen.get();
    if (state) {
      throw new IllegalStateException("Cannot modify the gadget registry while it is frozen!");
    }
  }

  public Collection<Gadget> getGadgets() {
    return this.gadgetRegistry.values().stream().map(Pair::first).toList();
  }

  Map<String, Gadget> getUsedGadgets(final GadgetManager manager, final MurderRun plugin) {
    final Game game = manager.getGame();
    final Server server = plugin.getServer();
    final PluginManager pluginManager = server.getPluginManager();
    final Collection<Pair<Gadget, MethodHandle>> gadgetClasses = this.gadgetRegistry.values();
    final Map<String, Gadget> gadgets = new HashMap<>();
    for (final Pair<Gadget, MethodHandle> pair : gadgetClasses) {
      final MethodHandle constructor = pair.second();
      final Gadget gadget = this.invokeGadgetConstructor(constructor, game);
      if (gadget instanceof final Listener listener) {
        pluginManager.registerEvents(listener, plugin);
      }
      final String name = gadget.getName();
      gadgets.put(name, gadget);
    }
    return gadgets;
  }

  private void load() {
    final SurvivorGadgets[] survivorGadgets = SurvivorGadgets.values();
    final KillerGadgets[] killerGadgets = KillerGadgets.values();
    for (final SurvivorGadgets gadget : survivorGadgets) {
      final Class<?> clazz = gadget.getClazz();
      this.handleGadgetClass(clazz);
    }
    for (final KillerGadgets gadget : killerGadgets) {
      final Class<?> clazz = gadget.getClazz();
      this.handleGadgetClass(clazz);
    }

    final DependencyGadgetHandler handler = new DependencyGadgetHandler();
    handler.disableGadgets(this);
  }

  private void handleGadgetClass(final Class<?> clazz) {
    try {
      final MethodHandle handle = this.getMethodHandleClass(clazz);
      final Gadget gadget = this.invokeGadgetConstructor(handle, null);
      final String name = gadget.getName();
      if (this.disabled.contains(name)) {
        return;
      }
      final Pair<Gadget, MethodHandle> pair = Pair.of(gadget, handle);
      this.gadgetRegistry.put(name, pair);
    } catch (final Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private MethodHandle getMethodHandleClass(final Class<?> clazz) throws Throwable {
    final MethodHandles.Lookup lookup = MethodHandles.lookup();
    try {
      final MethodType type = MethodType.methodType(Void.TYPE);
      return lookup.findConstructor(clazz, type);
    } catch (final Throwable e) {
      // if invalid, inject the game
      final MethodType injectGame = MethodType.methodType(Void.TYPE, Game.class);
      return lookup.findConstructor(clazz, injectGame);
    }
  }

  @SuppressWarnings("all")
  private Gadget invokeGadgetConstructor(final MethodHandle handle, final @Nullable Game game) {
    try {
      final MethodType type = handle.type();
      final int count = type.parameterCount();
      return count == 0 ? (Gadget) handle.invoke() : (Gadget) handle.invoke(game);
    } catch (final Throwable e) {
      throw new RuntimeException(e);
    }
  }

  public static GlobalGadgetRegistry getRegistry() {
    return GLOBAL_GADGET_REGISTRY;
  }
}
