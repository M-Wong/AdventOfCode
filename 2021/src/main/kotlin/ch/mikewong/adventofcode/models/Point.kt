package ch.mikewong.adventofcode.models

data class Point(val x: Int, val y: Int) {

	/**
	 * Return directly adjacent points (top, right, bottom, left)
	 */
	fun adjacent() = listOf(
		Point(x - 1, y),
		Point(x + 1, y),
		Point(x, y - 1),
		Point(x, y + 1),
	)
}