package ch.mikewong.adventofcode.common.models

import kotlin.math.absoluteValue
import kotlin.math.roundToLong

/**
 * A class representing a multi-rectangle aka a polygon with only 90° angles.
 * The [vertices] are the corners of the multi-rectangle and must always differ in only one dimension compared to the previous and next vertex.
 */
data class MultiRectangle(val vertices: List<Point>) {

	private val closedLoop by lazy {
		// Ensure that the vertices form a closed loop, so that the last vertex connects to the first one
		if (vertices.first() == vertices.last()) vertices else vertices.plus(vertices.first())
	}

	private val edgeLengths by lazy {
		closedLoop.zipWithNext().map { (a, b) -> a.manhattanDistanceTo(b) }
	}

	/**
	 * Returns the total edge length of this multi-rectangle
	 */
	fun getEdgeLength() = edgeLengths.sum()

	/**
	 * Returns the number of points contained within this multi-rectangle.
	 * @param includeEdge True to include the points that build the edge or false to exclude them
	 */
	fun getPointsWithin(includeEdge: Boolean): Long {
		// Use the shoelace formula to calculate the area of the polygon defined by the border points
		val shoelaceArea = shoelaceArea().roundToLong()

		// The shoelace area takes points as infinitely small and therefore half of each point is outside and needs to be accounted for
		val halfBorderLength = getEdgeLength() / 2

		// Since the shoelace area goes through the center of a point, for an outside corner this will effectively be 0.25 and for an inside corner 0.75
		// For a closed polygon, there will always be 4 outside corners more than inside corners, so we need to add another 1 point to the area
		val offset = 1

		return if (includeEdge) {
			shoelaceArea + halfBorderLength + offset
		} else {
			shoelaceArea - halfBorderLength + offset
		}
	}

	fun getEdgePoints(): Set<Point> {
		val edgePoints = mutableSetOf<Point>()

		closedLoop.zipWithNext().forEach { (a, b) ->
			when {
				a.x > b.x -> {
					(b.x..a.x).forEach { x ->
						edgePoints.add(Point(x, a.y))
					}
				}
				a.x < b.x -> {
					(a.x..b.x).forEach { x ->
						edgePoints.add(Point(x, a.y))
					}
				}
				a.y > b.y -> {
					(b.y..a.y).forEach { y ->
						edgePoints.add(Point(a.x, y))
					}
				}
				a.y < b.y -> {
					(a.y..b.y).forEach { y ->
						edgePoints.add(Point(a.x, y))
					}
				}
				else -> throw IllegalStateException("Points $a and $b are the same")
			}
		}

		return edgePoints
	}

	/**
	 * Calculate the area of this polygon using the Shoelace formula (see https://en.wikipedia.org/wiki/Shoelace_formula)
	 */
	private fun shoelaceArea(): Double {
		return (closedLoop.zipWithNext().sumOf { (a, b) -> (a.x * b.y) - (a.y * b.x) } / 2.0).absoluteValue
	}

}
