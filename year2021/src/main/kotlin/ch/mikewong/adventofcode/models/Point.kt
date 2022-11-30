package ch.mikewong.adventofcode.models

/**
 * x == row
 * y == column
 */
data class Point(val x: Int, val y: Int) {

	/**
	 * Return directly adjacent points (top, right, bottom, left)
	 */
	fun adjacent(filter: (Point) -> Boolean = { true }) = listOf(
		move(Direction.NORTH),
		move(Direction.EAST),
		move(Direction.SOUTH),
		move(Direction.WEST),
	).filter(filter)

	/**
	 * Return directly surrounding points
	 */
	fun surrounding(includeItself: Boolean = false, filter: (Point) -> Boolean = { true }) = listOfNotNull(
		move(Direction.NORTH_WEST),
		move(Direction.NORTH),
		move(Direction.NORTH_EAST),
		move(Direction.WEST),
		if (includeItself) move(Direction.CENTER) else null,
		move(Direction.EAST),
		move(Direction.SOUTH_WEST),
		move(Direction.SOUTH),
		move(Direction.SOUTH_EAST),
	).filter(filter)

	fun plus(xIncrement: Int, yIncrement: Int) = Point(this.x + xIncrement, this.y + yIncrement)

	fun plus(other: Point) = Point(this.x + other.x, this.y + other.y)

	fun isWithin(topLeft: Point, bottomRight: Point) = this.x in topLeft.x..bottomRight.x && this.y in bottomRight.y..topLeft.y

	fun move(direction: Direction) = Point(this.x + direction.deltaX, this.y + direction.deltaY)

	fun coerceIn(xRange: IntRange, yRange: IntRange) = Point(this.x.coerceIn(xRange), this.y.coerceIn(yRange))

	fun wrapAround(xRange: IntRange, yRange: IntRange) = Point(
		when {
			this.x > xRange.last -> xRange.first
			this.x < xRange.first -> xRange.last
			else -> this.x
		},
		when {
			this.y > yRange.last -> yRange.first
			this.y < yRange.first -> yRange.last
			else -> this.y
		},
	)
}

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