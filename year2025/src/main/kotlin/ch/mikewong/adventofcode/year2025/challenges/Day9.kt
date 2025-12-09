package ch.mikewong.adventofcode.year2025.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allPairs
import ch.mikewong.adventofcode.common.models.Area
import ch.mikewong.adventofcode.common.models.Point

class Day9 : Day<Long, Int>(2025, 9, "Movie Theater") {

	private val tiles by lazy {
		inputLines.map { input ->
			val (x, y) = input.split(",").map { it.toInt() }
			Point(x, y)
		}
	}

	/** 4749838800 */
	override fun partOne(): Long {
		return getAreas().maxOf { it.surfaceArea() }
	}

	override fun partTwo(): Int {
		return 0
	}

	private fun getAreas(): List<Area> {
		return tiles.allPairs()
			.map { (a, b) ->
				val topLeft = Point(
					x = minOf(a.x, b.x),
					y = minOf(a.y, b.y),
				)
				val bottomRight = Point(
					x = maxOf(a.x, b.x),
					y = maxOf(a.y, b.y),
				)
				Area(topLeft, bottomRight)
			}
	}

}