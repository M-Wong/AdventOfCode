package ch.mikewong.adventofcode.common.models

import ch.mikewong.adventofcode.common.extensions.inset
import ch.mikewong.adventofcode.common.extensions.size

data class Area(val topLeft: Point, val bottomRight: Point) {
	val bottomLeft = Point(topLeft.x, bottomRight.y)
	val topRight = Point(bottomRight.x, topLeft.y)

	val xRange: LongRange = topLeft.x..bottomRight.x
	val yRange: LongRange = topLeft.y..bottomRight.y

	val centerX by lazy { (xRange.last - xRange.first) / 2 }
	val centerY by lazy { (yRange.last - yRange.first) / 2 }

	/**
	 * @return True if the [point] is part of this area
	 */
	operator fun contains(point: Point) = point.x in xRange && point.y in yRange

	/**
	 * @return True if the [point] is one of the corner points of this area
	 */
	fun isCornerPoint(point: Point) = point == topLeft || point == topRight || point == bottomLeft || point == bottomRight

	/**
	 * @return True if the [point] is part of this area and lies on the edges of the area
	 */
	fun isOnEdge(point: Point, direction: Direction) = when (direction) {
		Direction.NORTH_WEST -> point == topLeft
		Direction.NORTH -> point.x == xRange.first && point.y in yRange
		Direction.NORTH_EAST -> point.x == xRange.first && point.y == yRange.last
		Direction.WEST -> point.x in xRange && point.y == yRange.first
		Direction.CENTER -> throw IllegalArgumentException("Center direction cannot be on edge")
		Direction.EAST -> point.x in xRange && point.y == yRange.last
		Direction.SOUTH_WEST -> point.x == xRange.last && point.y == yRange.first
		Direction.SOUTH -> point.x == xRange.last && point.y in yRange
		Direction.SOUTH_EAST -> point == bottomRight
	}

	/**
	 * @return True if the [point] is inside this area, excluding the edges
	 */
	fun isInside(point: Point): Boolean {
		return when {
			xRange.size() <= 2L -> false
			yRange.size() <= 2L -> false
			else -> point.x in xRange.inset() && point.y in yRange.inset()
		}
	}

	/**
	 * @return The total number of points included in this area
	 */
	fun surfaceArea() = xRange.size() * yRange.size()

	/**
	 * @return A set of all points within this area. This includes the edges
	 */
	fun allPoints() = buildSet {
		xRange.forEach { x ->
			yRange.forEach { y ->
				add(Point(x, y))
			}
		}
	}
}
