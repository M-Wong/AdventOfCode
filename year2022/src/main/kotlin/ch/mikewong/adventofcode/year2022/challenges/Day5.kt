package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day5 : Day<String, String>(2022, 5, "Supply Stacks") {

	override fun partOne(): String {
		val stacks = readStacksFromInput()
		inputGroups.last().forEach { line ->
			val move = line.toMove()
			repeat(move.count) {
				stacks[move.to].add(stacks[move.from].removeLast())
			}
		}

		return stacks.joinToString("") { it.last() }
	}

	override fun partTwo(): String {
		val stacks = readStacksFromInput()
		inputGroups.last().forEach { line ->
			val move = line.toMove()
			stacks[move.to].addAll(stacks[move.from].takeLast(move.count))

			repeat(move.count) {
				stacks[move.from].removeLastOrNull()
			}
		}

		return stacks.joinToString("") { it.last() }
	}

	private fun readStacksFromInput(): List<MutableList<String>> {
		val stackCount = inputGroups.first().last().count { !it.isWhitespace() }

		val stacks = MutableList(stackCount) { mutableListOf<String>() }

		// Start from the bottom of the lines, going through each line with a window of 3 characters (the crate "[X]") and step size 4 to skip blanks
		inputGroups.first().dropLast(1).reversed().forEach { line ->
			val input = line.substring(1).windowed(1, 4)
			input.forEachIndexed { stackIndex, crate ->
				if (crate.isNotBlank()) {
					stacks[stackIndex].add(crate)
				}
			}
		}

		return stacks
	}

	private fun String.toMove() : Move {
		val numbers = this.split(" ").mapNotNull { it.toIntOrNull() }
		return Move(numbers[0], numbers[1] - 1, numbers[2] - 1)
	}

	private data class Move(val count: Int, val from: Int, val to: Int)

}