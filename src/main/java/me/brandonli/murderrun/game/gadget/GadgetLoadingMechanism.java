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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.gadget.killer.KillerDevice;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorDevice;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.StreamUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
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

  private Set<Gadget> getKillerGadgets(
      @UnderInitialization GadgetLoadingMechanism this, final Map<String, Gadget> gameGadgets) {
    final Collection<Gadget> gadgets = gameGadgets.values();
    return gadgets.stream()
        .filter(StreamUtils.isInstanceOf(KillerDevice.class))
        .collect(Collectors.toSet());
  }

  private Set<Gadget> getSurvivorGadgets(
      @UnderInitialization GadgetLoadingMechanism this, final Map<String, Gadget> gameGadgets) {
    final Collection<Gadget> gadgets = gameGadgets.values();
    return gadgets.stream()
        .filter(StreamUtils.isInstanceOf(SurvivorDevice.class))
        .collect(Collectors.toSet());
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
    final String data =
        PDCUtils.getPersistentDataAttribute(stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING);
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
}
