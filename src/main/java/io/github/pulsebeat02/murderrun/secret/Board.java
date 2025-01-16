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
package io.github.pulsebeat02.murderrun.secret;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("all")
public class Board extends JPanel {

  private final int NUM_IMAGES = 13;
  private final int CELL_SIZE = 15;

  private final int COVER_FOR_CELL = 10;
  private final int MARK_FOR_CELL = 10;
  private final int EMPTY_CELL = 0;
  private final int MINE_CELL = 9;
  private final int COVERED_MINE_CELL = this.MINE_CELL + this.COVER_FOR_CELL;
  private final int MARKED_MINE_CELL = this.COVERED_MINE_CELL + this.MARK_FOR_CELL;

  private final int DRAW_MINE = 9;
  private final int DRAW_COVER = 10;
  private final int DRAW_MARK = 11;
  private final int DRAW_WRONG_MARK = 12;

  private final int N_MINES = 40;
  private final int N_ROWS = 16;
  private final int N_COLS = 16;

  private final int BOARD_WIDTH = this.N_COLS * this.CELL_SIZE + 1;
  private final int BOARD_HEIGHT = this.N_ROWS * this.CELL_SIZE + 1;

  private int[] field;
  private boolean inGame;
  private int minesLeft;
  private Image[] img;

  private int allCells;
  private final JLabel statusbar;

  public Board(final JLabel statusbar) {
    this.statusbar = statusbar;
    this.initBoard();
  }

  private void initBoard() {
    this.setPreferredSize(new Dimension(this.BOARD_WIDTH, this.BOARD_HEIGHT));

    this.img = new Image[this.NUM_IMAGES];

    for (int i = 0; i < this.NUM_IMAGES; i++) {
      final var path = "/secret/" + i + ".png";
      this.img[i] = (new ImageIcon(Board.class.getResource(path))).getImage();
    }

    this.addMouseListener(new MinesAdapter());
    this.newGame();
  }

  private void newGame() {
    int cell;

    final var random = new Random();
    this.inGame = true;
    this.minesLeft = this.N_MINES;

    this.allCells = this.N_ROWS * this.N_COLS;
    this.field = new int[this.allCells];

    for (int i = 0; i < this.allCells; i++) {
      this.field[i] = this.COVER_FOR_CELL;
    }

    this.statusbar.setText(Integer.toString(this.minesLeft));

    int i = 0;

    while (i < this.N_MINES) {
      final int position = (int) (this.allCells * random.nextDouble());

      if ((position < this.allCells) && (this.field[position] != this.COVERED_MINE_CELL)) {
        final int current_col = position % this.N_COLS;
        this.field[position] = this.COVERED_MINE_CELL;
        i++;

        if (current_col > 0) {
          cell = position - 1 - this.N_COLS;
          if (cell >= 0) {
            if (this.field[cell] != this.COVERED_MINE_CELL) {
              this.field[cell] += 1;
            }
          }
          cell = position - 1;
          if (cell >= 0) {
            if (this.field[cell] != this.COVERED_MINE_CELL) {
              this.field[cell] += 1;
            }
          }

          cell = position + this.N_COLS - 1;
          if (cell < this.allCells) {
            if (this.field[cell] != this.COVERED_MINE_CELL) {
              this.field[cell] += 1;
            }
          }
        }

        cell = position - this.N_COLS;
        if (cell >= 0) {
          if (this.field[cell] != this.COVERED_MINE_CELL) {
            this.field[cell] += 1;
          }
        }

        cell = position + this.N_COLS;
        if (cell < this.allCells) {
          if (this.field[cell] != this.COVERED_MINE_CELL) {
            this.field[cell] += 1;
          }
        }

        if (current_col < (this.N_COLS - 1)) {
          cell = position - this.N_COLS + 1;
          if (cell >= 0) {
            if (this.field[cell] != this.COVERED_MINE_CELL) {
              this.field[cell] += 1;
            }
          }
          cell = position + this.N_COLS + 1;
          if (cell < this.allCells) {
            if (this.field[cell] != this.COVERED_MINE_CELL) {
              this.field[cell] += 1;
            }
          }
          cell = position + 1;
          if (cell < this.allCells) {
            if (this.field[cell] != this.COVERED_MINE_CELL) {
              this.field[cell] += 1;
            }
          }
        }
      }
    }
  }

  private void find_empty_cells(final int j) {
    final int current_col = j % this.N_COLS;
    int cell;

    if (current_col > 0) {
      cell = j - this.N_COLS - 1;
      if (cell >= 0) {
        if (this.field[cell] > this.MINE_CELL) {
          this.field[cell] -= this.COVER_FOR_CELL;
          if (this.field[cell] == this.EMPTY_CELL) {
            this.find_empty_cells(cell);
          }
        }
      }

      cell = j - 1;
      if (cell >= 0) {
        if (this.field[cell] > this.MINE_CELL) {
          this.field[cell] -= this.COVER_FOR_CELL;
          if (this.field[cell] == this.EMPTY_CELL) {
            this.find_empty_cells(cell);
          }
        }
      }

      cell = j + this.N_COLS - 1;
      if (cell < this.allCells) {
        if (this.field[cell] > this.MINE_CELL) {
          this.field[cell] -= this.COVER_FOR_CELL;
          if (this.field[cell] == this.EMPTY_CELL) {
            this.find_empty_cells(cell);
          }
        }
      }
    }

    cell = j - this.N_COLS;
    if (cell >= 0) {
      if (this.field[cell] > this.MINE_CELL) {
        this.field[cell] -= this.COVER_FOR_CELL;
        if (this.field[cell] == this.EMPTY_CELL) {
          this.find_empty_cells(cell);
        }
      }
    }

    cell = j + this.N_COLS;
    if (cell < this.allCells) {
      if (this.field[cell] > this.MINE_CELL) {
        this.field[cell] -= this.COVER_FOR_CELL;
        if (this.field[cell] == this.EMPTY_CELL) {
          this.find_empty_cells(cell);
        }
      }
    }

    if (current_col < (this.N_COLS - 1)) {
      cell = j - this.N_COLS + 1;
      if (cell >= 0) {
        if (this.field[cell] > this.MINE_CELL) {
          this.field[cell] -= this.COVER_FOR_CELL;
          if (this.field[cell] == this.EMPTY_CELL) {
            this.find_empty_cells(cell);
          }
        }
      }

      cell = j + this.N_COLS + 1;
      if (cell < this.allCells) {
        if (this.field[cell] > this.MINE_CELL) {
          this.field[cell] -= this.COVER_FOR_CELL;
          if (this.field[cell] == this.EMPTY_CELL) {
            this.find_empty_cells(cell);
          }
        }
      }

      cell = j + 1;
      if (cell < this.allCells) {
        if (this.field[cell] > this.MINE_CELL) {
          this.field[cell] -= this.COVER_FOR_CELL;
          if (this.field[cell] == this.EMPTY_CELL) {
            this.find_empty_cells(cell);
          }
        }
      }
    }
  }

  @Override
  public void paintComponent(final Graphics g) {
    int uncover = 0;

    for (int i = 0; i < this.N_ROWS; i++) {
      for (int j = 0; j < this.N_COLS; j++) {
        int cell = this.field[(i * this.N_COLS) + j];

        if (this.inGame && cell == this.MINE_CELL) {
          this.inGame = false;
        }

        if (!this.inGame) {
          if (cell == this.COVERED_MINE_CELL) {
            cell = this.DRAW_MINE;
          } else if (cell == this.MARKED_MINE_CELL) {
            cell = this.DRAW_MARK;
          } else if (cell > this.COVERED_MINE_CELL) {
            cell = this.DRAW_WRONG_MARK;
          } else if (cell > this.MINE_CELL) {
            cell = this.DRAW_COVER;
          }
        } else {
          if (cell > this.COVERED_MINE_CELL) {
            cell = this.DRAW_MARK;
          } else if (cell > this.MINE_CELL) {
            cell = this.DRAW_COVER;
            uncover++;
          }
        }

        g.drawImage(this.img[cell], (j * this.CELL_SIZE), (i * this.CELL_SIZE), this);
      }
    }

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

      final int cCol = x / Board.this.CELL_SIZE;
      final int cRow = y / Board.this.CELL_SIZE;

      boolean doRepaint = false;

      if (!Board.this.inGame) {
        Board.this.newGame();
        Board.this.repaint();
      }

      if ((x < Board.this.N_COLS * Board.this.CELL_SIZE) && (y < Board.this.N_ROWS * Board.this.CELL_SIZE)) {
        if (e.getButton() == MouseEvent.BUTTON3) {
          if (Board.this.field[(cRow * Board.this.N_COLS) + cCol] > Board.this.MINE_CELL) {
            doRepaint = true;

            if (Board.this.field[(cRow * Board.this.N_COLS) + cCol] <= Board.this.COVERED_MINE_CELL) {
              if (Board.this.minesLeft > 0) {
                Board.this.field[(cRow * Board.this.N_COLS) + cCol] += Board.this.MARK_FOR_CELL;
                Board.this.minesLeft--;
                final String msg = Integer.toString(Board.this.minesLeft);
                Board.this.statusbar.setText(msg);
              } else {
                Board.this.statusbar.setText("No marks left");
              }
            } else {
              Board.this.field[(cRow * Board.this.N_COLS) + cCol] -= Board.this.MARK_FOR_CELL;
              Board.this.minesLeft++;
              final String msg = Integer.toString(Board.this.minesLeft);
              Board.this.statusbar.setText(msg);
            }
          }
        } else {
          if (Board.this.field[(cRow * Board.this.N_COLS) + cCol] > Board.this.COVERED_MINE_CELL) {
            return;
          }

          if (
            (Board.this.field[(cRow * Board.this.N_COLS) + cCol] > Board.this.MINE_CELL) &&
            (Board.this.field[(cRow * Board.this.N_COLS) + cCol] < Board.this.MARKED_MINE_CELL)
          ) {
            Board.this.field[(cRow * Board.this.N_COLS) + cCol] -= Board.this.COVER_FOR_CELL;
            doRepaint = true;

            if (Board.this.field[(cRow * Board.this.N_COLS) + cCol] == Board.this.MINE_CELL) {
              Board.this.inGame = false;
            }

            if (Board.this.field[(cRow * Board.this.N_COLS) + cCol] == Board.this.EMPTY_CELL) {
              Board.this.find_empty_cells((cRow * Board.this.N_COLS) + cCol);
            }
          }
        }

        if (doRepaint) {
          Board.this.repaint();
        }
      }
    }
  }
}
