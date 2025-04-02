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
package me.brandonli.murderrun.secret.minesweeper;

import java.awt.BorderLayout;
import javax.swing.*;

public final class Minesweeper extends JFrame {

  public Minesweeper() {
    final JLabel statusbar = new JLabel("");
    final MinefieldBoard board = createMinefieldBoard(statusbar);
    this.add(statusbar, BorderLayout.SOUTH);
    this.add(board);
    this.setResizable(false);
    this.setTitle("Minesweeper");
    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.pack();
  }

  private MinefieldBoard createMinefieldBoard(final JLabel statusbar) {
    final MinefieldBoard board = new MinefieldBoard(statusbar);
    board.initBoard();
    return board;
  }
}
