package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toGridNotNull
import ch.mikewong.adventofcode.common.models.Area
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day23 : Day<Int, Int>(2022, 23, "Unstable Diffusion") {

	private val originalElfPositions by lazy { readInput() }

	override fun partOne(): Int {
		val elfPositions = originalElfPositions.toMutableSet()
		val directionQueue = MovementDirections.values().toMutableList()

		repeat(10) {
			val moves = determineElfMovements(elfPositions, directionQueue)

			moves.forEach { move ->
				elfPositions.remove(move.from)
				elfPositions.add(move.to)
			}

			directionQueue.add(directionQueue.removeFirst())
		}

		val smallestRectangle = Area(
			topLeft = Point(elfPositions.minOf { it.x }, elfPositions.minOf { it.y }),
			bottomRight = Point(elfPositions.maxOf { it.x }, elfPositions.maxOf { it.y })
		)
		return smallestRectangle.surfaceArea() - elfPositions.size
	}

	override fun partTwo(): Int {
		val elfPositions = originalElfPositions.toMutableSet()
		val directionQueue = MovementDirections.values().toMutableList()

		var roundNumber = 0
		var canMove = true

		while (canMove) {
			roundNumber++

			val moves = determineElfMovements(elfPositions, directionQueue)

			moves.forEach { move ->
				elfPositions.remove(move.from)
				elfPositions.add(move.to)
			}

			if (moves.isEmpty()) {
				canMove = false
			}

			directionQueue.add(directionQueue.removeFirst())
		}

		return roundNumber
	}

	private fun determineElfMovements(elfPositions: Set<Point>, directionsToConsider: List<MovementDirections>): List<Move> {
		// To speed up the pruning process of duplicate destinations, moves here is a map of destination to origin and contains the final move set
		val moves = mutableMapOf<Point, Point>()

		// Mark duplicate destinations for quicker checks if a new destination is a valid move
		val duplicateDestinations = mutableSetOf<Point>()

		elfPositions.forEach { elf ->
			// If the elf has no neighbours, he doesn't need to move
			val hasNeighbours = elf.surrounding().any { elfPositions.contains(it) }

			if (hasNeighbours) {
				for (elfConsidersDirection in directionsToConsider) {
					val directions = elfConsidersDirection.adjacent

					// Check if there are no other elves in any of those directions
					val hasNoElvesInDirections = directions.none { elfPositions.contains(elf.move(it)) }
					if (hasNoElvesInDirections) {
						val destination = elf.move(elfConsidersDirection.moveDirection)

						if (duplicateDestinations.contains(destination)) {
							// Destination already marked as duplicate, no need to do anything
						} else if (moves.containsKey(destination)) {
							// Destination is already proposed by another elf, remove from move set and add to duplicate destinations
							moves.remove(destination)
							duplicateDestinations.add(destination)
						} else {
							// So far a valid destination
							moves[destination] = elf
						}

						break
					}
				}
			}
		}

		return moves.map { (to, from) -> Move(from, to) }
	}

	private fun readInput() = inputLines.toGridNotNull { _, c -> if (c == '#') true else null }.keys

	private data class Move(val from: Point, val to: Point)

	private enum class MovementDirections(val moveDirection: Direction, val adjacent: List<Direction>) {
		N(Direction.NORTH, listOf(Direction.NORTH_WEST, Direction.NORTH, Direction.NORTH_EAST)),
		S(Direction.SOUTH, listOf(Direction.SOUTH_WEST, Direction.SOUTH, Direction.SOUTH_EAST)),
		W(Direction.WEST, listOf(Direction.NORTH_WEST, Direction.WEST, Direction.SOUTH_WEST)),
		E(Direction.EAST, listOf(Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST)),
	}

}