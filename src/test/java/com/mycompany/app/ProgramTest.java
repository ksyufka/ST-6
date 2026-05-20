package com.mycompany.app;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import javax.swing.JButton;
import org.junit.jupiter.api.Test;

class ProgramTest {

    @Test
    void newGameStartsWithEmptyBoardAndPlayers() {
        Game game = new Game();

        assertEquals(State.PLAYING, game.state);
        assertEquals('X', game.player1.symbol);
        assertEquals('O', game.player2.symbol);
        assertArrayEquals(new char[] {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, game.board);
    }

    @Test
    void checkStateFindsWinsDrawsAndPlayingPositions() {
        Game game = new Game();

        game.symbol = 'X';
        assertEquals(State.XWIN, game.checkState(new char[] {'X', 'X', 'X', ' ', 'O', ' ', 'O', ' ', ' '}));
        assertEquals(State.XWIN, game.checkState(new char[] {'X', 'O', ' ', 'X', 'O', ' ', 'X', ' ', ' '}));
        assertEquals(State.XWIN, game.checkState(new char[] {'X', 'O', ' ', ' ', 'X', 'O', ' ', ' ', 'X'}));

        game.symbol = 'O';
        assertEquals(State.OWIN, game.checkState(new char[] {'X', 'X', 'O', 'X', 'O', ' ', 'O', ' ', ' '}));
        assertEquals(State.DRAW, game.checkState(new char[] {'X', 'O', 'X', 'X', 'O', 'O', 'O', 'X', 'X'}));
        assertEquals(State.PLAYING, game.checkState(new char[] {'X', 'O', 'X', ' ', 'O', ' ', ' ', 'X', ' '}));
    }

    @Test
    void generateMovesReturnsAllEmptyCells() {
        Game game = new Game();
        ArrayList<Integer> moves = new ArrayList<>();

        game.generateMoves(new char[] {'X', ' ', 'O', ' ', 'X', ' ', 'O', 'X', ' '}, moves);

        assertEquals(new ArrayList<Integer>() {{
            add(1);
            add(3);
            add(5);
            add(8);
        }}, moves);
    }

    @Test
    void evaluatePositionScoresWinLossDrawAndOngoingGame() {
        Game game = new Game();

        game.symbol = 'X';
        assertEquals(Game.INF, game.evaluatePosition(new char[] {'X', 'X', 'X', ' ', 'O', ' ', ' ', ' ', 'O'}, game.player1));
        assertEquals(-Game.INF, game.evaluatePosition(new char[] {'X', 'X', 'X', ' ', 'O', ' ', ' ', ' ', 'O'}, game.player2));

        game.symbol = 'O';
        assertEquals(Game.INF, game.evaluatePosition(new char[] {'X', 'X', 'O', 'X', 'O', ' ', 'O', ' ', ' '}, game.player2));
        assertEquals(0, game.evaluatePosition(new char[] {'X', 'O', 'X', 'X', 'O', 'O', 'O', 'X', 'X'}, game.player1));
        assertEquals(-1, game.evaluatePosition(new char[] {'X', 'O', 'X', ' ', 'O', ' ', ' ', 'X', ' '}, game.player1));
    }

    @Test
    void minimaxTakesWinningMoveAndRestoresBoard() {
        Game game = new Game();
        char[] board = {'X', ' ', 'X', 'O', 'O', ' ', ' ', 'X', ' '};
        char[] original = board.clone();

        int move = game.MiniMax(board, game.player2);

        assertEquals(6, move);
        assertArrayEquals(original, board);
        assertEquals(0, game.q);
    }

    @Test
    void minimaxBlocksImmediateOpponentWin() {
        Game game = new Game();
        char[] board = {'X', 'X', ' ', 'O', ' ', ' ', ' ', ' ', 'O'};

        int move = game.MiniMax(board, game.player2);

        assertEquals(3, move);
    }

    @Test
    void minMoveAndMaxMoveReturnTerminalScores() {
        Game game = new Game();
        game.symbol = 'X';

        assertEquals(Game.INF, game.MaxMove(new char[] {'X', 'X', 'X', ' ', 'O', ' ', ' ', ' ', 'O'}, game.player1));
        assertEquals(Game.INF, game.MinMove(new char[] {'X', 'X', 'X', ' ', 'O', ' ', ' ', ' ', 'O'}, game.player1));
        assertEquals(-Game.INF, game.MaxMove(new char[] {'X', 'X', 'X', ' ', 'O', ' ', ' ', ' ', 'O'}, game.player2));
    }

    @Test
    void cellStoresMarkerCoordinatesAndDisablesAfterMove() {
        TicTacToeCell cell = new TicTacToeCell(7, 1, 2);

        assertEquals(7, cell.getNum());
        assertEquals(1, cell.getCol());
        assertEquals(2, cell.getRow());
        assertEquals(' ', cell.getMarker());

        cell.setMarker("X");

        assertEquals('X', cell.getMarker());
        assertEquals("X", cell.getText());
        assertFalse(cell.isEnabled());
    }

    @Test
    void utilityPrintMethodsWriteBoardsAndMoves() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        try {
            Utility.print(new char[] {'X', 'O', ' ', ' ', 'X', ' ', 'O', ' ', 'X'});
            Utility.print(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9});
            ArrayList<Integer> moves = new ArrayList<>();
            moves.add(0);
            moves.add(4);
            moves.add(8);
            Utility.print(moves);
        } finally {
            System.setOut(originalOut);
        }

        String output = out.toString();
        assertTrue(output.contains("X-O- - -X- -O- -X-"));
        assertTrue(output.contains("1-2-3-4-5-6-7-8-9-"));
        assertTrue(output.contains("0-4-8-"));
    }

    @Test
    void panelCreatesNineCellsAndHandlesOpeningMove() {
        TicTacToePanel panel = new TicTacToePanel(new GridLayout(3, 3));

        assertEquals(9, panel.getComponentCount());
        assertTrue(panel.getComponent(0) instanceof TicTacToeCell);

        JButton firstCell = (JButton) panel.getComponent(0);
        panel.actionPerformed(new ActionEvent(firstCell, ActionEvent.ACTION_PERFORMED, "click"));

        int filledCells = 0;
        for (int i = 0; i < panel.getComponentCount(); i++) {
            TicTacToeCell cell = (TicTacToeCell) panel.getComponent(i);
            if (cell.getMarker() != ' ') {
                filledCells++;
            }
        }

        assertEquals('X', ((TicTacToeCell) panel.getComponent(0)).getMarker());
        assertEquals(2, filledCells);
    }
}
