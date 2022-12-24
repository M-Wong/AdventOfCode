package ch.mikewong.adventofcode.common.models

import ch.mikewong.adventofcode.common.extensions.pow
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * x == row
 * y == column
 */
data class Point(val x: Int, val y: Int) {

	override fun toString(): String {
		return "($x,$y)"
	}

	/**
	 * Return directly adjacent points (top, right, bottom, left)
	 */
	fun adjacent(filter: (Point) -> Boolean = { true }) = listOf(
		move(Direction.NORTH),
		move(Direction.EAST),
		move(Direction.SOUTH),
		move(Direction.WEST),
	).filter(filter)

	/**
	 * Return directly surrounding points
	 * @param includeItself True to include the center position
	 * @param filter An optional filter, removing any direction for which the predicate does not apply
	 */
	fun surrounding(includeItself: Boolean = false, filter: (Point) -> Boolean = { true }) = listOfNotNull(
		move(Direction.NORTH_WEST),
		move(Direction.NORTH),
		move(Direction.NORTH_EAST),
		move(Direction.WEST),
		if (includeItself) move(Direction.CENTER) else null,
		move(Direction.EAST),
		move(Direction.SOUTH_WEST),
		move(Direction.SOUTH),
		move(Direction.SOUTH_EAST),
	).filter(filter)

	fun plus(xIncrement: Int, yIncrement: Int) = Point(this.x + xIncrement, this.y + yIncrement)

	fun plus(other: Point) = Point(this.x + other.x, this.y + other.y)

	fun isWithin(topLeft: Point, bottomRight: Point) = this.x in topLeft.x..bottomRight.x && this.y in bottomRight.y..topLeft.y

	fun move(direction: Direction) = move(direction.deltaX, direction.deltaY)

	fun move(deltaX: Int, deltaY: Int) = Point(this.x + deltaX, this.y + deltaY)

	fun coerceIn(xRange: IntRange, yRange: IntRange) = Point(this.x.coerceIn(xRange), this.y.coerceIn(yRange))

	fun isAdjacentTo(other: Point) = abs(this.x - other.x) <= 1 && abs(this.y - other.y) <= 1

	fun wrapAround(area: Area) = wrapAround(area.xRange, area.yRange)

	fun wrapAround(xRange: IntRange, yRange: IntRange) = Point(
		when {
			this.x > xRange.last -> xRange.first
			this.x < xRange.first -> xRange.last
			else -> this.x
		},
		when {
			this.y > yRange.last -> yRange.first
			this.y < yRange.first -> yRange.last
			else -> this.y
		},
	)

	fun distanceTo(other: Point): Double {
		return sqrt((x - other.x).pow(2) + (y - other.y).pow(2))
	}

	fun manhattanDistanceTo(other: Point): Int {
		return abs(other.x - x) + abs(other.y - y)
	}

}
