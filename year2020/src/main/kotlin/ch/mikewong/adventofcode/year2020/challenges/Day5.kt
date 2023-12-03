package ch.mikewong.adventofcode.year2020.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day5 : Day<Int, Int>(2020, 5, "Binary Boarding") {

	private val seatIds by lazy { inputLines.map { getSeatId(it) } }

	override fun partOne(): Int {
		return seatIds.max()
	}

	override fun partTwo(): Int {
		val sortedSeatIds = seatIds.sorted()
		val lowestSeatId = sortedSeatIds.first()
		var startIndex = 0
		var endIndex = sortedSeatIds.lastIndex

		// Knowing there is exactly one number missing, use a binary search to find the missing number.
		// Starting from the middle, compare the actual value with its expected value.
		// If there is a mismatch, the gap must be on the left side. Otherwise, it must be on the right side.
		while (startIndex <= endIndex) {
			val middleIndex = (startIndex + endIndex) / 2

			// Check if the missing number is on the left or right side of the middle index
			if (sortedSeatIds[middleIndex] != lowestSeatId + middleIndex) {
				endIndex = middleIndex - 1
			} else {
				startIndex = middleIndex + 1
			}
		}

		// lowIndex will be the index of the missing number. If it equals the list size, that means there was no missing number
		if (startIndex == sortedSeatIds.size) throw IllegalArgumentException("No missing number detected")
		return lowestSeatId + startIndex
	}

	private fun getSeatId(specification: String): Int {
		if (specification.length != 10) throw IllegalArgumentException("Specification has invalid length")

		var rowMin = 0
		var rowMax = 127
		var colMin = 0
		var colMax = 7

		specification.forEach { char ->
			when (char) {
				'F' -> rowMax -= (rowMax - rowMin) / 2 + 1
				'B' -> rowMin += (rowMax - rowMin) / 2 + 1
				'L' -> colMax -= (colMax - colMin) / 2 + 1
				'R' -> colMin += (colMax - colMin) / 2 + 1
				else -> throw IllegalArgumentException("Unknown character $char in specification ($specification)")
			}
		}

		if (rowMin != rowMax) throw IllegalStateException("Specification ($specification) does not define a single row ($rowMin != $rowMax)")
		if (colMin != colMax) throw IllegalStateException("Specification ($specification) does not define a single column ($colMin != $colMax)")

		return rowMin * 8 + colMin
	}

}