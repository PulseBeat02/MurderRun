package io.github.pulsebeat02.murderrun.game.gadget;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerApparatus;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorApparatus;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.StreamUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GadgetLoadingMechanism {

  private final GadgetManager manager;
  private final Map<String, Gadget> gameGadgets;
  private final Set<Gadget> killerGadgets;
  private final Set<Gadget> survivorGadgets;

  public GadgetLoadingMechanism(final GadgetManager manager) {
    final GadgetRegistry instance = GadgetRegistry.getRegistry();
    final MurderRun run = manager.getPlugin();
    this.manager = manager;
    this.gameGadgets = instance.getUsedGadgets(manager, run);
    this.killerGadgets = this.getKillerGadgets(this.gameGadgets);
    this.survivorGadgets = this.getSurvivorGadgets(this.gameGadgets);
  }

  private Set<Gadget> getKillerGadgets(@UnderInitialization GadgetLoadingMechanism this, final Map<String, Gadget> gameGadgets) {
    final Collection<Gadget> gadgets = gameGadgets.values();
    return gadgets.stream().filter(StreamUtils.isInstanceOf(KillerApparatus.class)).collect(Collectors.toSet());
  }

  private Set<Gadget> getSurvivorGadgets(@UnderInitialization GadgetLoadingMechanism this, final Map<String, Gadget> gameGadgets) {
    final Collection<Gadget> gadgets = gameGadgets.values();
    return gadgets.stream().filter(StreamUtils.isInstanceOf(SurvivorApparatus.class)).collect(Collectors.toSet());
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
    final String data = PDCUtils.getPersistentDataAttribute(stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING);
    return data != null ? this.gameGadgets.get(data) : null;
  }

  public Gadget getRandomInnocentGadget() {
    final List<Gadget> gadgets = this.survivorGadgets.stream().collect(StreamUtils.toShuffledList());
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
}
