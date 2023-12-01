package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.firstDigit
import ch.mikewong.adventofcode.common.extensions.indexOfOrElse
import ch.mikewong.adventofcode.common.extensions.lastDigit

class Day1 : Day<Int, Int>(2023, 1, "Trebuchet?!") {
	private val numbers = listOf(
		"zero",
		"one",
		"two",
		"three",
		"four",
		"five",
		"six",
		"seven",
		"eight",
		"nine",
	)

	override fun partOne(): Int {
		return inputLines.sumOf { line ->
			val firstDigit = line.firstDigit() ?: 0
			val lastDigit = line.lastDigit() ?: 0
			(firstDigit * 10) + lastDigit
		}
	}

	override fun partTwo(): Int {
		return inputLines.sumOf { line ->
			val firstDigit = line.firstDigit() ?: 0
			val firstSpelledNumber = numbers.minBy { line.indexOfOrElse(it) }
			val firstNumber = firstDigit.takeIf {
				line.indexOfOrElse(it.toString()) < line.indexOfOrElse(firstSpelledNumber)
			} ?: numbers.indexOf(firstSpelledNumber)

			val lastDigit = line.lastDigit() ?: 0
			val lastSpelledNumber = numbers.maxBy { line.lastIndexOf(it) }
			val lastNumber = lastDigit.takeIf {
				line.lastIndexOf(it.toString()) > line.lastIndexOf(lastSpelledNumber)
			} ?: numbers.indexOf(lastSpelledNumber)

			(firstNumber * 10) + lastNumber
		}
	}

}