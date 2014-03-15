package dawnwords.game2048.core;

public enum Direction {
	LEFT(-1, 0), RIGHT(1, 0), UP(0, -1), DOWN(0, 1);

	public int x;
	public int y;

	private Direction(int y, int x) {
		this.x = x;
		this.y = y;
	}

}
