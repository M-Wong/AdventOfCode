package ch.mikewong.adventofcode.year2025.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.isDivisibleBy

class Day2 : Day<Long, Long>(2025, 2, "Gift Shop") {

	val ranges by lazy {
		input.split(",").map {
			val (start, end) = it.split("-")
			start.toLong()..end.toLong()
		}
	}

	/** 38310256125 */
	override fun partOne(): Long {
		val invalidIds = ranges.flatMap { it.getInvalidTupleIds() }
		return invalidIds.sum()
	}

	/** 58961152806 */
	override fun partTwo(): Long {
		val invalidIds = ranges.flatMap { range ->
			// Filter invalid numbers within the range
			range.filter { number ->
				val value = number.toString()
				val maxChunkLength = value.length / 2

				// Iterate each possible chunk length
				(1..maxChunkLength).forEach { i ->

					// If the value length is divisible by the chunk length, check if repeating the chunk forms the original value
					if (value.length.isDivisibleBy(i)) {
						val chunk = value.substring(0, i)
						val newValue = chunk.repeat(value.length / i)
						if (newValue == value) {
							return@filter true
						}
					}
				}
				false
			}
		}
		return invalidIds.sum()
	}

	private fun LongRange.getInvalidTupleIds(): List<Long> {
		val start = this.start
		val end = this.endInclusive

		val invalidIds = mutableListOf<Long>()

		// Take the first half of the range start ID
		var current = start.getFirstHalf()

		// Generate the tuple ID
		var currentId = "$current$current".toLong()

		// If the tuple ID is within the range, add it to the invalid IDs and increment the first half of the ID and generete the new tuple ID
		while (currentId <= end) {
			if (currentId in this) invalidIds.add(currentId)
			current++
			currentId = "$current$current".toLong()
		}

		return invalidIds
	}

	private fun Long.getFirstHalf(): Long {
		val str = this.toString()
		val halfLength = str.length / 2
		return if (str.length % 2 == 1) {
			generateLowestNumber(halfLength + 1)
		} else {
			str.substring(0, halfLength).toLong()
		}
	}

	private fun generateLowestNumber(length: Int): Long {
		return "1".padEnd(length, '0').toLong()
	}

}