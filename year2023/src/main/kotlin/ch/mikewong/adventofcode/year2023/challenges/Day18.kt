package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.MultiRectangle
import ch.mikewong.adventofcode.common.models.Point

class Day18 : Day<Long, Long>(2023, 18, "Lavaduct Lagoon") {

	private val digInstructions = inputLines.map { line ->
		val matches = requireNotNull("(.*) (\\d+) \\((.+)\\)".toRegex().matchEntire(line)).groupValues
		val direction = when (matches[1]) {
			"U" -> Direction.NORTH
			"R" -> Direction.EAST
			"D" -> Direction.SOUTH
			"L" -> Direction.WEST
			else -> throw IllegalArgumentException("Unsupported direction ${matches[1]}")
		}
		DigInstruction(matches[2].toLong(), direction, matches[3])
	}

	override fun partOne(): Long {
		val borderPoints = digInstructions.runningFold(Point(0, 0)) { previous, instruction ->
			previous.move(instruction.direction, instruction.meters)
		}
		return MultiRectangle(borderPoints).getPointsWithin(includeEdge = true)
	}

	override fun partTwo(): Long {
		val borderPoints = digInstructions.runningFold(Point(0, 0)) { previous, instruction ->
			previous.move(instruction.actualDirection, instruction.actualMeters)
		}
		return MultiRectangle(borderPoints).getPointsWithin(includeEdge = true)
	}

	@OptIn(ExperimentalStdlibApi::class)
	private data class DigInstruction(val meters: Long, val direction: Direction, val color: String) {
		val actualMeters by lazy { color.drop(1).dropLast(1).hexToLong() }
		val actualDirection by lazy {
			when (color.last()) {
				'0' -> Direction.EAST
				'1' -> Direction.SOUTH
				'2' -> Direction.WEST
				'3' -> Direction.NORTH
				else -> throw IllegalArgumentException("Unsupported direction ${color.last()}")
			}
		}
	}

}