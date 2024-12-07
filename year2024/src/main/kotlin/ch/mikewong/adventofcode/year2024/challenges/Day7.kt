package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day7 : Day<Long, Long>(2024, 7, "Bridge Repair") {

	private val equations by lazy { parseInput() }

	// 303766880536
	override fun partOne(): Long {
		return equations.filter { canBeSolved(it.result, it.operands, withConcatenation = false) }
			.sumOf { it.result }
	}

	// 337041851384440
	override fun partTwo(): Long {
		return equations.filter { canBeSolved(it.result, it.operands, withConcatenation = true) }
			.sumOf { it.result }
	}

	private fun parseInput(): List<Equation> {
		return inputLines.map { line ->
			val (testValue, operandString) = line.split(": ")
			val operands = operandString.split(" ").map { it.toLong() }
			Equation(testValue.toLong(), operands)
		}
	}

	private fun canBeSolved(expectedResult: Long, operands: List<Long>, withConcatenation: Boolean): Boolean {
		val first = operands[0]
		val second = operands[1]
		val remaining = operands.drop(2)

		// Early return if the first or second number is already greater than the expected result (since we can only multiply or add)
		if (first > expectedResult || second > expectedResult) return false

		val sum = first + second
		val product = first * second

		if (remaining.isEmpty()) {
			// If the remainder is empty, check if the sum or product is equal to the expected result
			if (sum == expectedResult || product == expectedResult) return true
		} else {
			// Otherwise, recursively check the remaining operands with the sum or product
			if (canBeSolved(expectedResult, listOf(sum) + remaining, withConcatenation)) return true
			if (canBeSolved(expectedResult, listOf(product) + remaining, withConcatenation)) return true
		}

		if (withConcatenation) {
			val concatenated = "$first$second".toLong()
			if (remaining.isEmpty()) {
				// If the remainder is empty, check if the concatenation is equal to the expected result
				if (concatenated == expectedResult) return true
			} else {
				// Otherwise, recursively check the remaining operands with the concatenation
				if (canBeSolved(expectedResult, listOf(concatenated) + remaining, true)) return true
			}
		}

		return false
	}

	data class Equation(val result: Long, val operands: List<Long>)
}