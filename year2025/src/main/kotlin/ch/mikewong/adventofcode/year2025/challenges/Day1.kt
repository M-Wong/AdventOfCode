package ch.mikewong.adventofcode.year2025.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day1 : Day<Int, Int>(2025, 1, "Secret Entrance") {

	private val turns by lazy { parseInput() }

	/** 1034 */
	override fun partOne(): Int {
		val (zeroLandings, _) = countZeroPasses()
		return zeroLandings
	}

	/** 6166 */
	override fun partTwo(): Int {
		val (_, zeroPasses) = countZeroPasses()
		return zeroPasses
	}

	private fun parseInput(): List<Turn> {
		return inputLines.map { line ->
			val direction = line[0]
			val amount = line.substring(1).toInt()
			when (direction) {
				'L' -> Turn.Left(amount)
				'R' -> Turn.Right(amount)
				else -> throw IllegalStateException("Unknown direction: $direction")
			}
		}
	}

	private fun countZeroPasses(): Pair<Int, Int> {
		val size = 100
		var current = 50

		// Count the number of times we land on zero and pass over zero
		var zeroLandings = 0
		var zeroPasses = 0

		turns.forEach { turn ->
			// Turn the current position by the specified amount (moduloed to the size of the circle)
			val wasZero = current == 0
			when (turn) {
				is Turn.Left -> current -= turn.amount % size
				is Turn.Right -> current += turn.amount % size
			}

			// Count full rotations
			zeroPasses += turn.amount / size

			when {
				// If we land on zero, count another pass
				current == 0 -> zeroPasses++

				// If we overshoot below zero, wrap around and count an additional pass
				current < 0 -> {
					current += size
					if (!wasZero) zeroPasses++
				}

				// If we overshoot above size, wrap around and count an additional pass
				current >= size -> {
					current -= size
					if (!wasZero) zeroPasses++
				}
			}

			// Count landings on zero
			if (current == 0) zeroLandings++
		}

		return zeroLandings to zeroPasses
	}

	private sealed interface Turn {
		val amount: Int

		data class Left(override val amount: Int): Turn
		data class Right(override val amount: Int): Turn
	}

}