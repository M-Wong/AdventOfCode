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
	SOUTH_EAST(1, 1);

	companion object {
		/** Returns the lateral (non-diagonal) directions */
		fun lateral() = setOf(NORTH, EAST, SOUTH, WEST)

		/** Returns the diagonal directions */
		fun diagonal() = setOf(NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST)
	}

	fun isVertical() = this == NORTH || this == SOUTH

	fun isHorizontal() = this == EAST || this == WEST

	fun opposite() = when (this) {
		NORTH_WEST -> SOUTH_EAST
		NORTH -> SOUTH
		NORTH_EAST -> SOUTH_WEST
		WEST -> EAST
		CENTER -> CENTER
		EAST -> WEST
		SOUTH_WEST -> NORTH_EAST
		SOUTH -> NORTH
		SOUTH_EAST -> NORTH_WEST
	}
}