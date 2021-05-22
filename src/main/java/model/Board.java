package model;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class Board implements Serializable {
    public static final long serialVersionUID = 1L;
    private int[][] board;
    private final int mines;
    private int randomNumber1;
    private int randomNumber2;
    private int turn;
    private boolean ended;
    private int loser;

    public Board() {
        mines = 10;
        board = new int[8][8];
    }

    public void initBoard() {
        turn = 1;
        loser = 0;
        ended = false;
        for (int i = 0; i < board.length - 1; i++) {
            for (int j = 0; j < board[i].length - 1; j++) {
                board[i][j] = 0;
            }
        }

        for (int i = 0; i < mines; i++) {
            if (board[randomNumber1 = ThreadLocalRandom.current().nextInt(0, 7)][randomNumber2 = ThreadLocalRandom.current().nextInt(0, 7)] == 0) {
                board[randomNumber1][randomNumber2] = 3;
            }
        }
    }

    public int getRandomNumber1() {
        return randomNumber1;
    }

    public void setRandomNumber1(int randomNumber1) {
        this.randomNumber1 = randomNumber1;
    }

    public int getRandomNumber2() {
        return randomNumber2;
    }

    public void setRandomNumber2(int randomNumber2) {
        this.randomNumber2 = randomNumber2;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getLoser() {
        return loser;
    }

    public void setLoser(int loser) {
        this.loser = loser;
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < board.length; i++) {

            s = s + (i + 1);
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    s = s + "[ ]";
                } else if (board[i][j] == 3) {
                    s = s + "[ ]";
                }
                if (i == 8){
                    System.out.println(i);
                }
            }
            s = s + "\n";
        }
        return s;
    }

    public void changeTurn() {
        if (turn == 1){
            turn = 2;
        } else{
            turn = 1;
        }
    }
}
