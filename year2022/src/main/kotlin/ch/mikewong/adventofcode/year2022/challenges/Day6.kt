package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allUnique

class Day6 : Day<Int, Int>(2022, 6, "Tuning Trouble") {

	override fun partOne() = findStartOfMessageMarker(4)

	override fun partTwo() = findStartOfMessageMarker(14)

	private fun findStartOfMessageMarker(packetSize: Int) = input.windowed(packetSize).indexOfFirst { it.allUnique() } + packetSize

}