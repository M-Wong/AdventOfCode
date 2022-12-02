package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day2 : Day<Int, Int>(2022, 2, "Rock Paper Scissors") {

	override fun partOne(): Int {
		return inputLines.sumOf { line ->
			val parts = line.split(' ')
			val opponent = parts.first().toOption()
			val me = parts.last().toOption()
			me.value + me.outcome(opponent).value
		}
	}

	override fun partTwo(): Int {
		val a = inputLines.map { line ->
			val parts = line.split(' ')
			val opponent = parts.first().toOption()
			val outcome = parts.last().toOutcome()
			outcome.value + outcome.option(opponent).value
		}
		return a.sum()
	}

	private fun String.toOption() = Option.values().first { it.identifiers.contains(this) }

	private fun String.toOutcome() = Outcome.values().first { it.identifier == this }

	private enum class Option(val value: Int, vararg val identifiers: String) {
		ROCK(1, "A", "X"),
		PAPER(2, "B", "Y"),
		SCISSOR(3, "C", "Z");

		fun outcome(other: Option) = if (this == other) {
			Outcome.DRAW
		} else {
			when (this) {
				ROCK -> if (other == SCISSOR) Outcome.WIN else Outcome.LOSE
				PAPER -> if (other == ROCK) Outcome.WIN else Outcome.LOSE
				SCISSOR -> if (other == PAPER) Outcome.WIN else Outcome.LOSE
			}
		}

		fun winsAgainst() = when (this) {
			ROCK -> SCISSOR
			PAPER -> ROCK
			SCISSOR -> PAPER
		}

		fun losesAgainst() = when (this) {
			ROCK -> PAPER
			PAPER -> SCISSOR
			SCISSOR -> ROCK
		}
	}

	private enum class Outcome(val value: Int, val identifier: String) {
		LOSE(0, "X"),
		DRAW(3, "Y"),
		WIN(6, "Z");

		fun option(other: Option) = when (this) {
			LOSE -> other.winsAgainst()
			DRAW -> other
			WIN -> other.losesAgainst()
		}
	}
}