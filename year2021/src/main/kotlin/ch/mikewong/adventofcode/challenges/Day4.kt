package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.util.asInts

class Day4 : Day<Int, Int>(2021, 4, "Giant Squid") {

	private val numbers = inputLines.first().split(",").asInts()
	private val boards = inputGroups.drop(1).map { group ->
		Board(
			group.map {
				it.trim().split("\\s+".toRegex()).asInts()
			}
		)
	}

	override fun partOne(): Int {
		numbers.indices.forEach { round ->
			val numbersSoFar = numbers.take(round + 1)
			boards.firstOrNull { it.hasWon(numbersSoFar) }?.let {
				return it.sumUnmarkedNumbers(numbersSoFar) * numbersSoFar.last()
			}
		}

		throw IllegalStateException("Could not find a winning board")
	}

	override fun partTwo(): Int {
		val nonWinningBoards = boards.toMutableList()
		numbers.indices.forEach { round ->
			val numbersSoFar = numbers.take(round + 1)
			if (nonWinningBoards.size > 1) {
				nonWinningBoards.removeIf { it.hasWon(numbersSoFar) }
			} else {
				val lastBoard = nonWinningBoards.single()
				if (lastBoard.hasWon(numbersSoFar)) {
					return lastBoard.sumUnmarkedNumbers(numbersSoFar) * numbersSoFar.last()
				}
			}
		}

		throw IllegalStateException("Could not find a winning board")
	}

	private class Board(val board: List<List<Int>>) {
		fun hasWon(numbers: List<Int>): Boolean {
			return board.indices.any { row ->
				val hasRowWon = board[row].all { numbers.contains(it) }
				val hasColumnWon = board.indices.all { numbers.contains(board[it][row]) }
				hasRowWon || hasColumnWon
			}
		}

		fun sumUnmarkedNumbers(numbers: List<Int>): Int {
			return board.sumOf { rows -> rows.filter { !numbers.contains(it) }.sum() }
		}
	}
}