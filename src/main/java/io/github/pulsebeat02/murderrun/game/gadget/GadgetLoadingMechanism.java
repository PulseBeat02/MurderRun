package io.github.pulsebeat02.murderrun.game.gadget;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerApparatus;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadgets;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorApparatus;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadgets;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import io.github.pulsebeat02.murderrun.utils.StreamUtils;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.type.tuple.Pair;

public final class GadgetLoadingMechanism {

  private static final Map<String, Pair<Gadget, MethodHandle>> GADGET_LOOK_UP_MAP = new HashMap<>();

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
    try {
      final MethodHandle handle = getMethodHandleClass(clazz);
      final Gadget gadget = invokeGadgetConstructor(handle, null);
      final String name = gadget.getName();
      final Pair<Gadget, MethodHandle> pair = Pair.of(gadget, handle);
      GADGET_LOOK_UP_MAP.put(name, pair);
    } catch (final Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private static MethodHandle getMethodHandleClass(final Class<?> clazz) throws Throwable {
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

  private static Gadget invokeGadgetConstructor(
      final MethodHandle handle, final @Nullable Game game) {
    try {
      final MethodType type = handle.type();
      final int count = type.parameterCount();
      return count == 0 ? (Gadget) handle.invoke() : (Gadget) handle.invoke((@NonNull Object) game);
    } catch (final Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private final GadgetManager manager;
  private final Map<String, Gadget> gameGadgets;
  private final Set<Gadget> killerGadgets;
  private final Set<Gadget> survivorGadgets;

  public GadgetLoadingMechanism(final GadgetManager manager) {
    final MurderRun run = manager.getPlugin();
    this.manager = manager;
    this.gameGadgets = this.getUsedGadgets(manager, run);
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
      @UnderInitialization GadgetLoadingMechanism this,
      final GadgetManager manager,
      final MurderRun plugin) {
    final Game game = manager.getGame();
    final Server server = plugin.getServer();
    final PluginManager pluginManager = server.getPluginManager();
    final Collection<Pair<Gadget, MethodHandle>> gadgetClasses = GADGET_LOOK_UP_MAP.values();
    final Map<String, Gadget> gadgets = new HashMap<>();
    for (final Pair<Gadget, MethodHandle> pair : gadgetClasses) {
      final MethodHandle constructor = pair.second();
      final Gadget gadget = invokeGadgetConstructor(constructor, game);
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
    final String data = ItemUtils.getPersistentDataAttribute(
        stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING);
    return data != null ? this.gameGadgets.get(data) : null;
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

  public static Map<String, Pair<Gadget, MethodHandle>> getGadgetLookUpMap() {
    return GADGET_LOOK_UP_MAP;
  }
}
