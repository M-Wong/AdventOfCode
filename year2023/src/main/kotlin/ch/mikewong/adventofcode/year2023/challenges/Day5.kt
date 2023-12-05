package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allLongs

class Day5 : Day<Long, Long>(2023, 5, "If You Give A Seed A Fertilizer") {

	private val seeds = inputLines.first().allLongs()
	private val conversionMaps = inputGroups.drop(1).map { group ->
		group.drop(1).map { line ->
			val (dst, src, length) = line.split(" ").map { it.toLong() }
			RangeMap(dst, src, length)
		}
	}

	override fun partOne(): Long {
		return seeds.minOf { seed ->
			getDestinationValue(seed, 0)
		}
	}

	override fun partTwo(): Long {
		val seedRanges = seeds.chunked(2).map { (start, length) -> start until (start + length) }

		val destinationRangeMaps = conversionMaps.last().sortedBy { it.destinationRangeStart }

		val rangeBeforeDestinationRangeMaps = (0..(destinationRangeMaps.first().destinationRangeStart))

		return rangeBeforeDestinationRangeMaps.firstOrNull { output ->
			val seed = getSourceValue(output, conversionMaps.lastIndex - 1)
			seedRanges.any { it.contains(seed) }
		} ?: destinationRangeMaps.firstNotNullOfOrNull { rangeMap ->
			rangeMap.destinationRange.firstOrNull { output ->
				val input = rangeMap.convertBack(output) ?: output
				val seed = getSourceValue(input, conversionMaps.lastIndex - 1)
				seedRanges.any { it.contains(seed) }
			}
		} ?: throw RuntimeException("No destination found")
	}

	private fun getDestinationValue(input: Long, rangeMapIndex: Int): Long {
		val rangeMaps = conversionMaps.getOrNull(rangeMapIndex) ?: return input
		val output = rangeMaps.firstNotNullOfOrNull { it.convert(input) } ?: input
		return getDestinationValue(output, rangeMapIndex + 1)
	}

	private fun getSourceValue(output: Long, rangeMapIndex: Int): Long {
		if (rangeMapIndex < 0) return output

		val rangeMap = conversionMaps[rangeMapIndex]
		val input = rangeMap.firstNotNullOfOrNull { it.convertBack(output) } ?: output
		return getSourceValue(input, rangeMapIndex - 1)
	}

	private data class RangeMap(val destinationRangeStart: Long, val sourceRangeStart: Long, val rangeLength: Long) {
		val destinationRange get() = destinationRangeStart..(destinationRangeStart + rangeLength)

		fun convert(input: Long): Long? {
			return if (input >= sourceRangeStart && input < (sourceRangeStart + rangeLength)) {
				destinationRangeStart + (input - sourceRangeStart)
			} else {
				null
			}
		}

		fun convertBack(output: Long): Long? {
			return if (output >= destinationRangeStart && output < (destinationRangeStart + rangeLength)) {
				sourceRangeStart + (output - destinationRangeStart)
			} else {
				null
			}

		}
	}
}