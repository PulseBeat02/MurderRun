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
package io.github.pulsebeat02.murderrun.game.lobby.player;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.ability.Ability;
import io.github.pulsebeat02.murderrun.game.ability.AbilityRegistry;
import io.github.pulsebeat02.murderrun.game.ability.killer.KillerAbility;
import io.github.pulsebeat02.murderrun.game.ability.survivor.SurvivorAbility;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetRegistry;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerDevice;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorDevice;
import io.github.pulsebeat02.murderrun.gui.ability.selection.AbilitySelectGui;
import io.github.pulsebeat02.murderrun.gui.gadget.shop.GadgetShopGui;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.bukkit.entity.Player;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class PlayerSelection {

  private final Player player;
  private final boolean killer;

  private final GadgetShopGui gadgetShopGui;
  private final AbilitySelectGui abilitySelectGui;

  public PlayerSelection(final MurderRun plugin, final Player player, final boolean killer) {
    this.player = player;
    this.killer = killer;
    this.gadgetShopGui = new GadgetShopGui(plugin, this.getRandomGadgets(killer));
    this.abilitySelectGui = new AbilitySelectGui(plugin, this.getRandomAbilities(killer));
  }

  private List<String> getRandomGadgets(@UnderInitialization PlayerSelection this, final boolean killer) {
    final boolean random = GameProperties.GAME_UTILITIES_RANDOM;
    final GadgetRegistry registry = GadgetRegistry.getRegistry();
    final Collection<Gadget> gadgetList = registry.getGadgets();
    final Stream<Gadget> stream = gadgetList.stream().filter(this.getGadgetPredicate(killer));
    final Collection<Gadget> gadgetCollection = stream.toList();
    if (random) {
      final Set<String> currentList = new HashSet<>();
      final int count = killer ? GameProperties.GAME_UTILITIES_KILLER_GADGETS : GameProperties.GAME_UTILITIES_SURVIVOR_GADGETS;
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

  private List<String> getRandomAbilities(@UnderInitialization PlayerSelection this, final boolean killer) {
    final boolean random = GameProperties.GAME_UTILITIES_RANDOM;
    final AbilityRegistry registry = AbilityRegistry.getRegistry();
    final Collection<Ability> abilityList = registry.getAbilities();
    final Stream<Ability> stream = abilityList.stream().filter(this.getAbilityPredicate(killer));
    final Collection<Ability> abilityCollection = stream.toList();
    if (random) {
      final Set<String> currentList = new HashSet<>();
      final int count = killer ? GameProperties.GAME_UTILITIES_KILLER_ABILITIES : GameProperties.GAME_UTILITIES_SURVIVOR_ABILITIES;
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
