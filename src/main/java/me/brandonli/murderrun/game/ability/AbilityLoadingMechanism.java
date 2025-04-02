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

  private Set<Ability> getKillerAbilities(@UnderInitialization AbilityLoadingMechanism this, final Map<String, Ability> gameAbilities) {
    final Collection<Ability> abilities = gameAbilities.values();
    return abilities.stream().filter(StreamUtils.isInstanceOf(KillerAbility.class)).collect(Collectors.toSet());
  }

  private Set<Ability> getSurvivorAbilities(@UnderInitialization AbilityLoadingMechanism this, final Map<String, Ability> gameAbilities) {
    final Collection<Ability> abilities = gameAbilities.values();
    return abilities.stream().filter(StreamUtils.isInstanceOf(SurvivorAbility.class)).collect(Collectors.toSet());
  }

  public @Nullable Ability getAbilityFromStack(final ItemStack stack) {
    final String data = PDCUtils.getPersistentDataAttribute(stack, Keys.ABILITY_KEY_NAME, PersistentDataType.STRING);
    return data != null ? this.gameAbilities.get(data) : null;
  }

  public Ability getRandomInnocentAbility() {
    final List<Ability> abilities = this.survivorAbilities.stream().collect(StreamUtils.toShuffledList());
    return abilities.getFirst();
  }

  public Ability getRandomKillerAbility() {
    final List<Ability> abilities = this.killerAbilities.stream().collect(StreamUtils.toShuffledList());
    return abilities.getFirst();
  }

  public AbilityManager getManager() {
    return this.manager;
  }

  public Map<String, Ability> getGameAbilities() {
    return this.gameAbilities;
  }
}
