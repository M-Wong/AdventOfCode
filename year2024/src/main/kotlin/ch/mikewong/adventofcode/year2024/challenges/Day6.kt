package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toCharGrid
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point
import ch.mikewong.adventofcode.common.models.PointWithDirection

class Day6 : Day<Int, Int>(2024, 6, "Guard Gallivant") {

	private val map by lazy { inputLines.toCharGrid() }
	private val startingPosition by lazy { map.filterValues { it == '^' }.keys.single() }

	private val partOneVisitedPositions = mutableSetOf<Point>()

	// 4819
	override fun partOne(): Int {
		var direction = Direction.NORTH
		var current: Point? = startingPosition

		while (current != null) {
			var next = current.move(direction)
			while (map[next] == '#') {
				direction = direction.turnRight()
				next = current.move(direction)
			}

			partOneVisitedPositions.add(current)
			current = next.takeIf { map[it] != null }
		}

		return partOneVisitedPositions.size
	}

	// 1796
	override fun partTwo(): Int {
		val possiblePositions = partOneVisitedPositions - startingPosition
		return possiblePositions.count { containsLoop(it) }
	}

	private fun containsLoop(obstacle: Point): Boolean {
		val visited = mutableSetOf<PointWithDirection>()
		var current: PointWithDirection? = PointWithDirection(startingPosition, Direction.NORTH)

		while (current != null) {
			var next = PointWithDirection(current.point.move(current.direction), current.direction)
			while (map[next.point] == '#' || next.point == obstacle) {
				val nextDirection = next.direction.turnRight()
				next = PointWithDirection(current.point.move(nextDirection), nextDirection)
			}

			if (visited.contains(next)) {
				return true
			}

			visited.add(current)
			current = next.takeIf { map[it.point] != null }
		}

		return false
	}

}