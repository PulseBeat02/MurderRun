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

import static java.util.Objects.requireNonNull;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.SplittableRandom;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class MinefieldBoard extends JPanel {

  private static final SplittableRandom RANDOM = new SplittableRandom();
  private static final String IMAGE_PATH_FORMAT = "/secret/%d.png";
  private static final int NUM_IMAGES = 13;
  private static final int CELL_SIZE = 15;
  private static final int COVER_FOR_CELL = 10;
  private static final int MARK_FOR_CELL = 10;
  private static final int EMPTY_CELL = 0;
  private static final int MINE_CELL = 9;
  private static final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
  private static final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;
  private static final int DRAW_MINE = 9;
  private static final int DRAW_COVER = 10;
  private static final int DRAW_MARK = 11;
  private static final int DRAW_WRONG_MARK = 12;
  private static final int N_MINES = 40;
  private static final int N_ROWS = 16;
  private static final int N_COLS = 16;
  private static final int BOARD_WIDTH = N_COLS * CELL_SIZE + 1;
  private static final int BOARD_HEIGHT = N_ROWS * CELL_SIZE + 1;

  private int[] field;
  private boolean inGame;
  private int minesLeft;

  private transient Image[] img;

  private int allCells;
  private final JLabel statusbar;

  public MinefieldBoard(final JLabel statusbar) {
    this.statusbar = statusbar;
  }

  public void initBoard() {
    final Dimension boardSize = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    final MinesAdapter adapter = new MinesAdapter();
    this.setPreferredSize(boardSize);
    this.loadImages();
    this.addMouseListener(adapter);
    this.newGame();
  }

  private void loadImages() {
    this.img = new Image[NUM_IMAGES];
    for (int i = 0; i < NUM_IMAGES; i++) {
      final String path = IMAGE_PATH_FORMAT.formatted(i);
      final URL url = requireNonNull(MinefieldBoard.class.getResource(path));
      final ImageIcon imageIcon = new ImageIcon(url);
      this.img[i] = imageIcon.getImage();
    }
  }

  private void newGame() {
    final String left = Integer.toString(N_MINES);
    this.inGame = true;
    this.minesLeft = N_MINES;
    this.allCells = N_ROWS * N_COLS;
    this.field = new int[this.allCells];
    this.statusbar.setText(left);
    this.initializeField();
    this.placeMines();
  }

  private void initializeField() {
    for (int i = 0; i < this.allCells; i++) {
      this.field[i] = COVER_FOR_CELL;
    }
  }

  private void placeMines() {
    int i = 0;
    while (i < N_MINES) {
      final int position = RANDOM.nextInt(this.allCells);
      if (this.field[position] != COVERED_MINE_CELL) {
        this.field[position] = COVERED_MINE_CELL;
        this.incrementSurroundingCells(position);
        i++;
      }
    }
  }

  private void incrementSurroundingCells(final int position) {
    final int current_col = position % N_COLS;
    this.incrementCell(position - 1 - N_COLS, current_col > 0);
    this.incrementCell(position - 1, current_col > 0);
    this.incrementCell(position + N_COLS - 1, current_col > 0);
    this.incrementCell(position - N_COLS, true);
    this.incrementCell(position + N_COLS, true);
    this.incrementCell(position - N_COLS + 1, current_col < (N_COLS - 1));
    this.incrementCell(position + N_COLS + 1, current_col < (N_COLS - 1));
    this.incrementCell(position + 1, current_col < (N_COLS - 1));
  }

  private void incrementCell(final int cell, final boolean condition) {
    if (condition && cell >= 0 && cell < this.allCells && this.field[cell] != COVERED_MINE_CELL) {
      this.field[cell]++;
    }
  }

  private void find_empty_cells(final int j) {
    final int current_col = j % N_COLS;
    if (current_col > 0) {
      this.checkAndUncoverCell(j - N_COLS - 1);
      this.checkAndUncoverCell(j - 1);
      this.checkAndUncoverCell(j + N_COLS - 1);
    }

    this.checkAndUncoverCell(j - N_COLS);
    this.checkAndUncoverCell(j + N_COLS);

    if (current_col < (N_COLS - 1)) {
      this.checkAndUncoverCell(j - N_COLS + 1);
      this.checkAndUncoverCell(j + N_COLS + 1);
      this.checkAndUncoverCell(j + 1);
    }
  }

  private void checkAndUncoverCell(final int cell) {
    if (cell >= 0 && cell < this.allCells && this.field[cell] > MINE_CELL) {
      this.field[cell] -= COVER_FOR_CELL;
      if (this.field[cell] == EMPTY_CELL) {
        this.find_empty_cells(cell);
      }
    }
  }

  @Override
  public void paintComponent(final Graphics g) {
    int uncover = 0;
    for (int i = 0; i < N_ROWS; i++) {
      for (int j = 0; j < N_COLS; j++) {
        int cell = this.field[(i * N_COLS) + j];
        cell = this.updateCellState(cell);
        if (cell == DRAW_COVER) {
          uncover++;
        }
        g.drawImage(this.img[cell], (j * CELL_SIZE), (i * CELL_SIZE), this);
      }
    }
    this.updateGameStatus(uncover);
  }

  private int updateCellState(int cell) {
    if (this.inGame && cell == MINE_CELL) {
      this.inGame = false;
    }
    if (!this.inGame) {
      if (cell == COVERED_MINE_CELL) {
        cell = DRAW_MINE;
      } else if (cell == MARKED_MINE_CELL) {
        cell = DRAW_MARK;
      } else if (cell > COVERED_MINE_CELL) {
        cell = DRAW_WRONG_MARK;
      } else if (cell > MINE_CELL) {
        cell = DRAW_COVER;
      }
    } else {
      if (cell > COVERED_MINE_CELL) {
        cell = DRAW_MARK;
      } else if (cell > MINE_CELL) {
        cell = DRAW_COVER;
      }
    }
    return cell;
  }

  private void updateGameStatus(final int uncover) {
    if (uncover == 0 && this.inGame) {
      this.inGame = false;
      this.statusbar.setText("Game won");
    } else if (!this.inGame) {
      this.statusbar.setText("Game lost");
    }
  }

  private class MinesAdapter extends MouseAdapter {

    @Override
    public void mousePressed(final MouseEvent e) {
      final int x = e.getX();
      final int y = e.getY();
      final int cCol = x / CELL_SIZE;
      final int cRow = y / CELL_SIZE;

      if (!MinefieldBoard.this.inGame) {
        MinefieldBoard.this.newGame();
        MinefieldBoard.this.repaint();
        return;
      }

      if (this.isWithinBoard(x, y)) {
        if (e.getButton() == MouseEvent.BUTTON3) {
          this.handleRightClick(cRow, cCol);
        } else {
          this.handleLeftClick(cRow, cCol);
        }
      }
    }

    private boolean isWithinBoard(final int x, final int y) {
      return x < N_COLS * CELL_SIZE && y < N_ROWS * CELL_SIZE;
    }

    private void handleRightClick(final int cRow, final int cCol) {
      final int cell = MinefieldBoard.this.field[(cRow * N_COLS) + cCol];
      if (cell > MINE_CELL) {
        if (cell <= COVERED_MINE_CELL) {
          this.markCell(cRow, cCol);
        } else {
          this.unmarkCell(cRow, cCol);
        }
        MinefieldBoard.this.repaint();
      }
    }

    private void markCell(final int cRow, final int cCol) {
      if (MinefieldBoard.this.minesLeft > 0) {
        MinefieldBoard.this.field[(cRow * N_COLS) + cCol] += MARK_FOR_CELL;
        MinefieldBoard.this.minesLeft--;
        MinefieldBoard.this.statusbar.setText(Integer.toString(MinefieldBoard.this.minesLeft));
      } else {
        MinefieldBoard.this.statusbar.setText("No marks left");
      }
    }

    private void unmarkCell(final int cRow, final int cCol) {
      MinefieldBoard.this.field[(cRow * N_COLS) + cCol] -= MARK_FOR_CELL;
      MinefieldBoard.this.minesLeft++;
      MinefieldBoard.this.statusbar.setText(Integer.toString(MinefieldBoard.this.minesLeft));
    }

    private void handleLeftClick(final int cRow, final int cCol) {
      final int cell = MinefieldBoard.this.field[(cRow * N_COLS) + cCol];
      if (cell > COVERED_MINE_CELL) {
        return;
      }

      if (cell > MINE_CELL) {
        MinefieldBoard.this.field[(cRow * N_COLS) + cCol] -= COVER_FOR_CELL;
        if (MinefieldBoard.this.field[(cRow * N_COLS) + cCol] == MINE_CELL) {
          MinefieldBoard.this.inGame = false;
        }

        if (MinefieldBoard.this.field[(cRow * N_COLS) + cCol] == EMPTY_CELL) {
          MinefieldBoard.this.find_empty_cells((cRow * N_COLS) + cCol);
        }
        MinefieldBoard.this.repaint();
      }
    }
  }
}
