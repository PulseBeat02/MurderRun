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
package me.brandonli.murderrun.game.lobby.player;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.ability.Ability;
import me.brandonli.murderrun.game.ability.AbilityRegistry;
import me.brandonli.murderrun.game.ability.killer.KillerAbility;
import me.brandonli.murderrun.game.ability.survivor.SurvivorAbility;
import me.brandonli.murderrun.game.gadget.Gadget;
import me.brandonli.murderrun.game.gadget.GadgetRegistry;
import me.brandonli.murderrun.game.gadget.killer.KillerDevice;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorDevice;
import me.brandonli.murderrun.gui.ability.selection.AbilitySelectGui;
import me.brandonli.murderrun.gui.gadget.shop.GadgetShopGui;
import me.brandonli.murderrun.utils.RandomUtils;
import org.bukkit.entity.Player;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class PlayerSelection {

  private final Player player;
  private final boolean killer;

  private final GadgetShopGui gadgetShopGui;
  private final AbilitySelectGui abilitySelectGui;

  public PlayerSelection(final MurderRun plugin, final GameProperties properties, final Player player, final boolean killer) {
    this.player = player;
    this.killer = killer;
    this.gadgetShopGui = new GadgetShopGui(plugin, properties, player, this.getRandomGadgets(properties, killer));
    this.abilitySelectGui = new AbilitySelectGui(plugin, properties, player, this.getRandomAbilities(properties, killer));
  }

  private List<String> getRandomGadgets(@UnderInitialization PlayerSelection this, final GameProperties properties, final boolean killer) {
    final boolean random = properties.getGameUtilitiesRandom();
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    final Collection<Gadget> gadgetList = registry.getGadgets();
    final Stream<Gadget> stream = gadgetList.stream().filter(this.getGadgetPredicate(killer));
    final Collection<Gadget> gadgetCollection = stream.toList();
    if (random) {
      final Set<String> currentList = new HashSet<>();
      final int count = killer ? properties.getGameUtilitiesKillerGadgets() : properties.getGameUtilitiesSurvivorGadgets();
      while (currentList.size() < count) {
        final Gadget randomGadget = RandomUtils.getRandomElement(gadgetCollection);
        final String name = randomGadget.getId();
        if (currentList.contains(name)) {
          continue;
        }
        currentList.add(name);
      }
      return List.copyOf(currentList);
    } else {
      return gadgetCollection.stream().map(Gadget::getId).toList();
    }
  }

  private Predicate<Gadget> getGadgetPredicate(@UnderInitialization PlayerSelection this, final boolean killer) {
    return gadget -> killer ? gadget instanceof KillerDevice : gadget instanceof SurvivorDevice;
  }

  private List<String> getRandomAbilities(
    @UnderInitialization PlayerSelection this,
    final GameProperties properties,
    final boolean killer
  ) {
    final boolean random = properties.getGameUtilitiesRandom();
    final AbilityRegistry registry = AbilityRegistry.getRegistry();
    final Collection<Ability> abilityList = registry.getAbilities();
    final Stream<Ability> stream = abilityList.stream().filter(this.getAbilityPredicate(killer));
    final Collection<Ability> abilityCollection = stream.toList();
    if (random) {
      final Set<String> currentList = new HashSet<>();
      final int count = killer ? properties.getGameUtilitiesKillerAbilities() : properties.getGameUtilitiesSurvivorAbilities();
      while (currentList.size() < count) {
        final Ability randomAbility = RandomUtils.getRandomElement(abilityCollection);
        final String name = randomAbility.getId();
        if (currentList.contains(name)) {
          continue;
        }
        currentList.add(name);
      }
      return List.copyOf(currentList);
    } else {
      return abilityCollection.stream().map(Ability::getId).toList();
    }
  }

  private Predicate<Ability> getAbilityPredicate(@UnderInitialization PlayerSelection this, final boolean killer) {
    return ability -> killer ? ability instanceof KillerAbility : ability instanceof SurvivorAbility;
  }

  public Player getPlayer() {
    return this.player;
  }

  public boolean isKiller() {
    return this.killer;
  }

  public GadgetShopGui getGadgetShopGui() {
    return this.gadgetShopGui;
  }

  public AbilitySelectGui getAbilitySelectGui() {
    return this.abilitySelectGui;
  }
}
