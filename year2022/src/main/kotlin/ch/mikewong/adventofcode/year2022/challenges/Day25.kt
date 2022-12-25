package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.pow

class Day25 : Day<String, Int>(2022, 25, "Full of Hot Air") {

	private val snafuCharacters = listOf('=', '-', '0', '1', '2')

	override fun partOne(): String {
		val totalFuelRequirements = inputLines.sumOf { line ->
			line.reversed().mapIndexed { idx, c -> c.fromSnafu(idx) }.sum()
		}
		return totalFuelRequirements.toSnafu()
	}

	override fun partTwo(): Int {
		// Merry Christmas
		return 0
	}

	private fun Char.fromSnafu(position: Int): Long {
		val value = snafuCharacters.indexOf(this)
		if (value == -1) throw IllegalArgumentException("Unknown SNAFU value $this")
		// Shift the index by two to get the value range from -2 to 2
		return (value - 2) * 5.pow(position).toLong()
	}

	private fun Long.toSnafu(): String {
		var decimalNumber = this
		return buildString {
			while (decimalNumber > 0) {
				// Convert decimal number to snafu number (base-5 shifted by 2)
				val n = (decimalNumber + 2) % 5
				append(snafuCharacters[n.toInt()])
				decimalNumber = (decimalNumber + 2) / 5
			}
		}.reversed()
	}
}