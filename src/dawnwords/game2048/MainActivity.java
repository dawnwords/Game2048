package dawnwords.game2048;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import dawnwords.game2048.core.CalculateThread;
import dawnwords.game2048.core.Direction;
import dawnwords.game2048.view.Cell;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity {

    public static final int ACTION_END = 1;
    public static final int SHOW_END = 2;
    public static final int CALCULATE_END = 3;
    public static final int GENERATE_CELL = 4;

    public static final int BOARD_SIZE = 4;

    public static final String DEBUG_TAG = "Game2048";

    private final Handler handler = new Handler() {
        private List<Cell> cellList;
        private List<Cell> updatedList;

        @Override
        public void handleMessage(Message msg) {
            synchronized (this) {
                switch (msg.what) {
                    case ACTION_END:
                        Cell c = (Cell) msg.obj;
                        cellList.remove(c);

                        if (c.shouldMerge()) {
                            boolean exists = false;
                            for (Cell cell : updatedList) {
                                if (cell.x() == c.moveX() && cell.y() == c.moveY()) {
                                    exists = true;
                                    break;
                                }
                            }

                            Log.i(DEBUG_TAG, "Merge " + c);
                            wrapper.removeView(c);
                            if (!exists) {
                                c = new Cell(MainActivity.this, c.moveX(),
                                        c.moveY(), c.value() * 2, this);
                                wrapper.addView(c);
                                c.show();
                                updatedList.add(c);
                            }
                        } else {
                            Log.i(DEBUG_TAG, "Move " + c);
                            c.updatePosition(c.moveX(), c.moveY());
                            updatedList.add(c);
                        }

                        if (cellList.size() == 0) {
                            board = new Cell[BOARD_SIZE][BOARD_SIZE];
                            for (Cell cell : updatedList) {
                                board[cell.x()][cell.y()] = cell;
                            }
                            sendEmptyMessage(GENERATE_CELL);
                        }

                        break;
                    case SHOW_END:
                        break;
                    case CALCULATE_END:
                        cellList = (List<Cell>) msg.obj;
                        updatedList = new LinkedList<Cell>();
                        for (int x = 0; x < BOARD_SIZE; x++) {
                            for (int y = 0; y < BOARD_SIZE; y++) {
                                if (board[x][y] != null) {
                                    board[x][y].performAction();
                                }
                            }
                        }

                        break;
                    case GENERATE_CELL:
                        int availableCount = BOARD_SIZE * BOARD_SIZE - (updatedList == null ? 0 : updatedList.size());
                        int randomIndex = (int) (Math.random() * availableCount - 1);
                        for (int i = 0; i < BOARD_SIZE; i++) {
                            for (int j = 0; j < BOARD_SIZE; j++) {
                                if (board[i][j] == null && randomIndex-- == 0) {
                                    int value = Math.random() < 0.9 ? 2 : 4;
                                    board[i][j] = new Cell(MainActivity.this, i,
                                            j, value, this);
                                    wrapper.addView(board[i][j]);
                                    board[i][j].show();
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private Cell[][] board;
    private RelativeLayout wrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wrapper = (RelativeLayout) findViewById(R.id.wrapper);
        board = new Cell[BOARD_SIZE][BOARD_SIZE];
        handler.sendEmptyMessage(GENERATE_CELL);
    }

    public void right(View v) {
        algorithm(Direction.RIGHT);
    }

    public void left(View v) {
        algorithm(Direction.LEFT);
    }

    public void up(View v) {
        algorithm(Direction.UP);
    }

    public void down(View v) {
        algorithm(Direction.DOWN);
    }

    private void algorithm(Direction direction) {
        new CalculateThread(board, direction, handler).start();
    }
}
