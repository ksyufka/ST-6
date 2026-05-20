package com.mycompany.app;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

enum State { PLAYING, OWIN, XWIN, DRAW }

class Player {
  public char symbol;
  public int move;
  public boolean selected;
  public boolean win;
}

class Game {
    public State state;
    public Player player1, player2;
    public Player cplayer;
    public int nmove;
    public char symbol;
    public static final int INF = 100;
    public int q;
    public char[] board;

    public Game() {
      player1 = new Player();
      player2 = new Player();
      player1.symbol = 'X';
      player2.symbol = 'O';
      state = State.PLAYING;
      board = new char[9];
      for (int i = 0; i < 9; i++) {
        board[i] = ' ';
      }
    }

    public State checkState(char[] board) {
      State result = State.PLAYING;
      if ((board[0] == symbol && board[1] == symbol && board[2] == symbol) ||
          (board[3] == symbol && board[4] == symbol && board[5] == symbol) ||
          (board[6] == symbol && board[7] == symbol && board[8] == symbol) ||
          (board[0] == symbol && board[3] == symbol && board[6] == symbol) ||
          (board[1] == symbol && board[4] == symbol && board[7] == symbol) ||
          (board[2] == symbol && board[5] == symbol && board[8] == symbol) ||
          (board[0] == symbol && board[4] == symbol && board[8] == symbol) ||
          (board[2] == symbol && board[4] == symbol && board[6] == symbol)) {
        if (symbol == 'X') {
            result = State.XWIN;
        } else if (symbol == 'O') {
            result = State.OWIN;
        }
      } else {
        result = State.DRAW;
        for (int i = 0; i < 9; i++) {
            if (board[i] == ' ') {
                result = State.PLAYING;
                break;
            }
        }
      }
      return result;
  }

   void generateMoves(char[] board, ArrayList<Integer> moveList) {
    for (int i = 0; i < 9; i++) {
        if (board[i] == ' ') {
            moveList.add(i);
        }
    }
   }

   int evaluatePosition(char[] board, Player player) {
    State state = checkState(board);
    if (state == State.XWIN || state == State.OWIN || state == State.DRAW) {
        if ((state == State.XWIN && player.symbol == 'X') || (state == State.OWIN && player.symbol == 'O')) {
            return +Game.INF;
        } else if ((state == State.XWIN && player.symbol == 'O') || (state == State.OWIN && player.symbol == 'X')) {
            return -Game.INF;
        } else if (state == State.DRAW) {
            return 0;
        }
    }
    return -1;
   }

   int MiniMax(char[] board, Player player) {
    int bestVal = -Game.INF, index = 0;
    ArrayList<Integer> moveList = new ArrayList<>();
    int[] bestMoves = new int[9];

    generateMoves(board, moveList);

    while (moveList.size() != 0) {
        board[moveList.get(0)] = player.symbol;
        symbol = player.symbol;

        int val = MinMove(board, player);

        if (val > bestVal) {
            bestVal = val;
            index = 0;
            bestMoves[index] = moveList.get(0) + 1;
        } else if (val == bestVal) {
            bestMoves[++index] = moveList.get(0) + 1;
        }

        System.out.printf("\nminimax: %3d(%1d) ", 1 + moveList.get(0), val);
        board[moveList.get(0)] = ' ';
        moveList.remove(0);
    }
    if (index > 0) {
      Random r = new Random();
      index = r.nextInt(index + 1);
    }

    System.out.printf("\nminimax best: %3d(%1d) ", bestMoves[index], bestVal);
    System.out.printf("Steps counted: %d", q);
    q = 0;
    return bestMoves[index];
  }

  int MinMove(char[] board, Player player) {
    int posValue = evaluatePosition(board, player);
    if (posValue != -1) {
      return posValue;
    }
    q++;
    int bestVal = +Game.INF;
    ArrayList<Integer> moveList = new ArrayList<>();

    generateMoves(board, moveList);

    while (moveList.size() != 0) {
        symbol = (player.symbol == 'X') ? 'O' : 'X';
        board[moveList.get(0)] = symbol;

        int val = MaxMove(board, player);

        if (val < bestVal) {
            bestVal = val;
        }
        board[moveList.get(0)] = ' ';
        moveList.remove(0);
    }
    return bestVal;
  }

  int MaxMove(char[] board, Player player) {
    int posValue = evaluatePosition(board, player);
    if (posValue != -1) {
      return posValue;
    }
    q++;
    int bestVal = -Game.INF;
    ArrayList<Integer> moveList = new ArrayList<>();
    generateMoves(board, moveList);
    while (moveList.size() != 0) {
        symbol = (player.symbol == 'X') ? 'X' : 'O';
        board[moveList.get(0)] = symbol;
        int val = MinMove(board, player);
        if (val > bestVal) {
            bestVal = val;
        }
        board[moveList.get(0)] = ' ';
        moveList.remove(0);
    }
    return bestVal;
  }
}

public class Program {
    public static void main(String[] args) throws IOException {
       JFrame frame = new JFrame("Demo");
       frame.add(new TicTacToePanel(new GridLayout(3, 3)));
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setBounds(5, 5, 500, 500);
       frame.setVisible(true);
    }
}

class TicTacToeCell extends JButton {
    private final int num;
    private final int row;
    private final int col;
    private char marker;

    public TicTacToeCell(int num, int x, int y) {
        this.num = num;
        row = y;
        col = x;
        marker = ' ';
        setText(Character.toString(marker));
        setFont(new Font("Arial", Font.PLAIN, 40));
    }

    public void setMarker(String m) {
        marker = m.charAt(0);
        setText(m);
        setEnabled(false);
    }

    public char getMarker() {
        return marker;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getNum() {
        return num;
    }
}

class Utility {
  public static void print(char[] board) {
    System.out.println();
    for (int j = 0; j < 9; j++) {
      System.out.print(board[j] + "-");
    }
    System.out.println();
  }

  public static void print(int[] board) {
    System.out.println();
    for (int j = 0; j < 9; j++) {
      System.out.print(board[j] + "-");
    }
    System.out.println();
  }

  public static void print(ArrayList<Integer> moves) {
    System.out.println();
    for (int j = 0; j < moves.size(); j++) {
      System.out.print(moves.get(j) + "-");
    }
    System.out.println();
  }
}

class TicTacToePanel extends JPanel implements ActionListener {
   private Game game;
   private TicTacToeCell[] cells = new TicTacToeCell[9];

   TicTacToePanel(GridLayout layout) {
       super(layout);
       createCell(0, 0, 0);
       createCell(1, 1, 0);
       createCell(2, 2, 0);
       createCell(3, 0, 1);
       createCell(4, 1, 1);
       createCell(5, 2, 1);
       createCell(6, 0, 2);
       createCell(7, 1, 2);
       createCell(8, 2, 2);
       game = new Game();
       game.cplayer = game.player1;
   }

   private void createCell(int num, int x, int y) {
       cells[num] = new TicTacToeCell(num, x, y);
       cells[num].addActionListener(this);
       add(cells[num]);
   }

   public void actionPerformed(ActionEvent ae) {
      game.player1.move = -1;
      game.player2.move = -1;

      int i = 0;
      for (TicTacToeCell jb: cells) {
         if (ae.getSource() == jb) {
            jb.setMarker(Character.toString(game.cplayer.symbol));
         }
         game.board[i++] = jb.getMarker();
      }
      if (game.cplayer == game.player1) {
         game.player2.move = game.MiniMax(game.board, game.player2);
         game.nmove = game.player2.move;
         game.symbol = game.player2.symbol;
         game.cplayer = game.player2;
         if (game.player2.move > 0) {
            cells[game.player2.move - 1].doClick();
         }
       } else {
         game.nmove = game.player1.move;
         game.symbol = game.player1.symbol;
         game.cplayer = game.player1;
       }

      game.state = game.checkState(game.board);

      if (game.state == State.XWIN) {
        JOptionPane.showMessageDialog(null, "Выиграли крестики", "Результат", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
      } else if (game.state == State.OWIN) {
        JOptionPane.showMessageDialog(null, "Выиграли нолики", "Результат", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
      } else if (game.state == State.DRAW) {
        JOptionPane.showMessageDialog(null, "Ничья", "Результат", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
      }
   }
}
