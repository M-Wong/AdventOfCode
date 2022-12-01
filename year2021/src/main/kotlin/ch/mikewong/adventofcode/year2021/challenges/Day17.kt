package ch.mikewong.adventofcode.year2021.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Point
import ch.mikewong.adventofcode.common.util.asInts
import ch.mikewong.adventofcode.common.util.substringBetween
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sqrt

class Day17 : Day<Int, Int>(2021, 17, "Trick Shot") {

	private val startPoint = Point(0, 0)
	private val targetArea = inputLines.first().run {
		val xRange = this.substringBetween("x=", ",").split("..").asInts()
		val yRange = this.substringAfter("y=").split("..").asInts()
		xRange.first()..xRange.last() to yRange.first()..yRange.last()
	}
	private val targetTopLeft = Point(targetArea.first.minOrNull() ?: 0, targetArea.second.maxOrNull() ?: 0)
	private val targetBottomRight = Point(targetArea.first.maxOrNull() ?: 0, targetArea.second.minOrNull() ?: 0)

	override fun partOne(): Int {
		val hittingVelocities = findHittingVelocities()
		return hittingVelocities.values.maxOrNull() ?: 0
	}

	override fun partTwo(): Int {
		val hittingVelocities = findHittingVelocities()
		return hittingVelocities.size
	}

	private fun findHittingVelocities(): Map<Velocity, Int> {
		val results = mutableMapOf<Velocity, Int>()

		val xDistanceRange = targetArea.first
		val requiredVelocityRangeX = (floor(sqrt(xDistanceRange.first * 2.0)).toInt()..ceil(sqrt(xDistanceRange.last * 2.0)).toInt())
			.filter { (it * (it + 1)) / 2 in xDistanceRange }

		(requiredVelocityRangeX.first()..(requiredVelocityRangeX.last() + 1000)).forEach { xVelocity ->
			val startingVelocityY = targetBottomRight.y

			(startingVelocityY..1000).forEach { yVelocity ->
				val initialVelocity = Velocity(xVelocity, yVelocity)
				var currentMaxHeight = 0

				var currentLocation = startPoint
				var currentVelocity = initialVelocity
				while (currentLocation.x <= targetBottomRight.x && currentLocation.y >= targetBottomRight.y) {
					if (currentLocation.isWithin(targetTopLeft, targetBottomRight)) {
						results[initialVelocity] = currentMaxHeight
						break
					} else {
						currentLocation = currentLocation.plus(currentVelocity)
						currentVelocity = currentVelocity.decrease()
						currentMaxHeight = max(currentMaxHeight, currentLocation.y)
					}
				}
			}
		}

		return results
	}

}

typealias Velocity = Point

private fun Velocity.decrease(dragAmount: Int = 1): Velocity {
	val newX = when {
		x > 0 -> x - dragAmount
		x < 0 -> x + dragAmount
		else -> 0
	}

	val newY = y - 1

	return Velocity(newX, newY)
}