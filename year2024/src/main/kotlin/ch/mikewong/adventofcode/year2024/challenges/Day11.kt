package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.longCount
import ch.mikewong.adventofcode.common.extensions.splitIntoTwo

class Day11 : Day<Long, Long>(2024, 11, "Plutonian Pebbles") {

	private val originalStones by lazy { input.split(" ").longCount { it.toLong() } }

	// 183484
	override fun partOne(): Long {
		return countStones(numberOfBlinks = 25)
	}

	// 218817038947400
	override fun partTwo(): Long {
		return countStones(numberOfBlinks = 75)
	}

	private fun countStones(numberOfBlinks: Int): Long {
		var stones = originalStones.toMap()
		repeat(numberOfBlinks) {
			stones = splitStones(stones)
		}
		return stones.values.sum()
	}

	private fun splitStones(stones: Map<Long, Long>): Map<Long, Long> {
		val newStones = mutableMapOf<Long, Long>()
		stones.forEach { (stone, count) ->
			splitStone(stone).forEach { newStone ->
				newStones[newStone] = newStones.getOrDefault(newStone, 0) + count
			}
		}
		return newStones
	}

	private fun splitStone(stone: Long): List<Long> {
		val stoneString = stone.toString()
		return when {
			stone == 0L -> listOf(1L)
			stoneString.length % 2 == 0 -> stoneString.splitIntoTwo().toList().map { it.toLong() }
			else -> listOf(stone * 2024L)
		}
	}

}