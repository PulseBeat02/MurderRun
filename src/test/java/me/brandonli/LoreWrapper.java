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
package me.brandonli;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class LoreWrapper {

  public static List<String> wrapLoreLines(String miniMessageInput, int maxWidth) {
    // Parse the minimessage string into a Component
    Component component = MiniMessage.miniMessage().deserialize(miniMessageInput);
    // Convert the component to a legacy formatted string (using § as color/format codes)
    String legacyString = LegacyComponentSerializer.legacySection().serialize(component);

    List<String> lines = new ArrayList<>();
    StringBuilder currentLine = new StringBuilder();
    // currentFormat holds the last active formatting codes in the current line.
    String currentFormat = "";

    // Split the legacy string on spaces to obtain words.
    String[] words = legacyString.split(" ");
    for (String word : words) {
      // If the current line is empty, then just try putting the word.
      String candidate = currentLine.length() == 0 ? word : currentLine.toString() + " " + word;
      // Check the effective length (ignoring formatting codes)
      if (getStrippedLength(candidate) > maxWidth) {
        // The word does not fit on this line.
        // Save the current line (if not empty)
        if (currentLine.length() > 0) {
          lines.add(currentLine.toString());
          // Compute active formatting codes from the current line so that the new line continues with them.
          currentFormat = getLastColors(currentLine.toString());
        }
        // Start a new line, pre-pending the active format codes.
        currentLine = new StringBuilder(currentFormat);
        currentLine.append(word);
      } else {
        // Otherwise add the word to the current line.
        if (currentLine.length() > 0) {
          currentLine.append(" ");
        }
        currentLine.append(word);
      }
    }
    // Add any remaining text as the final line.
    if (currentLine.length() > 0) {
      lines.add(currentLine.toString());
    }

    return lines;
  }

  /**
   * Returns the length of the string with formatting codes removed.
   * Formatting codes are assumed to be in the form of a section sign (§) followed by a single character.
   *
   * @param text the input legacy formatted string
   * @return the length of the string without formatting codes
   */
  private static int getStrippedLength(String text) {
    // Remove any legacy formatting codes (case-insensitive)
    return text.replaceAll("(?i)§[0-9A-FK-OR]", "").length();
  }

  /**
   * Returns the active formatting codes at the end of a legacy formatted string.
   * It iterates over the string to find all the formatting occurrences. The reset code (§r) clears all active formatting.
   *
   * @param text the legacy formatted string
   * @return a string containing the formatting codes that should be active at the end
   */
  private static String getLastColors(String text) {
    StringBuilder lastColors = new StringBuilder();
    // Use a regex pattern to find each formatting code.
    Pattern pattern = Pattern.compile("(?i)§[0-9A-FK-OR]");
    Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      String code = matcher.group();
      if (code.equalsIgnoreCase("§r")) {
        // Reset clears any previous codes.
        lastColors.setLength(0);
      } else {
        lastColors.append(code);
      }
    }
    return lastColors.toString();
  }

  // A simple main method to demonstrate the wrapping functionality.
  public static void main(String[] args) {
    String miniMessage =
      "<gray><dark_gray>THROW DOWN:</dark_gray> Makes all survivors bleed profusely permanently, leaving a trail behind for you to follow</gray>";
    int maxWidth = 40; // you can change the value to test different widths

    List<String> loreLines = wrapLoreLines(miniMessage, maxWidth);
    for (String line : loreLines) {
      // Each line is a legacy formatted line with color codes intact.
      System.out.println(line);
    }
  }
}
