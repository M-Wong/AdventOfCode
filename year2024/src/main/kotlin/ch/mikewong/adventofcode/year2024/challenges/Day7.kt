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

	/**
	 * Improved implementation, that recursively checks if the [expectedResult] can be reached with the given operands.
	 * This goes in reverse from the [expectedResult], checks if it can be reached by applying the last operand in the list, then calls itself again.
	 * This algorithm runs part 2 in ~4ms on my machine
	 */
	private fun canBeSolved(expectedResult: Long, operands: List<Long>, withConcatenation: Boolean): Boolean {
		if (operands.size == 1) {
			return operands[0] == expectedResult
		} else {
			val lastOperand = operands.last()

			// Check if addition can be used to reach the expected result
			if (expectedResult > lastOperand) {
				if (canBeSolved(expectedResult - lastOperand, operands.dropLast(1), withConcatenation)) return true
			}

			// Check if multiplication can be used to reach the expected result
			if (expectedResult % lastOperand == 0L) {
				if (canBeSolved(expectedResult / lastOperand, operands.dropLast(1), withConcatenation)) return true
			}

			if (withConcatenation) {
				// Check if concatenation can be used to reach the expected result
				val expectedString = expectedResult.toString()
				val operandString = lastOperand.toString()

				if (expectedString.endsWith(operandString) && expectedString != operandString) {
					if (canBeSolved(expectedString.removeSuffix(operandString).toLong(), operands.dropLast(1), true)) return true
				}
			}

			return false
		}
	}

	/**
	 * First implementation, that recursively checks if the [expectedResult] can be reached with the given operands.
	 * This applies the operations to the first two numbers of the [operands] list and adds it to the beginning of the operands, then calls itself again.
	 * This algorithm runs part 2 in ~650ms on my machine
	 */
	private fun canBeSolvedRecursively(expectedResult: Long, operands: List<Long>, withConcatenation: Boolean): Boolean {
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
			if (canBeSolvedRecursively(expectedResult, listOf(sum) + remaining, withConcatenation)) return true
			if (canBeSolvedRecursively(expectedResult, listOf(product) + remaining, withConcatenation)) return true
		}

		if (withConcatenation) {
			val concatenated = "$first$second".toLong()
			if (remaining.isEmpty()) {
				// If the remainder is empty, check if the concatenation is equal to the expected result
				if (concatenated == expectedResult) return true
			} else {
				// Otherwise, recursively check the remaining operands with the concatenation
				if (canBeSolvedRecursively(expectedResult, listOf(concatenated) + remaining, true)) return true
			}
		}

		return false
	}

	data class Equation(val result: Long, val operands: List<Long>)
}