/*

MIT License

Copyright (c) 2025 Brandon Li

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
package io.github.pulsebeat02.murderrun.game.ability;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.utils.ClassGraphUtils;
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
    final String raw = GameProperties.DISABLED_ABILITIES;
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

  @SuppressWarnings("all") // checker
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
      final Ability ability = this.invokeAbilityConstructor(handle, null);
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
  private Ability invokeAbilityConstructor(final MethodHandle handle, final @Nullable Game game) {
    try {
      final MethodType type = handle.type();
      return (Ability) handle.invoke(game);
    } catch (final Throwable e) {
      throw new AssertionError(e);
    }
  }

  public static AbilityRegistry getRegistry() {
    return GLOBAL_ABILITY_REGISTRY;
  }
}
