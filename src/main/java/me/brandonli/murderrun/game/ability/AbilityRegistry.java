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
package me.brandonli.murderrun.game.ability;

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

public final class AbilityRegistry {

  private static final AbilityRegistry GLOBAL_ABILITY_REGISTRY = new AbilityRegistry();

  public static void init() {
    // load registry
  }

  private final Map<String, Pair<Ability, MethodHandle>> abilityRegistry;
  private final Collection<String> disabled;
  private final AtomicBoolean frozen;

  private AbilityRegistry() {
    this.abilityRegistry = new HashMap<>();
    this.frozen = new AtomicBoolean(true);
    this.disabled = this.getDisabledAbilities();
    this.load();
  }

  private Collection<String> getDisabledAbilities(@UnderInitialization AbilityRegistry this) {
    final GameProperties properties = GameProperties.COMMON;
    final String raw = properties.getDisabledAbilities();
    final String[] split = raw.split(",");
    if (split[0].equals("none")) {
      return List.of();
    } else {
      return Arrays.asList(split);
    }
  }

  public void addAbility(final Ability ability) {
    this.checkState();
    final Class<?> clazz = ability.getClass();
    this.handleAbilityClass(clazz);
  }

  public void removeAbility(final Ability ability) {
    final String name = ability.getId();
    this.removeAbility(name);
  }

  public void removeAbility(final String abilityName) {
    this.checkState();
    this.abilityRegistry.remove(abilityName);
  }

  public @Nullable Ability getAbility(final String name) {
    final Pair<Ability, MethodHandle> pair = this.abilityRegistry.get(name);
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
      throw new IllegalStateException("Cannot modify the ability registry while it is frozen!");
    }
  }

  public Collection<Ability> getAbilities() {
    return this.abilityRegistry.values().stream().map(Pair::first).toList();
  }

  public Map<String, Ability> getUsedAbilities(final AbilityManager manager, final MurderRun plugin) {
    final Game game = manager.getGame();
    final Server server = plugin.getServer();
    final PluginManager pluginManager = server.getPluginManager();
    final Collection<Pair<Ability, MethodHandle>> abilityRegistry = this.abilityRegistry.values();
    final Map<String, Ability> abilities = new HashMap<>();
    for (final Pair<Ability, MethodHandle> pair : abilityRegistry) {
      final MethodHandle constructor = pair.second();
      final Ability ability = this.invokeAbilityConstructor(constructor, game);
      if (ability instanceof final Listener listener) {
        pluginManager.registerEvents(listener, plugin);
      }
      final String name = ability.getId();
      abilities.put(name, ability);
    }
    return abilities;
  }

  private void load() {
    final ScanResult result = ClassGraphUtils.getCachedScanResult();
    final ClassInfoList list = result.getClassesImplementing(Ability.class);
    final ClassInfoList implementations = list.getStandardClasses();
    for (final ClassInfo info : implementations) {
      if (!info.isAbstract()) {
        final Class<?> loaded = info.loadClass();
        this.handleAbilityClass(loaded);
      }
    }

    final AbilityDisabler handler = new AbilityDisabler();
    handler.disableAbilities(this);
  }

  private void handleAbilityClass(final Class<?> clazz) {
    try {
      final MethodHandle handle = this.getMethodHandleClass(clazz);
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
      final Game dummy = new Game(plugin, GameProperties.DEFAULT); // dummy for init only
      final Ability ability = this.invokeAbilityConstructor(handle, dummy);
      final String name = ability.getId();
      if (this.disabled.contains(name)) {
        return;
      }
      final Pair<Ability, MethodHandle> pair = Pair.of(ability, handle);
      this.abilityRegistry.put(name, pair);
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
  private Ability invokeAbilityConstructor(final MethodHandle handle, final Game game) {
    try {
      return (Ability) handle.invoke(game);
    } catch (final Throwable e) {
      throw new AssertionError(e);
    }
  }

  public static AbilityRegistry getRegistry() {
    return GLOBAL_ABILITY_REGISTRY;
  }
}
