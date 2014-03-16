package dawnwords.game2048;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import dawnwords.game2048.core.CalculateMoveThread;
import dawnwords.game2048.core.Direction;
import dawnwords.game2048.core.JudgeGameOverThread;
import dawnwords.game2048.view.Cell;
import dawnwords.game2048.view.ResultView;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity {

    public static final int ACTION_END = 1;
    public static final int SHOW_END = 2;
    public static final int CALCULATE_END = 3;
    public static final int GENERATE_CELL = 4;
    public static final int WIN = 5;
    public static final int LOSE = 6;
    public static final int RESTART = 7;
    public static final int BOARD_SIZE = 4;
    public static final int WIN_RESULT = 2048;

    private Cell[][] board;
    private RelativeLayout wrapper;
    private ResultView result;
    private boolean isGameEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GestureDetector detector = new GestureDetector(this, new SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e, MotionEvent e2, float v, float v2) {
                float deltaX = e.getX() - e2.getX();
                float deltaY = e.getY() - e2.getY();
                Direction direction;
                if (deltaX > 0 && deltaX > Math.abs(deltaY)) {
                    direction = Direction.LEFT;
                } else if (deltaX < 0 && -deltaX > Math.abs(deltaY)) {
                    direction = Direction.RIGHT;
                } else if (deltaY > 0 && deltaY > Math.abs(deltaX)) {
                    direction = Direction.UP;
                } else {
                    direction = Direction.DOWN;
                }
                calculateMerge(direction);
                return false;
            }
        });

        result = (ResultView) findViewById(R.id.result);
        wrapper = (RelativeLayout) findViewById(R.id.wrapper);
        wrapper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                if (!isGameEnd) {
                    detector.onTouchEvent(e);
                }
                return true;
            }
        });

        startGame();
    }

    private void startGame() {
        isGameEnd = false;
        wrapper.removeAllViews();
        result.reset();
        board = new Cell[BOARD_SIZE][BOARD_SIZE];
        handler.sendEmptyMessage(RESTART);
    }

    public void restart(View v) {
        startGame();
    }

    private void calculateMerge(Direction direction) {
        new CalculateMoveThread(board, direction, handler).start();
    }

    private final Handler handler = new Handler() {
        private List<Cell> cellList;
        private List<Cell> updatedList;
        private int hasMove;

        @Override
        public void handleMessage(Message msg) {
            synchronized (this) {
                switch (msg.what) {
                    case ACTION_END:
                        actionEnd((Cell) msg.obj);
                        break;
                    case SHOW_END:
                        break;
                    case CALCULATE_END:
                        cellList = (List<Cell>) msg.obj;
                        hasMove = msg.arg1;
                        updatedList = new LinkedList<Cell>();
                        calculateEnd();
                        break;
                    case GENERATE_CELL:
                        generateCell();
                        break;
                    case WIN:
                        isGameEnd = true;
                        result.win();
                        break;
                    case LOSE:
                        isGameEnd = true;
                        result.lose();
                        break;
                    case RESTART:
                        restart();
                        break;
                    default:
                        break;
                }
            }
        }

        private void restart() {
            cellList = null;
            updatedList = null;
            hasMove = 0;
            generateCell();
        }

        private void generateCell() {
            int availableCount = BOARD_SIZE * BOARD_SIZE - (updatedList == null ? 0 : updatedList.size());
            int randomIndex = (int) (Math.random() * availableCount - 1);
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == null && randomIndex-- == 0) {
                        int value = Math.random() < 0.9 ? 2 : 4;
                        board[i][j] = new Cell(MainActivity.this, i, j, value, this);
                        wrapper.addView(board[i][j]);
                        board[i][j].show();
                        if (availableCount == 1) {
                            new JudgeGameOverThread(board, this).start();
                        }
                    }
                }
            }
        }

        private void calculateEnd() {
            for (int x = 0; x < BOARD_SIZE; x++) {
                for (int y = 0; y < BOARD_SIZE; y++) {
                    if (board[x][y] != null) {
                        board[x][y].performAction();
                    }
                }
            }
        }

        private void actionEnd(Cell c) {
            cellList.remove(c);

            if (c.shouldMerge()) {
                boolean exists = false;
                for (Cell cell : updatedList) {
                    if (cell.x() == c.moveX() && cell.y() == c.moveY()) {
                        exists = true;
                        break;
                    }
                }

                wrapper.removeView(c);
                if (!exists) {
                    c = new Cell(MainActivity.this, c.moveX(), c.moveY(), c.value() * 2, this);
                    wrapper.addView(c);
                    c.show();
                    updatedList.add(c);
                }
            } else {
                c.updatePosition(c.moveX(), c.moveY());
                updatedList.add(c);
            }

            if (cellList.size() == 0) {
                board = new Cell[BOARD_SIZE][BOARD_SIZE];
                int maxValue = 0;
                for (Cell cell : updatedList) {
                    board[cell.x()][cell.y()] = cell;
                    if (cell.value() > maxValue) {
                        maxValue = cell.value();
                    }
                }

                if (maxValue >= WIN_RESULT) {
                    sendEmptyMessage(WIN);
                } else if (hasMove > 0) {
                    sendEmptyMessage(GENERATE_CELL);
                }
            }
        }
    };
}

