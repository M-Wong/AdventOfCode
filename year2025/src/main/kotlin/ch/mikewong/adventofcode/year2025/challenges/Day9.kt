package ch.mikewong.adventofcode.year2025.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allPairs
import ch.mikewong.adventofcode.common.models.Area
import ch.mikewong.adventofcode.common.models.MultiRectangle
import ch.mikewong.adventofcode.common.models.Point

class Day9 : Day<Long, Long>(2025, 9, "Movie Theater") {

	private val tiles by lazy {
		inputLines.map { input ->
			val (y, x) = input.split(",").map { it.toInt() }
			Point(x, y)
		}
	}

	/** 4749838800 */
	override fun partOne(): Long {
		return getAreas().maxOf { it.surfaceArea() }
	}

	/** 1624057680 */
	override fun partTwo(): Long {
		// TODO Takes about 6s to run, could be optimized maybe with coordinate compression?
		val areas = getAreas()
		val polygon = MultiRectangle(tiles)
		val edgePoints = polygon.getEdgePoints()

		val validAreas = areas
			.filter { area ->
				// If any of the tiles is inside the area, it's not valid
				tiles.none { area.isInside(it) }
			}
			.filter { area ->
				// If any of the edge points of the polygon is inside the area, it's not valid
				edgePoints.none { area.isInside(it) }
			}

		return validAreas.maxOf { it.surfaceArea() }
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