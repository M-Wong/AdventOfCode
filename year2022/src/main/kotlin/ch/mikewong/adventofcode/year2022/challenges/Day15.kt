package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.overlapsWith
import ch.mikewong.adventofcode.common.extensions.substringBetween
import ch.mikewong.adventofcode.common.models.Point
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day15 : Day<Int, Long>(2022, 15, "Beacon Exclusion Zone") {

	private val sensors by lazy { readInput() }
	private val isRunningOnTestSet by lazy { sensors.maxOf { it.position.y } < 100 } // Test set has different parameters than input

	override fun partOne(): Int {
		val rowToCheck = if (isRunningOnTestSet) 10 else 2000000
		val uniqueRanges = getUniqueRangesOnRow(rowToCheck)
		return uniqueRanges.sumOf { abs(it.first) + abs(it.last) }
	}

	override fun partTwo(): Long {
		val min = 0
		val max = if (isRunningOnTestSet) 20 else 4000000

		var gap: Point? = null

		val order = if (isRunningOnTestSet) min..max else max downTo min

		for (y in order) {
			val uniqueRanges = getUniqueRangesOnRow(y)
			if (uniqueRanges.size > 1) {
				val x = uniqueRanges.minOf { it.last } + 1
				gap = Point(x, y)
				break
			}
		}

		val xFactor = 4000000L
		return gap?.let { it.x * xFactor + it.y } ?: 0
	}

	private fun readInput(): List<Sensor> {
		return inputLines.map { line ->
			val parts = line.split(":")
			Sensor(
				position = Point(
					parts.first().substringBetween("x=", ",").toInt(),
					parts.first().substringAfter("y=").toInt()
				),
				closestBeacon = Point(
					parts.last().substringBetween("x=", ",").toInt(),
					parts.last().substringAfter("y=").toInt()
				)
			)
		}
	}

	/**
	 * Return the coverage of a single [sensor] for a given [row], which is 2 * (radius - rowDistanceToSensor) + 1
	 */
	private fun getCoverageOnRow(row: Int, sensor: Sensor): Int {
		val rowDistanceToSensor = abs(row - sensor.position.y)
		return (2 * (sensor.radius - rowDistanceToSensor) + 1).coerceAtLeast(0)
	}

	/**
	 * Return all ranges covered by a sensor on [row]. These might overlap
	 */
	private fun getRangesOnRow(row: Int): List<IntRange> {
		val sensorCoverage = sensors.mapNotNull { sensor ->
			val coverage = getCoverageOnRow(row, sensor)
			coverage.takeIf { it > 0 }?.let { sensor.position.x to it }
		}

		return sensorCoverage.map { (x, coverage) ->
			val halfCoverage = coverage / 2
			(x - halfCoverage)..(x + halfCoverage)
		}
	}

	/**
	 * Get unique ranges covered by all sensors on [row]. These should not overlap anymore
	 */
	private fun getUniqueRangesOnRow(row: Int): List<IntRange> {
		val ranges = getRangesOnRow(row).toMutableList()
		val uniqueRanges = mutableListOf<IntRange>()

		// Use ranges as a stack and keep processing while it is not empty
		while (ranges.isNotEmpty()) {
			// Use the first range as a reference
			val range = ranges.removeFirst()

			// Find all remaining ranges that overlap with the reference range
			val overlappingRanges = ranges.filter { it.overlapsWith(range) }
			ranges.removeAll(overlappingRanges)

			if (overlappingRanges.isEmpty()) {
				// If no ranges overlap with the reference, it is unique
				uniqueRanges.add(range)
			} else {
				// Fold the overlapping ranges onto the reference range by merging them together and re-add the resulting range onto the stack
				val newRange = overlappingRanges.fold(range) { a, b -> a.merge(b) }
				ranges.add(newRange)
			}
		}

		return uniqueRanges
	}

	/**
	 * Merge two overlapping ranges
	 */
	private fun IntRange.merge(other: IntRange): IntRange {
		return min(this.first, other.first) .. max(this.last, other.last)
	}

	private data class Sensor(val position: Point, val closestBeacon: Point) {
		val radius: Int = position.manhattanDistanceTo(closestBeacon)

		override fun toString(): String {
			return "$position -> $closestBeacon: $radius"
		}
	}

}