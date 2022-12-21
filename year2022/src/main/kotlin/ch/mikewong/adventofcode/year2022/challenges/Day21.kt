package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.ArithmeticOperation

class Day21 : Day<Long, Long>(2022, 21, "") {

	private val monkeys by lazy { readInput() }
	private val startMonkey = "root"
	private val humanMonkey = "humn"

	override fun partOne(): Long {
		return findResultOfMonkeyYelling(monkeys, startMonkey)
			?: throw IllegalStateException("Part 1 should not have a nullable branch")
	}

	override fun partTwo(): Long {
		// Get the root monkey and remove the human monkey from the list
		val rootMonkey = monkeys.getValue(startMonkey) as Monkey.Operation
		val monkeys = monkeys.minus(humanMonkey)

		// Check the left and right branch of the root monkey operation
		val leftMonkeyResult = findResultOfMonkeyYelling(monkeys, rootMonkey.leftMonkey)
		val rightMonkeyResult = findResultOfMonkeyYelling(monkeys, rootMonkey.rightMonkey)

		return when {
			leftMonkeyResult == null && rightMonkeyResult != null -> {
				// Human monkey is in the left branch
				findMonkeyValue(monkeys, rightMonkeyResult, rootMonkey.leftMonkey)
			}
			rightMonkeyResult == null && leftMonkeyResult != null -> {
				// Human monkey is in the right branch
				findMonkeyValue(monkeys, leftMonkeyResult, rootMonkey.rightMonkey)
			}
			else -> throw IllegalStateException("Monkey '$humanMonkey' is in neither branches of root monkey")
		}
	}

	private fun findResultOfMonkeyYelling(monkeys: Map<String, Monkey>, monkeyName: String): Long? {
		// Check if monkey exists in the list and return null if not
		val monkey = monkeys[monkeyName]
		return monkey?.let {
			when (it) {
				is Monkey.Primitive -> it.value // Primitive monkeys just return their value
				is Monkey.Operation -> {
					// Operation monkeys recursively check the value of the two monkeys they need and calculate the result
					val leftMonkeyResult = findResultOfMonkeyYelling(monkeys, it.leftMonkey)
					val rightMonkeyResult = findResultOfMonkeyYelling(monkeys, it.rightMonkey)

					if (leftMonkeyResult != null && rightMonkeyResult != null) {
						// If neither are null, the result of this monkey is just the result of its arithmetic operation
						when (it.operation) {
							ArithmeticOperation.PLUS -> leftMonkeyResult + rightMonkeyResult
							ArithmeticOperation.MINUS -> leftMonkeyResult - rightMonkeyResult
							ArithmeticOperation.MULTIPLY -> leftMonkeyResult * rightMonkeyResult
							ArithmeticOperation.DIVIDE -> leftMonkeyResult / rightMonkeyResult
						}
					} else {
						null
					}
				}
			}
		}
	}

	private fun findMonkeyValue(monkeys: Map<String, Monkey>, expectedValue: Long, monkeyName: String): Long {
		// If the monkey name equals the human monkey, the expectedValue parameter is the missing value
		if (monkeyName == humanMonkey) return expectedValue

		val monkey = monkeys.getValue(monkeyName)
		if (monkey is Monkey.Operation) {
			// Get the result of the left and right branch
			val leftMonkeyResult = findResultOfMonkeyYelling(monkeys, monkey.leftMonkey)
			val rightMonkeyResult = findResultOfMonkeyYelling(monkeys, monkey.rightMonkey)

			when {
				leftMonkeyResult == null && rightMonkeyResult != null -> {
					// If the human monkey is in the left branch, calculate the expected value of that monkey by reversing this monkeys arithmetic operation
					val newExpectedValue = when (monkey.operation) {
						ArithmeticOperation.PLUS -> expectedValue - rightMonkeyResult // value == left + right
						ArithmeticOperation.MINUS -> expectedValue + rightMonkeyResult // value == left - right
						ArithmeticOperation.MULTIPLY -> expectedValue / rightMonkeyResult // value == left / right
						ArithmeticOperation.DIVIDE -> expectedValue * rightMonkeyResult // value == left * right
					}
					// Recursively find the expected value of the left monkey (the branch containing the human monkey)
					return findMonkeyValue(monkeys, newExpectedValue, monkey.leftMonkey)
				}
				rightMonkeyResult == null && leftMonkeyResult != null -> {
					// If the human monkey is in the right branch, calculate the expected value of that monkey by reversing this monkeys arithmetic operation
					val newExpectedResult = when (monkey.operation) {
						ArithmeticOperation.PLUS -> expectedValue - leftMonkeyResult // value == left + right
						ArithmeticOperation.MINUS -> leftMonkeyResult - expectedValue // value == left - right
						ArithmeticOperation.MULTIPLY -> expectedValue / leftMonkeyResult // value == left / right
						ArithmeticOperation.DIVIDE -> leftMonkeyResult / expectedValue // value == left * right
					}
					// Recursively find the expected value of the right monkey (the branch containing the human monkey)
					return findMonkeyValue(monkeys, newExpectedResult, monkey.rightMonkey)
				}
				else -> throw IllegalStateException("Monkey '$monkeyName' has two null or two non-null value branches")
			}
		} else {
			throw IllegalStateException("Primitive monkeys should not be checked for the missing value, as their value is known")
		}
	}

	private fun readInput(): Map<String, Monkey> {
		return inputLines.associate { line ->
			val parts = line.split(": ")
			val monkeyName = parts[0]
			val primitive = parts[1].toLongOrNull()
			if (primitive != null) {
				monkeyName to Monkey.Primitive(primitive)
			} else {
				val operationParts = parts[1].split(" ")
				monkeyName to Monkey.Operation(
					ArithmeticOperation.fromString(operationParts[1]),
					operationParts[0],
					operationParts[2]
				)
			}
		}
	}

	private sealed class Monkey {
		data class Primitive(val value: Long) : Monkey()
		data class Operation(val operation: ArithmeticOperation, val leftMonkey: String, val rightMonkey: String) : Monkey()
	}

}