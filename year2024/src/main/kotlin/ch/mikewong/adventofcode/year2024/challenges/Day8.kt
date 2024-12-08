package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allPairs
import ch.mikewong.adventofcode.common.extensions.toGridNotNull
import ch.mikewong.adventofcode.common.models.Point

class Day8 : Day<Int, Int>(2024, 8, "Resonant Collinearity") {

	private val antennas by lazy { parseInput() }
	private val bounds = inputSize.toArea()

	// 413
	override fun partOne(): Int {
		return countAntinodes(withResonant = false)
	}

	// 1417
	override fun partTwo(): Int {
		return countAntinodes(withResonant = true)
	}

	private fun parseInput(): Map<Point, Char> {
		return inputLines.toGridNotNull { _, c -> c.takeIf { it != '.' } }
	}

	private fun countAntinodes(withResonant: Boolean): Int {
		// Group antennas by frequency, since only those with the same frequency can resonate
		val antennasByFrequency = antennas.entries.groupBy { it.value }
			.map { (_, groupValues) -> groupValues.map { it.key } }

		// For each frequency, find all antinodes between antennas of that frequency
		val antinodes = antennasByFrequency.map { resonantAntennas ->
			// Get all unique pairs of antennas and map them to their antinodes
			val antennaPairs = resonantAntennas.allPairs()
			antennaPairs.flatMap { (a, b) ->
				a.antinodes(b, withResonant)
			}
		}.flatten().toSet()
		return antinodes.count()

	}

	/**
	 * Returns the antinodes between [this] and [other] antennas.
	 * If [withResonant] is false, at most two antinodes exist, one on each side of the line between the antennas.
	 * If [withResonant] is true, all antinodes in the direct line of the antennas are returned, including the antennas themselves.
	 */
	private fun Point.antinodes(other: Point, withResonant: Boolean): List<Point> {
		val dx = this.x - other.x
		val dy = this.y - other.y

		return if (withResonant) {
			(this.generateAntinodes(dx, dy) + other.generateAntinodes(-dx, -dy)).toList()
		} else {
			listOf(
				this.move(dx, dy),
				other.move(-dx, -dy),
			).filter { it in bounds }
		}
	}

	/**
	 * Generates a sequence of points, starting at [this] and moving by [dx] and [dy] until the bounds are reached.
	 * This sequence includes the starting point.
	 */
	private fun Point.generateAntinodes(dx: Long, dy: Long): Sequence<Point> {
		return generateSequence(this) { it.move(dx, dy) }.takeWhile { it in bounds }
	}

}