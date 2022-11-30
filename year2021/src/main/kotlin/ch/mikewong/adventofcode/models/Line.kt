package ch.mikewong.adventofcode.models

data class Line(val start: Point, val end: Point) {
	/**
	 * Check if the line is straight (either horizontal or vertical)
	 */
	fun isStraight() = start.x == end.x || start.y == end.y

	/**
	 * Calculate the slope of the x coordinate. 0 means it doesn't change, 1 meanst it increases and -1 means it decreases
	 */
	fun xSlope() = when {
		start.x > end.x -> -1
		start.x < end.x -> 1
		else -> 0
	}

	/**
	 * Calculate the slope of the y coordinate. 0 means it doesn't change, 1 meanst it increases and -1 means it decreases
	 */
	fun ySlope() = when {
		start.y > end.y -> -1
		start.y < end.y -> 1
		else -> 0
	}
}