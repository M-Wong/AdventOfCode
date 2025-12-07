package ch.mikewong.adventofcode.year2025.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day6 : Day<Long, Long>(2025, 6, "Trash Compactor") {

	private val operatorLine by lazy { inputLines.last() }
	private val operandLines by lazy { inputLines.dropLast(1) }

	/** 4405895212738 */
	override fun partOne(): Long {
		val operands = mutableListOf<MutableList<Long>>()

		// Split operand lines by whitespace and add them to the correct operand list
		operandLines.forEach { line ->
			val parts = line.split("\\s+".toRegex()).filter { it.isNotEmpty() }
			parts.forEachIndexed { idx, part ->
				if (idx > operands.lastIndex) {
					operands.add(mutableListOf(part.toLong()))
				} else {
					operands[idx].add(part.toLong())
				}
			}
		}

		// Split the operator line by whitespace and create problems with their corresponding operands
		val problems = operatorLine
			.split("\\s+".toRegex())
			.filter { it.isNotEmpty() }
			.mapIndexed { idx, sign ->
				val operation = when (sign) {
					"+" -> Operation.ADDITION
					"*" -> Operation.MULTIPLICATION
					else -> throw IllegalArgumentException("Invalid sign: $sign")
				}
				Problem(
					operands = operands[idx],
					operation = operation,
				)
			}

		return problems.sumOf { it.solve() }
	}

	/** 7450962489289 */
	override fun partTwo(): Long {
		val problems = mutableListOf<Problem>()
		var currentOperation: Operation? = null
		val currentOperands = mutableListOf<Long>()

		// Iterate through each character in the operator line
		operatorLine.forEachIndexed { idx, char ->

			// If a non-space character is found, a new problem has started
			if (char != ' ') {
				if (currentOperation != null) {
					// If this isn't the first problem, save the previous one
					problems.add(
						Problem(
							operands = currentOperands.toList(),
							operation = currentOperation,
						)
					)
					currentOperands.clear()
				}

				// Update the current operation based on the character
				when (char) {
					'+' -> currentOperation = Operation.ADDITION
					'*' -> currentOperation = Operation.MULTIPLICATION
				}
			}

			// Construct the operand by getting the character from each operand line
			val operand = operandLines.mapNotNull { line ->
				line[idx].takeIf { it != ' ' }
			}.joinToString("").toLongOrNull()

			// If the operand is valid, add it to the list
			if (operand != null) {
				currentOperands.add(operand)
			}
		}

		return problems.sumOf { it.solve() }
	}

	private enum class Operation() {
		ADDITION,
		MULTIPLICATION,
	}

	private data class Problem(
		val operands: List<Long>,
		val operation: Operation,
	) {
		fun solve(): Long {
			return operands.reduce { acc, operand ->
				when (operation) {
					Operation.ADDITION -> acc + operand
					Operation.MULTIPLICATION -> acc * operand
				}
			}
		}
	}

}