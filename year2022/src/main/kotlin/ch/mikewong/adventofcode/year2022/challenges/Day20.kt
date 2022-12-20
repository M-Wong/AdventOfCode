package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.asLongs
import ch.mikewong.adventofcode.common.extensions.wrapAroundIndex
import java.util.*

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
		val indices = listOf(1000, 2000, 3000)
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
				// Find the index of the original coordinate
				val indexOfCoord = coordinates.indexOfFirst { it.originalIndex == ogCoord.originalIndex }

				// Remove the coordinate from the list, then rotate the list by the coordinate value, insert the coordinate again and rotate the same amount back
				val item = coordinates.removeAt(indexOfCoord)
				val rotation = item.value % coordinates.size
				Collections.rotate(coordinates, rotation.toInt() * -1)
				coordinates.add(indexOfCoord, item)
				Collections.rotate(coordinates, rotation.toInt())
			}
		}
		return coordinates
	}

	private fun readInput(): List<Coordinate> {
		return inputLines.asLongs().mapIndexed { index, value -> Coordinate(index, value) }
	}

	private data class Coordinate(val originalIndex: Int, val value: Long)

}