package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.util.MultiComparator

class Day7 : Day<Int, Int>(2023, 7, "Camel Cards") {

	private val handTypeComparator = Comparator<Hand> { h1, h2 ->
		h1.cardCounts.zip(h2.cardCounts).firstNotNullOfOrNull { (gs1, gs2) ->
			(gs1 - gs2).takeIf { it != 0 }
		} ?: 0
	}

	private val cardValueComparator = Comparator<Hand> { h1, h2 ->
		h1.cards.zip(h2.cards).firstNotNullOfOrNull { (c1, c2) ->
			(c1 - c2).takeIf { it != 0 }
		} ?: 0
	}

	override fun partOne(): Int {
		val cardValueMapping = mapOf(
			'T' to 10,
			'J' to 11,
			'Q' to 12,
			'K' to 13,
			'A' to 14,
		)
		val hands = inputLines.map { line ->
			val (cards, bid) = line.split(" ")

			// Map the card keys to integer values, then count the number of times each card occurs in the hand
			val cardValues = cards.map { cardKey -> cardValueMapping.getOrElse(cardKey) { cardKey.digitToInt() } }
			val cardCounts = cardValues.cardCounts()
			Hand(cardValues, cardCounts, bid.toInt())
		}

		val sortedHands = hands.sortedWith(MultiComparator(handTypeComparator, cardValueComparator))
		return sortedHands.mapIndexed { idx, hand -> hand.bid * (idx + 1) }.sum()
	}

	override fun partTwo(): Int {
		val cardValueMapping = mapOf(
			'J' to 1,
			'T' to 10,
			'Q' to 11,
			'K' to 12,
			'A' to 13,
		)

		val hands = inputLines.map { line ->
			val (cards, bid) = line.split(" ")

			// Map the card keys to integer values
			val cardValues = cards.map { cardKey -> cardValueMapping.getOrElse(cardKey) { cardKey.digitToInt() } }

			// Then find the optimal card counts by trying to replace each joker with another card value and finding the best card count
			val optimalCardCounts = (2..13).map { replacement ->
				val replacedCardValues = cardValues.map { cardValue -> cardValue.takeIf { it != 1 } ?: replacement }
				val replacedCardCounts = replacedCardValues.cardCounts()
				Hand(replacedCardValues, replacedCardCounts, 0)
			}.sortedWith(handTypeComparator).last().cardCounts
			Hand(cardValues, optimalCardCounts, bid.toInt())
		}

		val sortedHands = hands.sortedWith(MultiComparator(handTypeComparator, cardValueComparator))
		return sortedHands.mapIndexed { idx, hand -> hand.bid * (idx + 1) }.sum()

	}

	/**
	 * @return The number of occurrences for each card in descending orders. E.g. [1, 1, 2, 3, 4, 4, 4] returns [3, 2, 1, 1]
	 */
	private fun List<Int>.cardCounts() = this.groupBy { it }.map { (_, occurrences) -> occurrences.size }.sortedByDescending { it }

	/**
	 * Data class represeting a game hand.
	 * [cards] are the integer values of the individual cards
	 * [cardCounts] are the card counts in descending order (e.g. a full house will be [3, 2] and a two pair will be [2, 2, 1])
	 * [bid] will be the amount this hand was bid on
	 */
	data class Hand(val cards: List<Int>, val cardCounts: List<Int>, val bid: Int)

}