package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

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
		val borderLength = digInstructions.sumOf { it.meters }
		val borderPoints = digInstructions.runningFold(Point(0, 0)) { previous, instruction ->
			previous.move(instruction.direction, instruction.meters)
		}
		return calculateLagoonArea(borderLength, borderPoints)
	}

	override fun partTwo(): Long {
		val borderLength = digInstructions.sumOf { it.actualMeters }
		val borderPoints = digInstructions.runningFold(Point(0, 0)) { previous, instruction ->
			previous.move(instruction.actualDirection, instruction.actualMeters)
		}
		return calculateLagoonArea(borderLength, borderPoints)
	}

	private fun calculateLagoonArea(borderLength: Long, borderPoints: List<Point>): Long {
		// Use the shoelace formula to calculate the area of the polygon defined by the border points
		val shoelaceArea = borderPoints.shoelaceArea().roundToLong()

		// The shoelace area takes points as infinitely small and therefore half of each point is outside and needs to be accounted for
		val halfBorderLength = borderLength / 2

		// Since the shoelace area goes through the center of a point, for an outside corner this will effectively be 0.25 and for an inside corner 0.75
		// For a closed polygon, there will always be 4 outside corners more than inside corners, so we need to add another 1 point to the area
		return shoelaceArea + halfBorderLength + 1
	}

	private fun List<Point>.shoelaceArea(): Double {
		return (this.plus(this.first()).zipWithNext().sumOf { (a, b) -> (a.x * b.y) - (a.y * b.x) } / 2.0).absoluteValue
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