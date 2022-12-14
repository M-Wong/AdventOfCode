package ch.mikewong.adventofcode.common.extensions

import ch.mikewong.adventofcode.common.models.Point

/**
 * Prints a map of points to strings in a grid
 */
fun Map<Point, String>.printAsGrid(defaultChar: String = ".") {
	val xRange = this.keys.minOf { it.x } .. this.keys.maxOf { it.x }
	val yRange = this.keys.minOf { it.y } .. this.keys.maxOf { it.y }

	yRange.forEach { y ->
		xRange.forEach { x ->
			val char = this[Point(x, y)] ?: defaultChar
			print(char)
		}
		println()
	}
	println()
}