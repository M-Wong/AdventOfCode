package ch.mikewong.adventofcode.common.models

import ch.mikewong.adventofcode.common.extensions.abs

data class Area(val topLeft: Point, val bottomRight: Point) {
	val xRange = topLeft.x..bottomRight.x
	val yRange = topLeft.y..bottomRight.y

	/**
	 * @return True if the [point] is part of this area
	 */
	operator fun contains(point: Point) = point.x in xRange && point.y in yRange

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
	 * @return The total number of points included in this area
	 */
	fun surfaceArea() = (xRange.abs() + 1) * (yRange.abs() + 1)
}
