package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allLongs
import ch.mikewong.adventofcode.common.extensions.overlapsWith
import kotlin.math.max
import kotlin.math.min

class Day5 : Day<Long, Long>(2023, 5, "If You Give A Seed A Fertilizer") {

	private val seeds = inputLines.first().allLongs()
	private val conversions = inputGroups.drop(1).map { group ->
		group.drop(1).map { line ->
			val (dst, src, length) = line.allLongs()
			RangeMap(dst, src, length)
		}
	}

	override fun partOne(): Long {
		return seeds.minOf { seed ->
			getDstValue(seed, 0)
		}
	}

	override fun partTwo(): Long {
		val seedRanges = seeds.chunked(2).map { (start, length) -> start until (start + length) }

		// Map all seed ranges to a list of location ranges, after all the conversions were applied
		val seedToLocationRanges = seedRanges.map { seedRange ->

			// Accumulate the ranges that this seed range maps to when applying all the conversions
			conversions.fold(listOf(seedRange)) { accumulatedRanges, conversionMappings ->
				// List of ranges that were not yet mapped and ranges that are mapped using the conversion mappings
				val unmappedRanges = accumulatedRanges.toMutableList()
				val mappedRanges = mutableListOf<LongRange>()

				while (unmappedRanges.isNotEmpty()) {
					// The next range to be mapped
					val range = unmappedRanges.removeFirst()

					// Check if there exists a conversion mapping whose source range overlaps with the range to be mapped
					val mapping = conversionMappings.firstOrNull {
						it.srcRange.overlapsWith(range)
					}

					if (mapping == null) {
						// If there exists no such conversion, add this range directly to the mapped ranges, as those numbers are mapped one to one
						mappedRanges.add(range)
						continue
					}

					// The end of the source (input) range of this mapping
					val mappingSrcEnd = mapping.srcRange.last + 1

					// Calculate the overlap between the conversion mapping range and the range to be mapped
					val srcOverlapStart = max(range.first, mapping.srcStart)
					val dstOverlapStart = mapping.dstStart + (srcOverlapStart - mapping.srcStart)
					val dstOverlapLength = min(range.last, mappingSrcEnd) - srcOverlapStart
					mappedRanges.add(dstOverlapStart until (dstOverlapStart + dstOverlapLength))

					if (range.first < mapping.srcStart) {
						// If the range starts before the conversion mapping range, add the non-overlapping range to the unmapped ranges
						unmappedRanges.add(range.first until (range.first + mapping.srcStart - range.first))
					}

					if (range.last > mappingSrcEnd) {
						// If the range ends after the conversion mapping range, add the non-overlapping range to the unmapped ranges
						unmappedRanges.add(mappingSrcEnd until (mappingSrcEnd + range.last - mappingSrcEnd))
					}
				}

				mappedRanges
			}
		}
		return seedToLocationRanges.flatten().minOf { it.first }

		// Initial "brute-force" bottom-up algorithm (~8000x slower)
//		val destinationRangeMaps = conversionMaps.last().sortedBy { it.destinationRangeStart }
//
//		val rangeBeforeDestinationRangeMaps = (0..(destinationRangeMaps.first().destinationRangeStart))
//
//		return rangeBeforeDestinationRangeMaps.firstOrNull { output ->
//			val seed = getSrcValue(output, conversionMaps.lastIndex - 1)
//			seedRanges.any { it.contains(seed) }
//		} ?: destinationRangeMaps.firstNotNullOfOrNull { rangeMap ->
//			rangeMap.destinationRange.firstOrNull { output ->
//				val input = rangeMap.convertBack(output) ?: output
//				val seed = getSrcValue(input, conversionMaps.lastIndex - 1)
//				seedRanges.any { it.contains(seed) }
//			}
//		} ?: throw RuntimeException("No destination found")
	}

	private fun getDstValue(input: Long, rangeMapIndex: Int): Long {
		val rangeMaps = conversions.getOrNull(rangeMapIndex) ?: return input
		val output = rangeMaps.firstNotNullOfOrNull { it.convert(input) } ?: input
		return getDstValue(output, rangeMapIndex + 1)
	}

	private fun getSrcValue(output: Long, rangeMapIndex: Int): Long {
		if (rangeMapIndex < 0) return output

		val rangeMap = conversions[rangeMapIndex]
		val input = rangeMap.firstNotNullOfOrNull { it.convertBack(output) } ?: output
		return getSrcValue(input, rangeMapIndex - 1)
	}

	private data class RangeMap(val dstStart: Long, val srcStart: Long, val rangeLength: Long) {
		val srcRange = srcStart until (srcStart + rangeLength)
		val dstRange = dstStart until (dstStart + rangeLength)

		/**
		 * Converts an [input] number to an output number or null if it doesn't match the [srcRange]
		 */
		fun convert(input: Long) = if (input in srcRange) dstStart + (input - srcStart) else null

		/**
		 * Converts an [output] number back to an input number or null if it doesn't match the [dstRange]
		 */
		fun convertBack(output: Long) = if (output in dstRange) srcStart + (output - dstStart) else null
	}
}