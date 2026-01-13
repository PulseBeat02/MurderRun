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
package me.brandonli.murderrun.game.gadget;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.utils.ClassGraphUtils;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
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
    final GameProperties properties = GameProperties.COMMON;
    final String raw = properties.getDisabledGadgets();
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
    final String name = gadget.getId();
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
      final String name = gadget.getId();
      gadgets.put(name, gadget);
    }
    return gadgets;
  }

  @SuppressWarnings("all") // checker
  private void load() {
    final ScanResult result = ClassGraphUtils.getCachedScanResult();
    final ClassInfoList list = result.getClassesImplementing(Gadget.class);
    final ClassInfoList implementations = list.getStandardClasses();
    for (final ClassInfo info : implementations) {
      if (!info.isAbstract()) {
        final Class<?> loaded = info.loadClass();
        this.handleGadgetClass(loaded);
      }
    }

    final GadgetDisabler handler = new GadgetDisabler();
    handler.disableGadgets(this);
  }

  private void handleGadgetClass(final Class<?> clazz) {
    try {
      final MethodHandle handle = this.getMethodHandleClass(clazz);
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
      final Game dummy = new Game(plugin, GameProperties.DEFAULT); // dummy for init only
      final Gadget gadget = this.invokeGadgetConstructor(handle, dummy);
      final String name = gadget.getId();
      if (this.disabled.contains(name)) {
        return;
      }
      final Pair<Gadget, MethodHandle> pair = Pair.of(gadget, handle);
      this.gadgetRegistry.put(name, pair);
    } catch (final NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  private MethodHandle getMethodHandleClass(final Class<?> clazz) throws NoSuchMethodException, IllegalAccessException {
    final MethodHandles.Lookup lookup = MethodHandles.lookup();
    try {
      final MethodType type = MethodType.methodType(Void.TYPE);
      return lookup.findConstructor(clazz, type);
    } catch (final Exception e) {
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
