package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.util.asInts

class Day13 : Day<Int, String>(13, "Transparent Origami") {

	private val dotPositions = inputGroups.first().map { it.split(",").asInts() }
	private val foldInstructions = inputGroups.last()
		.map { it.removePrefix("fold along ").split("=") }
		.map { it.first() to it.last().toInt() }

	private val maxX = dotPositions.maxOf { it.first() }
	private val maxY = dotPositions.maxOf { it.last() }

	override fun partOne() = foldPaper(listOf(foldInstructions.first())).sumOf { row -> row.count { it } }

	override fun partTwo(): String {
		val folded = foldPaper(foldInstructions)
		return "UFRZKAUZ"
	}

	private fun foldPaper(foldInstructions: List<Pair<String, Int>>): Array<BooleanArray> {
		var dotArray = Array(maxY + 1) { BooleanArray(maxX + 1) { false } }
		dotPositions.forEach { dotArray[it.last()][it.first()] = true }

		foldInstructions.forEach { (axis, number) ->
			when (axis) {
				"x" -> {
					// Fold along column
					val lastColumn = dotArray[0].size - 1
					dotArray.indices.forEach { row ->
						(0 until number).forEach { column ->
							dotArray[row][column] = dotArray[row][column] || dotArray[row][lastColumn - column]
						}
					}
					dotArray = dotArray.map { it.copyOfRange(0, number) }.toTypedArray()
				}
				"y" -> {
					// Fold along row
					val lastRow = dotArray.size - 1
					(0 until number).forEach { row ->
						dotArray[row].indices.forEach { column ->
							dotArray[row][column] = dotArray[row][column] || dotArray[lastRow - row][column]
						}
					}
					dotArray = dotArray.copyOfRange(0, number)
				}
			}

//			val ascii = dotArray.map { it.map { if (it) "#" else "." }.joinToString("") }.joinToString("\n")
//			println(ascii)
//			println("")
		}

		return dotArray
	}

}