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
package me.brandonli.murderrun.secret;

import java.util.SplittableRandom;
import javax.swing.*;
import me.brandonli.murderrun.secret.minesweeper.Minesweeper;
import me.brandonli.murderrun.secret.rick.RickRoll;

public final class Main {

  private static final SplittableRandom RANDOM = new SplittableRandom();

  public static void main(final String[] args) {
    SwingUtilities.invokeLater(Main::invokeRandomMenu);
  }

  private static void invokeRandomMenu() {
    final int random = RANDOM.nextInt(2);
    switch (random) {
      case 0 -> invokeMinesweeperMenu();
      case 1 -> invokeRickRollMenu();
      default -> throw new IllegalStateException();
    }
  }

  private static void invokeRickRollMenu() {
    final RickRoll rickRoll = new RickRoll();
    rickRoll.setVisible(true);
  }

  private static void invokeMinesweeperMenu() {
    final JFrame minesweeper = new Minesweeper();
    minesweeper.setVisible(true);
  }
}
