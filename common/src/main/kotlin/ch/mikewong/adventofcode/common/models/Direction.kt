package ch.mikewong.adventofcode.common.models

enum class Direction(val deltaX: Int, val deltaY: Int) {
	NORTH_WEST(-1, -1),
	NORTH(-1, 0),
	NORTH_EAST(-1, 1),
	WEST(0, -1),
	CENTER(0, 0),
	EAST(0, 1),
	SOUTH_WEST(1, -1),
	SOUTH(1, 0),
	SOUTH_EAST(1, 1),
}