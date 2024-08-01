package io.github.pulsebeat02.murderrun.gadget;

import com.google.common.reflect.ClassPath;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.immutable.NamespacedKeys;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.utils.ItemStackUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

public final class MurderGadgetManager {

  private static final String GADGETS_PACKAGE = "io.github.pulsebeat02.murderrun.gadget";
  public static final Map<String, Constructor<?>> GADGET_LOOK_UP_MAP = new HashMap<>();

  private final MurderRun plugin;
  private final MurderGame game;
  private final Map<String, MurderGadget> gameGadgets;

  public MurderGadgetManager(final MurderGame game) {
    this.game = game;
    this.plugin = game.getPlugin();
    this.gameGadgets = new HashMap<>();
  }

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

  public void registerNecessaryGadgets() {
    final MurderPlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllParticipants(participant -> {
      final Set<MurderGadget> gadgets = this.getUsedGadgets(participant);
      for (final MurderGadget gadget : gadgets) {
        final String name = gadget.getName();
        this.gameGadgets.put(name, gadget);
      }
    });
  }

  private Set<MurderGadget> getUsedGadgets(final GamePlayer player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack[] slots = inventory.getContents();
    final Set<MurderGadget> playerGadgets = new HashSet<>();
    for (final ItemStack slot : slots) {
      if (!ItemStackUtils.isTrap(slot)) {
        continue;
      }
      final String data =
          ItemStackUtils.getData(slot, NamespacedKeys.TRAP_KEY_NAME, PersistentDataType.STRING);
      if (data == null) {
        throw new AssertionError("An error occurred while retrieving from PDC!");
      }
      final Constructor<?> constructor = GADGET_LOOK_UP_MAP.get(data);
      if (constructor == null) {
        throw new AssertionError(String.format("Failed to get class for trap %s", data));
      }
      try {
        final MurderGadget gadget = invokeGadgetConstructor(this.plugin, constructor);
        playerGadgets.add(gadget);
      } catch (final Exception e) {
        throw new RuntimeException(e);
      }
    }
    return playerGadgets;
  }
}
