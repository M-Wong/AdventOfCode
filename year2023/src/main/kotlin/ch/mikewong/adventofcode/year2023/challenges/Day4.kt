package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allNumbers
import ch.mikewong.adventofcode.common.extensions.pow

class Day4 : Day<Int, Int>(2023, 4, "Scratchcards") {

	private val scratchCards = inputLines.map { line ->
		val (winnings, mine) = line.split("|")
		val winningNumbers = winnings.substringAfter(":").allNumbers()
		val myNumbers = mine.allNumbers()
		ScratchCard(winningNumbers, myNumbers)
	}

	/**
	 * Keep track of how many card copies (recursively) a single card will win in total.
	 * The key is the card index within the scratchCards list and the value will be the number of card copies that card will win
	 */
	private val cardCopiesWon = mutableMapOf<Int, Int>()

	override fun partOne(): Int {
		return scratchCards.sumOf { card ->
			val numberOfWinningNumbers = card.winningNumbers.intersect(card.myNumbers.toSet()).count()
			2.pow(numberOfWinningNumbers - 1).toInt()
		}
	}

	override fun partTwo(): Int {
		// Iterative approach
		val copies = IntArray(scratchCards.size) { 1 }
		scratchCards.forEachIndexed { idx, card ->
			// Iterate each card, then sum add the current number of cards to the ones this card copies
			val numberOfWinningNumbers = card.winningNumbers.intersect(card.myNumbers.toSet()).count()
			(1..numberOfWinningNumbers).forEach { offset ->
				copies[idx + offset] += copies[idx]
			}
		}

		return copies.sum()

		// Recursive approach (~2x slower)
//		scratchCards.indices.forEach { cardIndex ->
//			cardCopiesWon[cardIndex] = countNumberOfCopiesWon(cardIndex)
//		}
//
//		// Sum up the number of card copies won and then add the number of original cards to get the final number
//		return cardCopiesWon.values.sum() + scratchCards.size
	}

	private fun countNumberOfCopiesWon(cardIndex: Int): Int {
		// Early return if this card has already been processed
		cardCopiesWon[cardIndex]?.let { return it }

		val card = scratchCards[cardIndex]
		val numberOfWinningNumbers = card.winningNumbers.intersect(card.myNumbers.toSet()).count()
		return if (cardIndex == scratchCards.lastIndex || numberOfWinningNumbers == 0) {
			0
		} else {
			// The total number of cards this card wins, is the number of winning numbers plus the sum of card copies won by each of those cards
			val totalNumberOfCardsWon =
				numberOfWinningNumbers + (1..numberOfWinningNumbers).sumOf { countNumberOfCopiesWon(cardIndex + it) }
			totalNumberOfCardsWon.also { cardCopiesWon[cardIndex] = it }
		}
	}

	private data class ScratchCard(val winningNumbers: List<Int>, val myNumbers: List<Int>)

}