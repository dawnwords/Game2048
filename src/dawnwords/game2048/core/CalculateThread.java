package dawnwords.game2048.core;

import java.util.LinkedList;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import dawnwords.game2048.MainActivity;
import dawnwords.game2048.view.Cell;

public class CalculateThread extends Thread {
	private Cell[][] board;
	private Handler handler;
	private Direction direction;

	public CalculateThread(Cell[][] board, Direction direction, Handler handler) {
		this.board = board;
		this.handler = handler;
		this.direction = direction;
	}

	@Override
	public void run() {
		List<Cell> cellList = new LinkedList<Cell>();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				int x = getTraversX(i);
				int y = getTraversY(j);
				Cell cell = board[x][y];
				if (cell != null) {
					Cell nearest = findNearest(cell);
					int moveX, moveY;
					boolean shouldMerge;

					if (nearest == null) {
						moveX = getMoveX(x);
						moveY = getMoveY(y);
						shouldMerge = false;
					} else if (cell.value() == nearest.value()
							&& !nearest.shouldMerge()) {
						moveX = nearest.moveX();
						moveY = nearest.moveY();
						shouldMerge = true;
						nearest.setActionParams(moveX, moveY, shouldMerge);
					} else {
						moveX = nearest.moveX() - direction.x;
						moveY = nearest.moveY() - direction.y;
						shouldMerge = false;
					}
					cell.setActionParams(moveX, moveY, shouldMerge);
					cellList.add(cell);
				}
			}
		}

		Message message = new Message();
		message.what = MainActivity.CALCULATE_END;
		message.obj = cellList;
		handler.sendMessage(message);
	}

	private int getMoveX(int x) {
		return getMove(x, direction.x);
	}

	private int getMoveY(int y) {
		return getMove(y, direction.y);
	}

	private int getMove(int x, int direction) {
		if (direction == 0) {
			return x;
		}
		if (direction == 1) {
			return board.length - 1;
		}
		return 0;
	}

	private int getTraversX(int i) {
		return getTravers(i, direction.x);
	}

	private int getTraversY(int i) {
		return getTravers(i, direction.y);
	}

	private int getTravers(int i, int direction) {
		if (direction == 1) {
			return board.length - 1 - i;
		}
		return i;
	}

	private Cell findNearest(Cell cell) {
		int i = 1;
		do {
			int x = cell.x() + direction.x * i;
			int y = cell.y() + direction.y * i;
			if (!checkBounds(x, y)) {
				break;
			}
			if (board[x][y] != null) {
				return board[x][y];
			}
			i++;
		} while (true);
		return null;
	}

	private boolean checkBounds(int x, int y) {
		return x >= 0 && y >= 0 && x < board.length && y < board.length;
	}
}
