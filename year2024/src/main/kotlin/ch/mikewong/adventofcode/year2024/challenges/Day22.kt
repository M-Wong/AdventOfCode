package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.asLongs

class Day22 : Day<Long, Long>(2024, 22, "Monkey Market") {

	companion object {
		private const val PRUNE_MODULO = 16777216
	}

	private val initialSecretNumbers by lazy { inputLines.asLongs() }

	// 13764677935
	override fun partOne(): Long {
		val finalSecretNumbers = initialSecretNumbers.map { initial ->
			(1..2000).fold(initial) { secretNumber, _ -> calculateNextSecretNumber(secretNumber) }
		}

		return finalSecretNumbers.sum()
	}

	// 1619
	override fun partTwo(): Long {
		// Keep a map of sequences to total bananas these sequences have produced
		val bananasPerSequence = mutableMapOf<List<Long>, Long>()

		initialSecretNumbers.forEach { initial ->
			// Keep track of which sequence was already seen for this buyer (since we sell immediately after the first sequence)
			val previousSequences = mutableSetOf<List<Long>>()
			val priceChanges = mutableListOf<Long>()

			var previousSecretNumber = initial
			var previousPrice = initial % 10

			repeat(2000) {
				// Calculate the next secret number and its price
				val currentSecretNumber = calculateNextSecretNumber(previousSecretNumber)
				val currentPrice = currentSecretNumber % 10

				// Calculate the price change
				val priceChange = currentPrice - previousPrice
				priceChanges.add(priceChange)

				if (priceChanges.size >= 4) {
					// Take the last four price changes as the current sequence and add the current price to the total, if the sequence has not yet been encountered
					val currentSequence = priceChanges.takeLast(4)
					if (!previousSequences.contains(currentSequence)) {
						bananasPerSequence[currentSequence] = bananasPerSequence.getOrDefault(currentSequence, 0) + currentPrice
						previousSequences.add(currentSequence)
					}
				}

				previousSecretNumber = currentSecretNumber
				previousPrice = currentPrice
			}
		}

		return bananasPerSequence.values.max()
	}

	private fun calculateNextSecretNumber(input: Long): Long {
		var result = input

		// Step 1: Multiplication by 64
		val step1 = result * 64
		result = (result xor step1) % PRUNE_MODULO

		// Step 2: Division by 32
		val step2 = result / 32
		result = (result xor step2) % PRUNE_MODULO

		// Step 3: Multiplication by 2048
		val step3 = result * 2048
		result = (result xor step3) % PRUNE_MODULO

		return result
	}

}