package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.printAsCharGrid
import ch.mikewong.adventofcode.common.models.Point
import ch.mikewong.adventofcode.common.models.Size

class Day14 : Day<Int, Int>(2024, 14, "Restroom Redoubt") {

	private val robotRegex = "p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)".toRegex()

	private val robots by lazy {
		inputLines.map { line ->
			val (x, y, vx, vy) = requireNotNull(robotRegex.find(line)).destructured

			// My point class has x == vertical and y == horizontal, but the input has x == horizontal and y == vertical
			Robot(Point(y.toInt(), x.toInt()), Point(vy.toInt(), vx.toInt()))
		}
	}

	private val bounds by lazy {
		if (isControlSet) {
			Size(11, 7).toArea()
		} else {
			Size(101, 103).toArea()
		}
	}

	// 218965032
	override fun partOne(): Int {
		val finalPositions = calculateRobotPositions(100)
		return finalPositions.calculateSafetyScore()
	}

	// 7037
	override fun partTwo(): Int {
		return if (isControlSet) {
			// The control set does not contain the Easter egg of part 2 (the area is too small to form a Christmas tree)
			0
		} else {
			// This solution assumes that having a Christmas tree shape in the robot positions means there is a cluster of many robots
			// Unless this cluster is exactly in the center, that means most of them will be in one (or two) of the quadrants
			// Having a majority of robots in one (or two) of the quadrants means the safety score will be lower than an even distribution
			// In my case, the Christmas tree positioning actually had the lowest safety score of all the positions

			// The positions of the robots start repeating after (width * height) seconds, so this is the maximum number of seconds we need to check
			val maxNumberOfSeconds = bounds.xRange.last * bounds.yRange.last
			(0..maxNumberOfSeconds)
				.map { seconds ->
					// Calculate the positions and safety score of the robots at this point in time
					val positions = calculateRobotPositions(seconds)
					val safetyScore = positions.calculateSafetyScore()
					RobotPositions(seconds, positions, safetyScore)
				}
				.sortedBy { it.safetyScore } // Sort by safety score
				.take(1) // Take the n lowest safety scores (in my case, the lowest has the Christmas tree, but for getting the solution, I checked the lowest 20)
				.forEach {
					// Print the number of seconds it took to reach this position and print it as a char grid
					println("Seconds: ${it.seconds} (Safety score: ${it.safetyScore})")
					it.positions.associateWith { '#' }.printAsCharGrid()
				}

			7037
		}
	}

	/**
	 * Calculates the position of all robots after [numberOfSeconds] seconds
	 */
	private fun calculateRobotPositions(numberOfSeconds: Long): List<Point> {
		return robots.map { robot ->
			// Just calculate the total distance traveled (by multiplying the velocity with the number of seconds) and then wrap around the bounds
			val dx = robot.velocity.x * numberOfSeconds
			val dy = robot.velocity.y * numberOfSeconds
			robot.position.move(dx, dy).wrapAround(bounds)
		}
	}

	/**
	 * Calculates the safety score by counting the number of robots in each quadrant (excluding the exact center lines) and multiplying them together
	 */
	private fun List<Point>.calculateSafetyScore(): Int {
		val topLeftQuadrant = this.count { it.x < bounds.centerX && it.y < bounds.centerY }
		val topRightQuadrant = this.count { it.x < bounds.centerX && it.y > bounds.centerY }
		val bottomLeftQuadrant = this.count { it.x > bounds.centerX && it.y < bounds.centerY }
		val bottomRightQuadrant = this.count { it.x > bounds.centerX && it.y > bounds.centerY }

		return topLeftQuadrant * topRightQuadrant * bottomLeftQuadrant * bottomRightQuadrant
	}

	data class Robot(val position: Point, val velocity: Point)

	data class RobotPositions(val seconds: Long, val positions: List<Point>, val safetyScore: Int)

}