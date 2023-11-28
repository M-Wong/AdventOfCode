package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.asLongs
import ch.mikewong.adventofcode.common.extensions.product
import ch.mikewong.adventofcode.common.models.ArithmeticOperation

class Day11 : Day<Long, Long>(2022, 11, "Monkey in the Middle") {

	override fun partOne(): Long {
		val monkeys = readInput()
		repeat(20) {
			monkeys.forEach { monkey ->
				// Inspect the items and divide the new worry level by 3 to keep the worry level down
				val throwingItems = monkey.inspect { it / 3 }

				// Clear this monkeys items and then throw the items to the correct monkey
				monkey.items.clear()
				throwingItems.forEach { (monkey, itemWorryLevel) ->
					monkeys[monkey].items.add(itemWorryLevel)
				}
			}
		}

		return monkeys.map { it.inspectionCounter }.sortedDescending().take(2).product()
	}

	override fun partTwo(): Long {
		val monkeys = readInput()

		// Calculate the least common multiple of all monkey test divisors
		val leastCommonMultiple = monkeys.map { it.testDivisor }.distinct().product()
		repeat(10_000) {
			monkeys.forEach { monkey ->
				// Inspect the items and modulo the new worry level by the least common multiple of all monkeys to keep the worry level down
				val throwingItems = monkey.inspect { it % leastCommonMultiple }

				// Clear this monkeys items and then throw the items to the correct monkey
				monkey.items.clear()
				throwingItems.forEach { (monkey, itemWorryLevel) ->
					monkeys[monkey].items.add(itemWorryLevel)
				}
			}
		}

		return monkeys.map { it.inspectionCounter }.sortedDescending().take(2).product()
	}

	private fun readInput() : List<Monkey> {
		return inputGroups.map { lines ->
			val startingItems = lines[1].trim().removePrefix("Starting items: ").split(", ").asLongs()
			val operation = lines[2].trim().removePrefix("Operation: new = ").toOperation()
			val testDivisor = lines[3].trim().removePrefix("Test: divisible by ").toInt()
			val throwToMonkeyIfTrue = lines[4].trim().removePrefix("If true: throw to monkey ").toInt()
			val throwToMonkeyIfFalse = lines[5].trim().removePrefix("If false: throw to monkey ").toInt()

			Monkey(startingItems, operation, testDivisor, throwToMonkeyIfTrue, throwToMonkeyIfFalse)
		}
	}

	private class Monkey(
		startingItems: List<Long>,
		val operation: Operation,
		val testDivisor: Int,
		val throwToMonkeyIfTrue: Int,
		val throwToMonkeyIfFalse: Int,
	) {
		val items = startingItems.toMutableList()
		var inspectionCounter = 0L

		fun inspect(worryLevelManagement: (Long) -> Long): List<Pair<Int, Long>> {
			return items.map { itemWorryLevel ->
				// Calculate the new worry level by applying the arithmetic operation and then the worry level management lambda
				val newWorryLevel = worryLevelManagement.invoke(operation.apply(itemWorryLevel))

				// Decide which monkey to throw the item to
				val monkeyToThrowTo = if (newWorryLevel % testDivisor == 0L) throwToMonkeyIfTrue else throwToMonkeyIfFalse

				inspectionCounter++

				monkeyToThrowTo to newWorryLevel
			}
		}
	}

	private data class Operation(private val type: ArithmeticOperation, private val operandA: Value, private val operandB: Value) {
		fun apply(old: Long): Long {
			val valueA = when (operandA) {
				is Value.Old -> old
				is Value.Number -> operandA.value
			}
			val valueB = when (operandB) {
				is Value.Old -> old
				is Value.Number -> operandB.value
			}
			return type.apply(valueA, valueB)
		}
	}

	private sealed class Value {
		data object Old : Value()
		data class Number(val value: Long) : Value()
	}

	private fun String.toValue() = if (this == "old") Value.Old else Value.Number(this.toLong())

	private fun String.toOperation(): Operation {
		val parts = this.split(" ")
		val operationType = ArithmeticOperation.fromString(parts[1])
		val operandA = parts[0].toValue()
		val operandB = parts[2].toValue()
		return Operation(operationType, operandA, operandB)
	}

}