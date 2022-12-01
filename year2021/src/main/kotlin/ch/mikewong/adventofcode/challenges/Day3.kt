package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day3 : Day<Int, Int>(2021, 3, "Binary Diagnostic") {

	private val inputLength = inputLines.first().length

	override fun partOne(): Int {
		val gammaRate = IntArray(inputLength) { inputLines.getMostCommonBitAt(it) }.joinToString("").toInt(2)
		val epsilonRate = IntArray(inputLength) { inputLines.getLeastCommonBitAt(it) }.joinToString("").toInt(2)

		return gammaRate * epsilonRate
	}

	override fun partTwo(): Int {
		var oxygenRatingList = inputLines
		var co2RatingList = inputLines

		val range = 0 until inputLength
		range.forEach { position ->
			if (oxygenRatingList.size > 1) {
				oxygenRatingList = oxygenRatingList.filterWithBitAt(oxygenRatingList.getMostCommonBitAt(position), position)
			}

			if (co2RatingList.size > 1) {
				co2RatingList = co2RatingList.filterWithBitAt(co2RatingList.getLeastCommonBitAt(position), position)
			}
		}

		val oxygenRating = oxygenRatingList.single().toInt(2)
		val co2Rating = co2RatingList.single().toInt(2)

		return oxygenRating * co2Rating
	}

	private fun List<String>.filterWithBitAt(bit: Int, position: Int): List<String> {
		return filter { it[position] == bit.digitToChar() }
	}

	private fun List<String>.getMostCommonBitAt(position: Int): Int {
		val oneCount = count { it[position] == '1' }
		val zeroCount = count { it[position] == '0' }
		return if (oneCount >= zeroCount) 1 else 0
	}

	private fun List<String>.getLeastCommonBitAt(position: Int): Int {
		val oneCount = count { it[position] == '1' }
		val zeroCount = count { it[position] == '0' }
		return if (oneCount < zeroCount) 1 else 0
	}
}