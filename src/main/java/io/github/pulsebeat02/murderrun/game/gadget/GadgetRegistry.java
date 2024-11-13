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
package io.github.pulsebeat02.murderrun.game.gadget;

import static java.util.Objects.requireNonNull;

import com.google.common.reflect.ClassPath;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.type.tuple.Pair;

public final class GadgetRegistry {

  private static final GadgetRegistry GLOBAL_GADGET_REGISTRY = new GadgetRegistry();

  public static void init() {
    // load registry
  }

  private final Map<String, Pair<Gadget, MethodHandle>> gadgetRegistry;
  private final Collection<String> disabled;
  private final AtomicBoolean frozen;

  private GadgetRegistry() {
    this.gadgetRegistry = new HashMap<>();
    this.frozen = new AtomicBoolean(true);
    this.disabled = this.getDisabledGadgets();
    this.load();
  }

  private Collection<String> getDisabledGadgets(@UnderInitialization GadgetRegistry this) {
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

  public Map<String, Gadget> getUsedGadgets(final GadgetManager manager, final MurderRun plugin) {
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
    final Class<?> clazz = this.getClass();
    final ClassLoader loader = requireNonNull(clazz.getClassLoader());
    try {
      final ClassPath classPath = ClassPath.from(loader);
      final Set<ClassPath.ClassInfo> classes = classPath.getAllClasses();
      classes.stream().parallel().forEach(this::loadClassInfo);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
    final GadgetDisabler handler = new GadgetDisabler();
    handler.disableGadgets(this);
  }

  private void loadClassInfo(final ClassPath.ClassInfo info) {
    try {
      final Class<?> classObject = info.load();
      if (Gadget.class.isAssignableFrom(classObject)) {
        this.handleGadgetClass(classObject);
      }
    } catch (final Throwable ignored) {}
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
      throw new AssertionError(e);
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

  @SuppressWarnings("all") // checker
  private Gadget invokeGadgetConstructor(final MethodHandle handle, final @Nullable Game game) {
    try {
      final MethodType type = handle.type();
      final int count = type.parameterCount();
      return (Gadget) (count == 0 ? handle.invoke() : handle.invoke(game));
    } catch (final Throwable e) {
      throw new AssertionError(e);
    }
  }

  public static GadgetRegistry getRegistry() {
    return GLOBAL_GADGET_REGISTRY;
  }
}
