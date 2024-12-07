package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day7 : Day<Long, Long>(2024, 7, "Bridge Repair") {

	private val equations by lazy { parseInput() }

	// 303766880536
	override fun partOne(): Long {
		return equations.filter { it.canBeSolved(withConcatenation = false) }
			.sumOf { it.result }
	}

	// 337041851384440
	override fun partTwo(): Long {
		return equations.filter { it.canBeSolved(withConcatenation = true) }
			.sumOf { it.result }
	}

	private fun parseInput(): List<Equation> {
		return inputLines.map { line ->
			val (testValue, operandString) = line.split(": ")
			val operands = operandString.split(" ").map { it.toLong() }
			Equation(testValue.toLong(), operands)
		}
	}

	data class Equation(val result: Long, val operands: List<Long>) {
		fun canBeSolved(withConcatenation: Boolean): Boolean {
			val remainingPossibilities = mutableListOf(operands)

			while (remainingPossibilities.isNotEmpty()) {
				val nextPossibilities = mutableListOf<List<Long>>()

				for (possibility in remainingPossibilities) {
					val first = possibility[0]
					val second = possibility[1]
					val remaining = possibility.drop(2)

					// If there is more than one number left, try adding and multiplying the first two numbers and create new possibilities
					val sum = first + second
					val product = first * second

					// Check if the result is reached, if not generate new possibilities
					if (remaining.isEmpty()) {
						if (sum == result || product == result) return true
					} else {
						nextPossibilities.add(listOf(sum) + remaining)
						nextPossibilities.add(listOf(product) + remaining)
					}

					// If concatenation is allowed, try concatenating the first two numbers, then check if the result is reached or else generate new possibilities
					if (withConcatenation) {
						val concatenated = "$first$second".toLong()
						if (remaining.isEmpty()) {
							if (concatenated == result) return true
						} else {
							nextPossibilities.add(listOf(concatenated) + remaining)
						}
					}
				}

				remainingPossibilities.clear()
				remainingPossibilities.addAll(nextPossibilities)
			}

			return false
		}
	}
}