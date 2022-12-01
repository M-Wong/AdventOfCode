package ch.mikewong.adventofcode.year2021.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Point
import ch.mikewong.adventofcode.common.util.toCharGrid

class Day20 : Day<Int, Int>(2021, 20, "Trench Map") {

	private val imageEnhancementAlgorithm by lazy { inputLines.first() }

	override fun partOne() = countLightPixels(2)

	override fun partTwo() = countLightPixels(50)

	private fun countLightPixels(times: Int): Int {
		var inputImage = inputGroups.last().toCharGrid()
		var minSize = 0
		var maxSize = inputImage.keys.maxOf { it.x }

		repeat(times) { iteration ->
			val outputImage = mutableMapOf<Point, Char>()

			// Iterate one more row/column on each side of the grid
			(minSize - 1 .. maxSize + 1).forEach { x ->
				(minSize - 1 .. maxSize + 1).forEach { y ->
					val point = Point(x, y)

					// Get the surrounding points including itself
					val surroundingPoints = point.surrounding(includeItself = true)
					val binary = surroundingPoints.map {
						// Map the surrounding points to their binary value
						if (inputImage.containsKey(it)) {
							if (inputImage[it] == '#') 1 else 0
						} else {
							// If there is no value yet in the grid for the point, alternate between 1 and 0 based on the first character
							// in the algorithm to accommodate for the infinite grid size
							if (iteration % 2 == 1 && imageEnhancementAlgorithm[0] == '#') 1 else 0
						}
					}.joinToString("")
					val index = binary.toInt(2)

					val newCharacter = imageEnhancementAlgorithm[index]
					outputImage[point] = newCharacter
				}
			}

			minSize--
			maxSize++

			inputImage = outputImage
		}

		return inputImage.values.count { it == '#' }
	}

}