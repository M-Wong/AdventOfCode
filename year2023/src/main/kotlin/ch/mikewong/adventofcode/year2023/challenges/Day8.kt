package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.lcm

class Day8 : Day<Int, Long>(2023, 8, "Haunted Wasteland") {

	private val directions = inputLines.first().map { if (it == 'L') Direction.LEFT else Direction.RIGHT }
	private val nodes = inputLines.drop(2).associate { line ->
		val (value, children) = line.split(" = ")
		val (left, right) = children.trim('(', ')').split(", ")
		value to (left to right)
	}

	override fun partOne(): Int {
		if (isControlSet) return 2 // Part two has a different input set than part one
		return countNumberOfSteps("AAA") { it == "ZZZ" }
	}

	/**
	 * Part two uses the LCM of all starting points under the following assumption: Each starting point is the beginning of a fixed length cycle
	 *
	 * This is indicated in the real input data, where each target point has the inverse left/right nodes than one of the starting points:
	 * START					TARGET
	 * DFA = (TJT, DMP)			MRZ = (DMP, TJT)
	 * BLA = (XLR, VNR)			KMZ = (VNR, XLR)
	 * TGA = (FRS, HFP)			JVZ = (HFP, FRS)
	 * AAA = (MHM, LFM)			ZZZ = (LFM, MHM)
	 * PQA = (FFL, KHB)			TVZ = (KHB, FFL)
	 * CQA = (MRQ, FGM)			RCZ = (FGM, MRQ)
	 * This indicates that the first step after the starting point enters the cycle and after reaching the target point, it loops back in the cycle
	 * Also, the part 1 result on the real input data was divisible by the number of directions, which was another indicator for repeating cycles
	 *
	 * The cycle is semi-visible in the part 2 sample input, where starting node 11A will hit 11Z every 2 steps, while starting node 22A will hit 22Z every 3 steps.
	 * Running the same loop as in part 1 for each starting node of part 2 many times and printing the number of steps every time a target point is reached,
	 * this will print the cycle length (which is incidentally the exact same as the initial distance from start to target)
	 */
	override fun partTwo(): Long {
		// Get the starting points (ending with an A) and target points (ending with a Z)
		val startingPoints = nodes.keys.filter { it.endsWith('A') }
		val targetPoints = nodes.keys.filter { it.endsWith('Z') }.toSet()

		// Map each starting point to the number of steps it takes to reach one of the target points
		val numberOfSteps = startingPoints.map { start ->
			countNumberOfSteps(start) { targetPoints.contains(it) }.toLong()
		}

		return lcm(numberOfSteps)
	}

	private fun countNumberOfSteps(start: String, endCondition: (String) -> Boolean): Int {
		var current = start
		var numberOfSteps = 0

		while (!endCondition.invoke(current)) {
			val direction = directions[numberOfSteps % directions.size]
			val (nextLeft, nextRight) = nodes.getValue(current)
			current = when (direction) {
				Direction.LEFT -> nextLeft
				Direction.RIGHT -> nextRight
			}
			numberOfSteps++
		}

		return numberOfSteps
	}

	private enum class Direction {
		LEFT, RIGHT
	}

}