package ch.mikewong.adventofcode.challenges

class Day3 : Day<Int, Int>(3, "Binary Diagnostic") {

	private val inputLength = rawInput.first().length

	override fun partOne(): Int {
		val gammaRate = IntArray(inputLength) { rawInput.getMostCommonBitAt(it) }.joinToString("").toInt(2)
		val epsilonRate = IntArray(inputLength) { rawInput.getLeastCommonBitAt(it) }.joinToString("").toInt(2)

		return gammaRate * epsilonRate
	}

	override fun partTwo(): Int {
		var oxygenRatingList = rawInput
		var co2RatingList = rawInput

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