package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.asLongs

class Day20 : Day<Long, Long>(2022, 20, "Grove Positioning System") {

	private val originalCoordinates by lazy { readInput() }

	override fun partOne(): Long {
		val coordinates = mixCoordinates(originalCoordinates, times = 1)
		return getGroveCoordinates(coordinates)
	}

	override fun partTwo(): Long {
		val decryptionKey = 811589153

		val newOriginalCoordinates = originalCoordinates.map { it.copy(value = it.value * decryptionKey) }
		val coordinates = mixCoordinates(newOriginalCoordinates, times = 10)
		return getGroveCoordinates(coordinates)
	}

	private fun getGroveCoordinates(coordinates: List<Coordinate>): Long {
		val indexOfZero = coordinates.indexOfFirst { it.value == 0L }
		val indices = listOf(1000L, 2000L, 3000L)
		return indices.sumOf { idx ->
			val actualIndex = coordinates.wrapAroundIndex(indexOfZero + idx)
			coordinates[actualIndex].value
		}
	}

	private fun mixCoordinates(
		originalCoordinates: List<Coordinate>,
		times: Int,
	): List<Coordinate> {
		val coordinates = originalCoordinates.toMutableList()
		repeat(times) {
			originalCoordinates.forEach { ogCoord ->
				if (ogCoord.value != 0L) {
					// Find the index of the original coordinate
					val indexOfCoord = coordinates.indexOfFirst { it.originalIndex == ogCoord.originalIndex }

					// Remove the coordinate from the list, calculate the new index and insert it at that position
					val coordinate = coordinates.removeAt(indexOfCoord)
					val newIndex = coordinates.wrapAroundIndex(indexOfCoord + coordinate.value)
					coordinates.add(newIndex, coordinate)
				}
			}
		}
		return coordinates
	}

	private fun readInput(): List<Coordinate> {
		return inputLines.asLongs().mapIndexed { index, value -> Coordinate(index, value) }
	}

	private fun <T> List<T>.wrapAroundIndex(index: Long): Int {
		if (isEmpty()) throw NoSuchElementException()
		return when (index) {
			0L -> 0
			in indices -> index.toInt()
			else -> index.mod(size) // Note that .mod() is different to %: https://medium.com/@alexvanyo/kotlin-functions-rem-and-mod-fa2e865304c3
		}
	}

	private data class Coordinate(val originalIndex: Int, val value: Long)

}