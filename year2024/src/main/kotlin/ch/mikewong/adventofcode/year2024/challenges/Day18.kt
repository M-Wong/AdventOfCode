package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.algorithms.dijkstra
import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Point
import ch.mikewong.adventofcode.common.models.Size

class Day18 : Day<Int, String>(2024, 18, "RAM Run") {

	private val bytes by lazy {
		inputLines.map { line ->
			val (x, y) = line.split(",")
			Point(y.toLong(), x.toLong())
		}
	}

	private val bounds by lazy {
		if (isControlSet) Size(7, 7).toArea() else Size(71, 71).toArea()
	}

	// 318
	override fun partOne(): Int {
		val start = bounds.topLeft
		val end = bounds.bottomRight

		// Take the first 12 (example input) or 1024 (main input) bytes to use as the map
		val simulateBytes = if (isControlSet) bytes.take(12) else bytes.take(1024)
		val result = dijkstra(
			startingNode = start,
			isTargetNode = { it == end },
			neighbours = { current ->
				current.adjacent { it in bounds && it !in simulateBytes }
			},
		)

		return result.totalCost
	}

	// 56,29
	override fun partTwo(): String {
		val start = bounds.topLeft
		val end = bounds.bottomRight

		// Iterate from a fully blocked map backwards until we find the first byte that has a shortest path.
		// Beware that the first blocking byte is actually the one before that (so no index - 1 when getting the byte)
		val blockingByteIndex = (bytes.size downTo 1).first { count ->
			val simulateBytes = bytes.take(count)

			try {
				dijkstra(
					startingNode = start,
					isTargetNode = { it == end },
					neighbours = { current ->
						current.adjacent { it in bounds && it !in simulateBytes }
					},
				)
				true
			} catch (e: IllegalStateException) {
				false
			}
		}

		val blockingByte = bytes[blockingByteIndex]
		return "${blockingByte.y},${blockingByte.x}"
	}

}