package io.github.pulsebeat02.murderrun.gadget;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.filters.FilterBasedOnInheritance;
import com.openpojo.reflection.impl.PojoClassFactory;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.immutable.NamespacedKeys;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.utils.ItemStackUtils;
import java.util.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

public final class MurderGadgetManager {

  private static final String GADGETS_PACKAGE = "io.github.pulsebeat02.murderrun.gadget";
  public static final Map<String, PojoMethod> GADGET_LOOK_UP_MAP = new HashMap<>();

  private final MurderRun plugin;
  private final MurderGame game;
  private final Map<String, MurderGadget> gameGadgets;

  public MurderGadgetManager(final MurderGame game) {
    this.game = game;
    this.plugin = game.getPlugin();
    this.gameGadgets = new HashMap<>();
  }

  public static void init(final MurderRun plugin) {
    final Class<MurderGadget> clazz = MurderGadget.class;
    final PojoClassFilter inheritance = new FilterBasedOnInheritance(clazz);
    final List<PojoClass> classes =
        PojoClassFactory.enumerateClassesByExtendingType(GADGETS_PACKAGE, clazz, inheritance);
    for (final PojoClass pojoClass : classes) {
      final PojoMethod constructor = getPojoConstructor(pojoClass);
      final MurderGadget gadget = invokeGadgetConstructor(plugin, constructor);
      final String name = gadget.getName();
      GADGET_LOOK_UP_MAP.put(name, constructor);
    }
  }

  private static PojoMethod getPojoConstructor(final PojoClass pojoClass) {
    final List<PojoMethod> methods = pojoClass.getPojoConstructors();
    final PojoMethod first = methods.getFirst();
    if (first == null) {
      throw new AssertionError(
          String.format("Couldn't find constructor of gadget class %s", pojoClass));
    }
    return first;
  }

  @SuppressWarnings("nullness")
  private static MurderGadget invokeGadgetConstructor(
      final MurderRun plugin, final PojoMethod constructor) {
    final Class<?>[] arguments = constructor.getParameterTypes();
    final MurderGadget gadget;
    if (arguments.length == 1 && arguments[0].equals(MurderGame.class)) {
      gadget = (MurderGadget) constructor.invoke(plugin);
    } else {
      gadget = (MurderGadget) constructor.invoke(null);
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
      final PojoMethod constructor = GADGET_LOOK_UP_MAP.get(data);
      if (constructor == null) {
        throw new AssertionError(String.format("Failed to get class for trap %s", data));
      }
      final MurderGadget gadget = invokeGadgetConstructor(this.plugin, constructor);
      if (gadget != null) {
        playerGadgets.add(gadget);
      }
    }
    return playerGadgets;
  }
}
