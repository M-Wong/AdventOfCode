package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day15 : Day<Int, Int>(2023, 15, "Lens Library") {

	private val initializationSequence = inputLines.joinToString(",").split(",")

	override fun partOne(): Int {
		return initializationSequence.sumOf { it.hashValue() }
	}

	override fun partTwo(): Int {
		val boxes = Array(256) { linkedMapOf<String, Int>() }

		initializationSequence.forEach { operation ->
			if (operation.endsWith("-")) {
				// If the operation ends with a '-', remove the lens label from the box
				val label = operation.dropLast(1)
				boxes[label.hashValue()].remove(label)
			} else {
				// Otherwise, add the lens to the box, replacing any existing one with the same label
				val (label, focalLength) = operation.split("=")
				boxes[label.hashValue()][label] = focalLength.toInt()
			}
		}

		// Sum up the lens focusing powers by multiplying the box number with the lens number and its focal length
		return boxes.mapIndexed { boxIdx, box ->
			box.entries.mapIndexed { lensIdx, (_, focalLength) ->
				(boxIdx + 1) * (lensIdx + 1) * focalLength
			}.sum()
		}.sum()
	}

	private fun String.hashValue() = this.fold(0) { acc, c ->
		((acc + c.code) * 17).rem(256)
	}

}