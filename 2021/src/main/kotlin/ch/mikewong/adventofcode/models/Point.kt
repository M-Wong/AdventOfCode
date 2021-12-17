package ch.mikewong.adventofcode.models

data class Point(val x: Int, val y: Int) {

	/**
	 * Return directly adjacent points (top, right, bottom, left)
	 */
	fun adjacent(filter: (Point) -> Boolean = { true }) = listOf(
		Point(x - 1, y),
		Point(x + 1, y),
		Point(x, y - 1),
		Point(x, y + 1),
	).filter(filter)

	/**
	 * Return directly surrounding points
	 */
	fun surrounding(filter: (Point) -> Boolean = { true }) = listOf(
		Point(x - 1, y - 1),
		Point(x, y - 1),
		Point(x + 1, y - 1),
		Point(x - 1, y),
		Point(x + 1, y),
		Point(x - 1, y + 1),
		Point(x, y + 1),
		Point(x + 1, y + 1),
	).filter(filter)

	fun plus(xIncrement: Int, yIncrement: Int) = Point(this.x + xIncrement, this.y + yIncrement)

	fun plus(other: Point) = Point(this.x + other.x, this.y + other.y)

	fun isWithin(topLeft: Point, bottomRight: Point) = this.x in topLeft.x..bottomRight.x && this.y in bottomRight.y..topLeft.y
}