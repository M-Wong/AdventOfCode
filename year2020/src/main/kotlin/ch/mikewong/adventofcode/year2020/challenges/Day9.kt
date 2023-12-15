package ch.mikewong.adventofcode.year2020.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.asLongs

class Day9 : Day<Long, Long>(2020, 9, "Encoding Error") {

	private val preambleSize get() = if (isControlSet) 5 else 25
	private val numbers = inputLines.asLongs()

	override fun partOne(): Long {
		return findFirstInvalidNumber()
	}

	override fun partTwo(): Long {
		val invalidNumber = findFirstInvalidNumber()
		val slidingWindow = mutableListOf<Long>()
		numbers.forEach { number ->
			var sum = slidingWindow.sum()
			when {
				// If the sum equals the invalid number, return the sum of the smallest and largest number
				sum == invalidNumber -> return slidingWindow.min() + slidingWindow.max()

				// If the sum plus the next number is larger, add the number and drop until the sum is smaller again
				sum + number > invalidNumber -> {
					sum += number
					slidingWindow.add(number)
					do {
						sum -= slidingWindow.removeFirst()
					} while (sum > invalidNumber)
				}

				// Otherwise just add the next number and continue
				else -> slidingWindow.add(number)
			}
		}

		throw IllegalStateException("No matching set found")
	}

	private fun findFirstInvalidNumber() = numbers.windowed(preambleSize + 1)
		.first { numbers ->
			val preamble = numbers.take(preambleSize)
			val checkNumber = numbers.last()
			!checkNumber.isNumberValid(preamble)
		}.last()

	private fun Long.isNumberValid(preamble: List<Long>): Boolean {
		return preamble.any { a ->
			preamble.firstOrNull { this - a == it } != null
		}
	}

}