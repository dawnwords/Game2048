package dawnwords.game2048.core;

import android.os.Handler;
import dawnwords.game2048.MainActivity;
import dawnwords.game2048.view.Cell;

/**
 * Created by Dawnwords on 14-3-16.
 */
public class JudgeGameOverThread extends CalculateThread {


    public JudgeGameOverThread(Cell[][] board, Handler handler) {
        super(board, handler);
    }

    @Override
    public void run() {
        for (Cell[] b : board) {
            for (Cell c : b) {
                if (c == null) {
                    return;
                }
                for (Direction direction : Direction.values()) {
                    int x = c.x() + direction.x;
                    int y = c.y() + direction.y;
                    if (checkBounds(x, y) && board[x][y].value() == c.value()) {
                        return;
                    }
                }
            }
        }

        handler.sendEmptyMessage(MainActivity.LOSE);
    }

}
