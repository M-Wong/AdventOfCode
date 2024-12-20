package ch.mikewong.adventofcode.common.models

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * x == row / vertical
 * y == column / horizontal
 */
data class Point(val x: Long, val y: Long) {

	constructor(x: Int, y: Int): this(x.toLong(), y.toLong())

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

	fun move(direction: Direction, distance: Int) = move(direction, distance.toLong())

	fun move(direction: Direction, distance: Long) = if (distance > 0) move(direction.deltaX * distance, direction.deltaY * distance) else this

	fun move(deltaX: Int, deltaY: Int) = move(deltaX.toLong(), deltaY.toLong())

	fun move(deltaX: Long, deltaY: Long) = Point(this.x + deltaX, this.y + deltaY)

	fun coerceIn(xRange: LongRange, yRange: LongRange) = Point(this.x.coerceIn(xRange), this.y.coerceIn(yRange))

	fun isAdjacentTo(other: Point) = abs(this.x - other.x) <= 1 && abs(this.y - other.y) <= 1

	fun wrapAround(area: Area, overshoot: Boolean = true) = wrapAround(area.xRange, area.yRange, overshoot)

	/**
	 * Wraps this point around an [xRange] and [yRange]. Setting [overshoot] to true will wrap around and continue on the other side
	 * E.g. Point(5, 5) wrapping around xRange 0..3 and yRange 0..3 will lead to the following output.
	 * - overshoot = true -> Point(1, 1)
	 * - overshoot = false -> Point(0, 0)
	 */
	fun wrapAround(xRange: LongRange, yRange: LongRange, overshoot: Boolean = true) = Point(
		when {
			this.x > xRange.last -> xRange.first + if (overshoot) x % (xRange.last + 1) else 0
			this.x < xRange.first -> xRange.last - if (overshoot) (abs(x + 1) % (xRange.last + 1)) else 0
			else -> this.x
		},
		when {
			this.y > yRange.last -> yRange.first + if (overshoot) y % (yRange.last + 1) else 0
			this.y < yRange.first -> yRange.last - if (overshoot) (abs(y + 1) % (yRange.last + 1)) else 0
			else -> this.y
		},
	)

	fun distanceTo(other: Point): Double {
		return sqrt((x - other.x).toDouble().pow(2) + (y - other.y).toDouble().pow(2))
	}

	fun manhattanDistanceTo(other: Point): Long {
		return abs(other.x - x) + abs(other.y - y)
	}

	fun directionTo(other: Point): Direction {
		return when {
			other == this -> Direction.CENTER
			other.x == this.x && other.y > this.y -> Direction.EAST
			other.x == this.x && other.y < this.y -> Direction.WEST
			other.x > this.x && other.y == this.y -> Direction.SOUTH
			other.x < this.x && other.y == this.y -> Direction.NORTH
			other.x < this.x && other.y < this.y -> Direction.NORTH_WEST
			other.x < this.x && other.y > this.y -> Direction.NORTH_EAST
			other.x > this.x && other.y < this.y -> Direction.SOUTH_WEST
			other.x > this.x && other.y > this.y -> Direction.SOUTH_EAST
			else -> throw IllegalArgumentException("Should never happen")
		}
	}

}
