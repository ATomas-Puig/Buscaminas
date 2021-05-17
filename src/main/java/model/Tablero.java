package model;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Tablero {
    private int[][] tablero;
    private int minas;
    int randomNumber1;
    int randomNumber2;

    public Tablero() {
        minas = 10;
        tablero = new int[8][8];
    }

    public void initTablero() {
        for (int i = 0; i < tablero.length - 1; i++) {
            for (int j = 0; j < tablero[i].length - 1; j++) {
                tablero[i][j] = 0;
            }
        }

        for (int i = 0; i < minas; i++) {
            if (tablero[randomNumber1 = ThreadLocalRandom.current().nextInt(0, 7)][randomNumber2 = ThreadLocalRandom.current().nextInt(0, 7)] == 0){
                tablero[randomNumber1][randomNumber2] = 3;
            }
        }
    }

    @Override
    public String toString() {
        String s = "";
        for (int i =0; i< tablero.length; i++){
            for (int j = 0; j < tablero[i].length; j++){
                s = s + "[" + tablero[i][j] + "]";
                //System.out.print("[" + tablero[i][j] + "]");
            }
            s = s + "\n";
            //System.out.println();
        }
        return s;
    }
}
