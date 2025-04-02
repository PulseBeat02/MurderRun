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
package me.brandonli.murderrun.gui;

import static java.util.Objects.requireNonNull;

import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PatternGui extends Gui {

  private final Map<Character, GuiItem> pattern;

  public PatternGui(final int rows, final String title, final Set<InteractionModifier> interactionModifiers) {
    super(rows, title, interactionModifiers);
    this.pattern = new HashMap<>();
  }

  public void map(final Character character, final GuiItem item) {
    this.pattern.put(character, item);
  }

  public void popularize(final List<String> pattern) {
    for (int i = 0; i < pattern.size(); i++) {
      final String row = pattern.get(i);
      for (int j = 0; j < row.length(); j++) {
        final Character character = row.charAt(j);
        if (!this.pattern.containsKey(character)) {
          continue;
        }
        final GuiItem item = requireNonNull(this.pattern.get(character));
        this.setItem(i + 1, j + 1, item);
      }
    }
  }
}
