package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allDigits
import ch.mikewong.adventofcode.common.extensions.allLongs
import ch.mikewong.adventofcode.common.extensions.product

class Day6 : Day<Int, Int>(2023, 6, "Wait For It") {

	override fun partOne(): Int {
		val times = inputLines.first().allLongs()
		val distances = inputLines.last().allLongs()
		val races = times.zip(distances).map { (t, d) -> Race(t, d) }

		return races.map { it.countWaysToBeatRecord() }.product()
	}

	override fun partTwo(): Int {
		val time = inputLines.first().allDigits().toLong()
		val distance = inputLines.last().allDigits().toLong()
		val race = Race(time, distance)
		return race.countWaysToBeatRecord()
	}

	private data class Race(val time: Long, val distance: Long) {
		fun countWaysToBeatRecord(): Int {
			val firstTimeBeatingRecord = (0..time).indexOfFirst { holdingTime ->
				val speed = holdingTime
				val remainingTime = time - holdingTime
				val distanceTravelled = remainingTime * speed
				distanceTravelled > distance
			}

			// Semi-mathematically formula. Since the travelled distances are symmetrical, knowing when the first time beating the
			// record occurs, we can basically just subtract that number twice from the number of possible holding times.
			// I know there's some kind of formula that calculates this without having to iterate until the first index, but this works just fine for me
			return (time + 1).toInt() - (2 * firstTimeBeatingRecord)
		}
	}

}