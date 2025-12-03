package ch.mikewong.adventofcode.year2025.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day3 : Day<Long, Long>(2025, 3, "Lobby") {

	/** 17408 */
	override fun partOne(): Long {
		return inputLines.sumOf { it.getLargestNumber(2) }
	}

	/** 172740584266849 */
	override fun partTwo(): Long {
		return inputLines.sumOf { it.getLargestNumber(12) }
	}

	private fun String.getLargestNumber(digitCount: Int): Long {
		// Initialize an array the length of the desired number
		val digits = IntArray(digitCount) { 0 }

		this.forEachIndexed { idx, char ->
			val digit = char.digitToInt()

			var reset = false
			digits.forEachIndexed { pos, current ->
				// For each digit position, check if we should reset the current digit or else compare it to the digit in the string.
				// The lastPossibleIndex is the last index in the string from which we can still fill the remaining digits
				val lastPossibleIndex = this.lastIndex - (digitCount - 1 - pos)
				if (reset) {
					digits[pos] = 0
				} else if (idx <= lastPossibleIndex && digit > current) {
					digits[pos] = digit
					reset = true
				}
			}
		}

		return digits.joinToString("").toLong()
	}

}