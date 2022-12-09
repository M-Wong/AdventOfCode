package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day9 : Day<Int, Int>(2022, 9, "Rope Bridge") {

	private val moves by lazy { readInput() }

	override fun partOne() = countVisitedTailPosition(2)

	override fun partTwo() = countVisitedTailPosition(10)

	private fun readInput(): List<Move> {
		return inputLines.map { line ->
			val parts = line.split(" ")
			val direction = when (parts[0]) {
				"U" -> Direction.NORTH
				"R" -> Direction.EAST
				"D" -> Direction.SOUTH
				"L" -> Direction.WEST
				else -> throw IllegalArgumentException("Unknown direction")
			}
			Move(direction, parts[1].toInt())
		}
	}

	private fun countVisitedTailPosition(ropeLength: Int): Int {
		val rope = MutableList(ropeLength) { Point(0, 0) }

		val visited = mutableSetOf(rope.last())

		moves.forEach { move ->
			repeat(move.steps) {
				rope.forEachIndexed { idx, point ->
					if (idx == 0) {
						// Move the head in the right direction
						rope[idx] = point.move(move.direction)
					} else {
						val previous = rope[idx - 1]
						if (!previous.isAdjacentTo(point)) {
							// Only move the point if it is not adjacent anymore to the previous point
							val deltaX = (previous.x - point.x).coerceIn(-1, 1)
							val deltaY = (previous.y - point.y).coerceIn(-1, 1)
							rope[idx] = point.move(deltaX, deltaY)
						}
					}
				}
				visited.add(rope.last())

			}
		}

		return visited.size
	}

	private data class Move(val direction: Direction, val steps: Int)

}