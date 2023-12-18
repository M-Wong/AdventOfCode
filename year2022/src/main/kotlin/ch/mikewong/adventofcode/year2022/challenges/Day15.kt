package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.merge
import ch.mikewong.adventofcode.common.extensions.overlapsWith
import ch.mikewong.adventofcode.common.extensions.substringBetween
import ch.mikewong.adventofcode.common.models.Point
import kotlin.math.abs

class Day15 : Day<Long, Long>(2022, 15, "Beacon Exclusion Zone") {

	private val sensors by lazy { readInput() }

	override fun partOne(): Long {
		val rowToCheck = if (isControlSet) 10L else 2000000L
		val uniqueRanges = getUniqueRangesOnRow(rowToCheck)
		return uniqueRanges.sumOf { abs(it.first) + abs(it.last) }
	}

	override fun partTwo(): Long {
		val min = 0L
		val max = if (isControlSet) 20L else 4000000L

		var gap: Point? = null

		val order = if (isControlSet) min..max else max downTo min

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
	private fun getCoverageOnRow(row: Long, sensor: Sensor): Long {
		val rowDistanceToSensor = abs(row - sensor.position.y)
		return (2 * (sensor.radius - rowDistanceToSensor) + 1).coerceAtLeast(0)
	}

	/**
	 * Return all ranges covered by a sensor on [row]. These might overlap
	 */
	private fun getRangesOnRow(row: Long): List<LongRange> {
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
	private fun getUniqueRangesOnRow(row: Long): List<LongRange> {
		val ranges = getRangesOnRow(row).toMutableList()
		val uniqueRanges = mutableListOf<LongRange>()

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

	private data class Sensor(val position: Point, val closestBeacon: Point) {
		val radius: Int = position.manhattanDistanceTo(closestBeacon).toInt()

		override fun toString(): String {
			return "$position -> $closestBeacon: $radius"
		}
	}

}