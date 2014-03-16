package dawnwords.game2048.core;

import android.os.Handler;
import dawnwords.game2048.view.Cell;

/**
 * Created by Dawnwords on 14-3-16.
 */
public class CalculateThread extends Thread {
    protected Cell[][] board;
    protected Handler handler;

    public CalculateThread(Cell[][] board, Handler handler) {
        this.board = board;
        this.handler = handler;
    }

    protected boolean checkBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < board.length && y < board.length;
    }
}
