package ch.mikewong.adventofcode.common.extensions

import ch.mikewong.adventofcode.common.models.Point

fun Map<Point, Char>.printAsCharGrid(
	vertical: Boolean = true,
	defaultChar: String = ".",
) = this.mapValues { (_, c) -> c.toString() }.printAsGrid(vertical, defaultChar)

/**
 * Prints a map of points to strings in a grid
 * @param vertical True to print x as lines
 */
fun Map<Point, String>.printAsGrid(vertical: Boolean = true, defaultChar: String = ".") {
	val xRange = this.keys.minOf { it.x }..this.keys.maxOf { it.x }
	val yRange = this.keys.minOf { it.y }..this.keys.maxOf { it.y }

	if (vertical) {
		xRange.forEach { x ->
			yRange.forEach { y ->
				val char = this[Point(x, y)] ?: defaultChar
				print(char)
			}
			println()
		}
	} else {
		yRange.forEach { y ->
			xRange.forEach { x ->
				val char = this[Point(x, y)] ?: defaultChar
				print(char)
			}
			println()
		}
	}
	println()
}