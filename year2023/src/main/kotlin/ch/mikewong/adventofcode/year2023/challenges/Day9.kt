package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allInts

class Day9 : Day<Int, Int>(2023, 9, "Mirage Maintenance") {

	private val valueHistories = inputLines.map { it.allInts() }

	override fun partOne(): Int {
		val sequences = getHistoricalDiffSequences()
		return sequences.sumOf { sequence ->
			// To get the predicted future value, reverse the diff sequences and accumulate the last value of each sequence
			sequence.reversed().fold(0) { acc, seq -> seq.last() + acc }.toInt()
		}
	}

	override fun partTwo(): Int {
		val sequences = getHistoricalDiffSequences()
		return sequences.sumOf { sequence ->
			// To get the predicted past value, reverse the diff sequence and subtract the previous value
			sequence.reversed().fold(0) { acc, seq -> seq.first() - acc }.toInt()
		}
	}

	private fun getHistoricalDiffSequences(): List<List<List<Int>>> {
		return valueHistories.map { valueHistory ->
			// For each value history, generate a list of difference sequences until there is a sequence with only zeros
			buildList {
				var differences = valueHistory
				do {
					add(differences)
					differences = differences.getDifferences()
				} while (differences.any { it != 0 })
			}
		}
	}

	private fun List<Int>.getDifferences() = this.zipWithNext().map { (a, b) -> b - a }

}