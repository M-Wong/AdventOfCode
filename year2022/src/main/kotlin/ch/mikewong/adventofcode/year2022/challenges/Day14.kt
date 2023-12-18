package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day14 : Day<Int, Int>(2022, 14, "Regolith Reservoir") {

	private val originalCaveLayout by lazy { readInput() }
	private val sourceOfSand = Point(500, 0)

	override fun partOne(): Int {
		val caveLayout = originalCaveLayout.associateWith { "#" }.toMutableMap()

		var unitsOfSand = 0

		// While there is a resting point for sand starting at the source, update the cave layout and increment the sand unit count
		var restingPoint: Point? = moveSand(caveLayout.keys, sourceOfSand)
		while (restingPoint != null) {
			unitsOfSand++
			caveLayout[restingPoint] = "o"
			restingPoint = moveSand(caveLayout.keys, sourceOfSand)
		}

		return unitsOfSand
	}

	override fun partTwo(): Int {
		val caveLayout = originalCaveLayout.associateWith { "#" }.toMutableMap()

		val floor = caveLayout.keys.maxOf { it.y } + 2
		var unitsOfSand = 0

		// While the resting point for sand starting at the source is not equal to the starting point itself, update the cave layout and increment the sand unit count
		var restingPoint = requireNotNull(moveSand(caveLayout.keys, sourceOfSand, floor))
		while (restingPoint != sourceOfSand) {
			unitsOfSand++
			caveLayout[restingPoint] = "o"
			restingPoint = requireNotNull(moveSand(caveLayout.keys, sourceOfSand, floor))
		}

		// Plus one because the starting point is not included
		return unitsOfSand + 1
	}

	private fun readInput(): Set<Point> {
		val rockStructures = mutableSetOf<Point>()

		inputLines.forEach { structure ->
			val parts = structure.split(" -> ").map { Point(it.substringBefore(",").toInt(), it.substringAfter(",").toInt()) }

			// For each rock structure, zip its directions with the next one to fill the gaps in between
			parts.zipWithNext().forEach { (start, end) ->
				var current = start
				while (current != end) {
					rockStructures.add(current)

					// Move the current by one in the direction of end
					current = current.move((end.x - current.x).coerceIn(-1, 1), (end.y - current.y).coerceIn(-1, 1))
				}
				rockStructures.add(end)
			}
		}

		return rockStructures
	}

	private fun moveSand(caveLayout: Set<Point>, previous: Point, floor: Long? = null): Point? {
		if (floor == null && caveLayout.none { it.x == previous.x && it.y > previous.y }) {
			return null // No more solid structures below. Only applies if there is no floor
		}

		// Try to move sand one field downward
		var nextPoint = previous.moveDown()

		return if (caveLayout.contains(nextPoint) || nextPoint.y == floor) {
			// If that place is already occupied, try to move it one field down left
			nextPoint = previous.moveDownLeft()

			if (caveLayout.contains(nextPoint) || nextPoint.y == floor) {
				// If that place is also occupied, try to move it one field down right
				nextPoint = previous.moveDownRight()

				if (caveLayout.contains(nextPoint) || nextPoint.y == floor) {
					// If that place is also occupied, the sand has come to a rest here
					previous
				} else {
					// Can move down right, recursively move further
					moveSand(caveLayout, nextPoint, floor)
				}
			} else {
				// Can move down left, recursively move further
				moveSand(caveLayout, nextPoint, floor)
			}
		} else {
			// Can move downward, recursively move further
			moveSand(caveLayout, nextPoint, floor)
		}
	}

	private fun Point.moveDown() = this.move(Direction.EAST) // EAST instead of SOUTH because input has other orientation than the Point class

	private fun Point.moveDownLeft() = this.move(Direction.NORTH_EAST) // NORTH_EAST instead of SOUTH_WEST because input has other orientation than the Point class

	private fun Point.moveDownRight() = this.move(Direction.SOUTH_EAST)

}