package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toGridNotNull
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day14 : Day<Long, Long>(2023, 14, "Parabolic Reflector Dish") {

	private val grid = inputLines.toGridNotNull { _, c -> c.takeIf { it != '.' } }
	private val movableRocks = grid.filterValues { it == 'O' }.keys
	private val stationaryRocks = grid.filterValues { it == '#' }.keys

	override fun partOne(): Long {
		return movableRocks.toList()
			.moveRocks(Direction.NORTH)
			.sumOf { inputSize.height - it.x }
	}

	override fun partTwo(): Long {
		val cycles = 1_000_000_000
		var rocks = movableRocks.toList()

		// Store the position of every stone with their cycle index at which this layout first occured
		val rockLayouts = mutableMapOf<List<Point>, Int>()

		// Cycle the rocks until the first time a rock layout is encountered for a second time, that's when a cycle ended
		var currentCycle = 0
		while (!rockLayouts.containsKey(rocks) && currentCycle <= cycles) {
			rockLayouts[rocks] = currentCycle
			rocks = rocks.moveRocks(Direction.NORTH).moveRocks(Direction.WEST).moveRocks(Direction.SOUTH).moveRocks(Direction.EAST)
			currentCycle++
		}

		// Calculate when the cycle started (the first time this rock layout was encountered)
		val cycleStart = rockLayouts.getValue(rocks)

		// ... when the cycle ended (the last iteration)
		val cycleEnd = currentCycle

		// ... the cycle size
		val cycleSize = cycleEnd - cycleStart

		// ... how many cycles can be skipped until the final, non-complete cycle
		val skipCycles = (cycles - cycleStart) / cycleSize

		// ... the start of the final, non-complete cycle
		val startOfLastCycle = cycleStart + (skipCycles * cycleSize)

		// Repeat cycling the rocks until the total number of cycles is reached
		repeat(cycles - startOfLastCycle) {
			rocks = rocks.moveRocks(Direction.NORTH).moveRocks(Direction.WEST).moveRocks(Direction.SOUTH).moveRocks(Direction.EAST)
		}

		return rocks.sumOf { inputSize.height - it.x }
	}

	private fun List<Point>.moveRocks(direction: Direction): List<Point> {
		val rocks = this.toMutableSet()

		// Sort the rocks, so they are moved in the correct order
		val sortedRocks = when (direction) {
			Direction.NORTH -> rocks.sortedBy { it.x }
			Direction.WEST -> rocks.sortedBy { it.y }
			Direction.EAST -> rocks.sortedByDescending { it.y }
			Direction.SOUTH -> rocks.sortedByDescending { it.x }
			else -> throw IllegalArgumentException("Unsupported direction: $direction")
		}

		sortedRocks.forEach { rock ->
			var currentPosition = rock
			var nextPosition = currentPosition.move(direction)

			// For each rock, move them until they either reach the boundary or a stationary rock or another already moved rock
			while (
				nextPosition.x in inputSize.rowRange()
				&& nextPosition.y in inputSize.colRange()
				&& !rocks.contains(nextPosition)
				&& !stationaryRocks.contains(nextPosition)
			) {
				currentPosition = nextPosition
				nextPosition = nextPosition.move(direction)
			}

			// If the rock moved, update the rock list
			if (currentPosition != rock) {
				rocks.remove(rock)
				rocks.add(currentPosition)
			}
		}

		return rocks.toList()
	}

}