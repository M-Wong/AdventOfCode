package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toGridNotNull
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day16 : Day<Int, Int>(2023, 16, "The Floor Will Be Lava") {

	private val bounds = inputSize.toArea()
	private val contraptions = inputLines.toGridNotNull { _, c ->
		when (c) {
			'|' -> Contraption.Splitter(isHorizontal = false)
			'-' -> Contraption.Splitter(isHorizontal = true)
			'/' -> Contraption.Mirror(isSlash = true)
			'\\' -> Contraption.Mirror(isSlash = false)
			else -> null
		}
	}

	override fun partOne(): Int {
		return energize(Point(0, 0), Direction.EAST).size
	}

	override fun partTwo(): Int {
		// Get a list of all starting points and their direction
		val startingPositions = buildList {
			bounds.xRange.forEach { x ->
				add(Point(x, 0) to Direction.EAST)
				add(Point(x, bounds.yRange.last) to Direction.WEST)
			}
			bounds.yRange.forEach { y ->
				add(Point(0, y) to Direction.SOUTH)
				add(Point(bounds.xRange.last, y) to Direction.NORTH)
			}
		}

		return startingPositions.maxOf { (initialPosition, initialDirection) ->
			// Find the starting position with the maximum number of energized tiles (clearing the cache in between each starting position)
			visited.clear()
			energize(initialPosition, initialDirection).size
		}
	}

	private val visited = mutableSetOf<Pair<Point, Direction>>()

	private fun energize(initialPosition: Point, initialDirection: Direction): Set<Point> {
		// Queue for points with a direction to be processed
		val queue = ArrayDeque(listOf(initialPosition to initialDirection))

		// Set of directed points that were already energized (visited)
		val energizedTiles = mutableSetOf<Pair<Point, Direction>>()

		while (queue.isNotEmpty()) {
			val directedPoint = queue.removeFirst()
			val (currentPosition, currentDirection) = directedPoint

			// Skip this directed point if it is outside the area bounds or already energized
			if (!bounds.contains(currentPosition) || directedPoint in energizedTiles) continue

			// Determine the direction(s) to move next from the current position based on the contraption
			val directions = when (val contraption = contraptions[currentPosition]) {
				is Contraption.Splitter -> {
					if (contraption.isHorizontal) {
						if (currentDirection == Direction.NORTH || currentDirection == Direction.SOUTH) {
							// Split the beam into two west- and east-heading beams
							listOf(Direction.WEST, Direction.EAST)
						} else {
							// Continue in the current direction
							listOf(currentDirection)
						}
					} else {
						if (currentDirection == Direction.EAST || currentDirection == Direction.WEST) {
							// Split the beam into two north- and south-heading beams
							listOf(Direction.NORTH, Direction.SOUTH)
						} else {
							// Continue in the current direction
							listOf(currentDirection)
						}
					}
				}
				is Contraption.Mirror -> {
					// Apply the mirror reflection to the current direction
					val nextDirection = when (currentDirection) {
						Direction.NORTH -> if (contraption.isSlash) Direction.EAST else Direction.WEST
						Direction.EAST -> if (contraption.isSlash) Direction.NORTH else Direction.SOUTH
						Direction.SOUTH -> if (contraption.isSlash) Direction.WEST else Direction.EAST
						Direction.WEST -> if (contraption.isSlash) Direction.SOUTH else Direction.NORTH
						else -> throw IllegalArgumentException("Direction $currentDirection not supported")
					}
					listOf(nextDirection)
				}
				null -> listOf(currentDirection)
			}

			directions.forEach { direction ->
				// For each returned direction (one for most tiles and two for splitters), add the point in that direction to the queue
				queue.add(currentPosition.move(direction) to direction)
			}

			energizedTiles.add(directedPoint)
		}

		return energizedTiles.map { it.first }.toSet()
	}

	private sealed interface Contraption {
		data class Splitter(val isHorizontal: Boolean) : Contraption {
			override fun toString() = if (isHorizontal) "-" else "|"
		}
		data class Mirror(val isSlash: Boolean) : Contraption {
			override fun toString() = if (isSlash) "/" else "\\"
		}
	}

}