package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.differentCharacters
import ch.mikewong.adventofcode.common.extensions.transpose

class Day13 : Day<Int, Int>(2023, 13, "Point of Incidence") {

	// Each input group represents a pattern. To find vertical reflection lines, transpose each pattern once, so the same horizontal algorithm can be reused
	private val patterns = inputGroups
	private val transposedPatterns = patterns.map { it.transpose() }

	override fun partOne(): Int {
		return inputGroups.indices.sumOf { idx ->
			// Try to find a horizontal reflection line first in the normal pattern, then in the transposed pattern
			findHorizontalReflectionLine(patterns[idx])?.let { it * 100 }
				?: findHorizontalReflectionLine(transposedPatterns[idx])
				?: throw IllegalStateException("Could not find reflection line for pattern at index $idx")
		}
	}

	override fun partTwo(): Int {
		return inputGroups.indices.sumOf { idx ->
			// Try to find a smudged horizontal reflection line first in the normal pattern, then in the transposed pattern
			findSmudgedHorizontalReflectionLine(patterns[idx])?.let { it * 100 }
				?: findSmudgedHorizontalReflectionLine(transposedPatterns[idx])
				?: throw IllegalStateException("Could not find reflection line for pattern at index $idx")
		}
	}

	private fun findHorizontalReflectionLine(pattern: List<String>): Int? {
		pattern.indices.forEach { idx ->
			// For each row index, get the lines before (reversed to match the following lines) and after
			val before = pattern.subList(0, idx).reversed()
			val after = pattern.subList(idx, pattern.size)

			// Zip them together (to discard excessive lines) and check if all of them are the same
			val zipped = before.zip(after)
			if (zipped.isNotEmpty() && zipped.all { (b, a) -> a == b }) return idx
		}

		return null
	}

	private fun findSmudgedHorizontalReflectionLine(pattern: List<String>): Int? {
		pattern.indices.forEach { idx ->
			// For each row index, get the lines before (reversed to match the following lines) and after
			val before = pattern.subList(0, idx).reversed()
			val after = pattern.subList(idx, pattern.size)

			// Zip them together (to discard excessive lines)
			val zipped = before.zip(after)

			// Count the character differences between each pair of zipped lines
			val totalDifferentCharacters = zipped.sumOf { (b, a) ->
				if (a == b) 0 else a.differentCharacters(b)
			}

			// If the number of different characters across all line pairs is exactly one, this is a reflection line
			if (totalDifferentCharacters == 1) return idx
		}

		return null
	}

}