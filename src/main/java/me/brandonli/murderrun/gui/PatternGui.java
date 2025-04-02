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
