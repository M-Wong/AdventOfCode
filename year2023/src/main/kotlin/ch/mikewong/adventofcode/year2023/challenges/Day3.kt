package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.product
import ch.mikewong.adventofcode.common.extensions.toGridNotNull
import ch.mikewong.adventofcode.common.models.Point

class Day3 : Day<Int, Int>(2023, 3, "Gear Ratios") {

	private val symbolPositions by lazy { readSymbols() }

	override fun partOne(): Int {
		return findNumbersAdjacentToSymbols { true }.values.flatten().sum()
	}

	override fun partTwo(): Int {
		return findNumbersAdjacentToSymbols { it == '*' }.values.filter { it.size == 2 }.sumOf { it.product() }
	}

	private fun readSymbols() = inputLines.toGridNotNull { char -> char.takeIf { it != '.' && !it.isDigit() } }

	private fun findNumbersAdjacentToSymbols(symbolFilter: (Char) -> Boolean): Map<Point, List<Int>> {
		val numbersNextToSymbol = mutableMapOf<Point, MutableList<Int>>()
		inputLines.forEachIndexed { row, line ->
			var currentNumber = ""
			var currentSymbolPosition: Point? = null

			// Iterate each character in the input
			line.forEachIndexed { column, char ->
				if (char.isDigit()) {
					// If the character is a digit, add it to the current number and check if it is adjacent to a symbol (using the symbolFilter lambda)
					currentNumber += char
					val point = Point(row, column)

					// This assumes that there is always only one symbol adjacent to any number
					val surroundingSymbol = point.surrounding { symbolPositions[it]?.let(symbolFilter) ?: false }.singleOrNull()
					currentSymbolPosition = surroundingSymbol ?: currentSymbolPosition
				} else {
					// If the character is not a digit, if we just finished a number, add it to the map
					currentSymbolPosition?.let {
						numbersNextToSymbol.getOrPut(it) { mutableListOf() }.add(currentNumber.toInt())
					}
					currentNumber = ""
					currentSymbolPosition = null
				}
			}

			// Additional check in case a number is at the end of a line
			currentSymbolPosition?.let {
				numbersNextToSymbol.getOrPut(it) { mutableListOf() }.add(currentNumber.toInt())
			}
		}

		return numbersNextToSymbol
	}

}