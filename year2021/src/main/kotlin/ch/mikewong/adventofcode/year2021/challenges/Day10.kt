package ch.mikewong.adventofcode.year2021.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.util.middle
import java.util.ArrayDeque

class Day10 : Day<Long, Long>(2021, 10, "Syntax Scoring") {

	private val characters = mapOf(
		'(' to ')', ')' to '(',
		'[' to ']', ']' to '[',
		'{' to '}', '}' to '{',
		'<' to '>', '>' to '<',
	)

	private val openingCharacters = listOf('(', '[', '{', '<')

	override fun partOne(): Long {
		var syntaxScore = 0L

		inputLines.forEach line@{ line ->
			val stack = ArrayDeque<Char>()

			line.toCharArray().forEach { char ->
				// Iterate the characters, putting them on the stack if they are opening characters
				if (char in openingCharacters) {
					stack.push(char)
				} else {
					// If it's not an opening character, check if it closes the last opening and if not, add it to the syntax score
					val lastOpening = stack.pop()
					if (!char.doesClose(lastOpening)) {
						syntaxScore += char.getSyntaxScore(isCorrupt = true)
						return@line
					}
				}
			}
		}

		return syntaxScore
	}

	override fun partTwo(): Long {
		val syntaxScores = mutableListOf<Long>()

		inputLines.forEach line@{ line ->
			val stack = ArrayDeque<Char>()

			line.toCharArray().forEach char@{ char ->
				// Iterate the characters, putting them on the stack if they are opening characters
				if (char in openingCharacters) {
					stack.push(char)
				} else {
					val lastOpening = stack.pop()
					if (!char.doesClose(lastOpening)) {
						// Skip this line if it is corrupt
						return@line
					}
				}
			}

			// Calculate the score of an incomplete line by iterating the stack of remaining opening characters and cumulating their score
			val score = stack.map { unclosedChar ->
				val closingChar = characters[unclosedChar]!!
				closingChar.getSyntaxScore(isCorrupt = false)
			}.reduce { acc, score -> (acc * 5) + score }
			syntaxScores.add(score)
		}

		return syntaxScores.sorted().middle()
	}

	private fun Char.doesClose(other: Char): Boolean = characters[this] == other

	private fun Char.getSyntaxScore(isCorrupt: Boolean): Long = when (this) {
		')' -> if (isCorrupt) 3 else 1
		']' -> if (isCorrupt) 57 else 2
		'}' -> if (isCorrupt) 1197 else 3
		'>' -> if (isCorrupt) 25137 else 4
		else -> throw IllegalArgumentException("Invalid character $this")
	}

}