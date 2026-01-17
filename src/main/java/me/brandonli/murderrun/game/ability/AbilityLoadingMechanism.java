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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.ability.killer.KillerAbility;
import me.brandonli.murderrun.game.ability.survivor.SurvivorAbility;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.StreamUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class AbilityLoadingMechanism {

  private final AbilityManager manager;
  private final Map<String, Ability> gameAbilities;
  private final Set<Ability> killerAbilities;
  private final Set<Ability> survivorAbilities;

  public AbilityLoadingMechanism(final AbilityManager manager) {
    final AbilityRegistry instance = AbilityRegistry.getRegistry();
    final MurderRun run = manager.getPlugin();
    this.manager = manager;
    this.gameAbilities = instance.getUsedAbilities(manager, run);
    this.killerAbilities = this.getKillerAbilities(this.gameAbilities);
    this.survivorAbilities = this.getSurvivorAbilities(this.gameAbilities);
  }

  public void shutdown() {
    final Collection<Ability> values = this.gameAbilities.values();
    for (final Ability ability : values) {
      if (!(ability instanceof final Listener listener)) {
        continue;
      }
      HandlerList.unregisterAll(listener);
    }
  }

  private Set<Ability> getKillerAbilities(
      @UnderInitialization AbilityLoadingMechanism this, final Map<String, Ability> gameAbilities) {
    final Collection<Ability> abilities = gameAbilities.values();
    return abilities.stream()
        .filter(StreamUtils.isInstanceOf(KillerAbility.class))
        .collect(Collectors.toSet());
  }

  private Set<Ability> getSurvivorAbilities(
      @UnderInitialization AbilityLoadingMechanism this, final Map<String, Ability> gameAbilities) {
    final Collection<Ability> abilities = gameAbilities.values();
    return abilities.stream()
        .filter(StreamUtils.isInstanceOf(SurvivorAbility.class))
        .collect(Collectors.toSet());
  }

  public @Nullable Ability getAbilityFromStack(final ItemStack stack) {
    final String data = PDCUtils.getPersistentDataAttribute(
        stack, Keys.ABILITY_KEY_NAME, PersistentDataType.STRING);
    return data != null ? this.gameAbilities.get(data) : null;
  }

  public Ability getRandomInnocentAbility() {
    final List<Ability> abilities =
        this.survivorAbilities.stream().collect(StreamUtils.toShuffledList());
    return abilities.getFirst();
  }

  public Ability getRandomKillerAbility() {
    final List<Ability> abilities =
        this.killerAbilities.stream().collect(StreamUtils.toShuffledList());
    return abilities.getFirst();
  }

  public AbilityManager getManager() {
    return this.manager;
  }

  public Map<String, Ability> getGameAbilities() {
    return this.gameAbilities;
  }
}
