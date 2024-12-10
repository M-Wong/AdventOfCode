package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toGrid
import ch.mikewong.adventofcode.common.models.Point

class Day10 : Day<Int, Int>(2024, 10, "Hoof It") {

	companion object {
		private const val TRAIL_END = 9
	}

	private val map by lazy { inputLines.toGrid { _, c -> c.digitToInt() } }
	private val trailheads by lazy { map.filterValues { it == 0 }.keys }

	// 825
	override fun partOne(): Int {
		return trailheads.sumOf { countTrailEnds(it).size }
	}

	// 1805
	override fun partTwo(): Int {
		return trailheads.sumOf { countUniqueTrails(it) }
	}

	/**
	 * Counts all reachable trail ends from the given [start] point with [startValue].
	 * This only counts reachable trail ends, even if multiple trails lead to that trail end.
	 */
	private fun countTrailEnds(start: Point, startValue: Int = 0): Set<Point> {
		if (startValue == TRAIL_END) return setOf(start)

		val nextValue = startValue + 1
		val adjacent = start.adjacent { map[it] == nextValue }
		return if (adjacent.isEmpty()) {
			emptySet()
		} else {
			adjacent.fold(emptySet()) { acc, point -> acc + countTrailEnds(point, nextValue) }
		}
	}

	/**
	 * Counts all unique trails from the given [start] point with [startValue].
	 * This counts all trails, even those that lead to the same trail end.
	 */
	private fun countUniqueTrails(start: Point, startValue: Int = 0): Int {
		if (startValue == TRAIL_END) return 1

		val nextValue = startValue + 1
		val adjacent = start.adjacent { map[it] == nextValue }
		return if (adjacent.isEmpty()) {
			0
		} else {
			adjacent.sumOf { countUniqueTrails(it, nextValue) }
		}

	}

}