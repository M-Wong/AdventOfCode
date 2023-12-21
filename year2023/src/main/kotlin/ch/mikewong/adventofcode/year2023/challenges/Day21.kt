package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toGridNotNull
import ch.mikewong.adventofcode.common.models.Point

class Day21 : Day<Int, Long>(2023, 21, "Step Counter") {

	private lateinit var startingPoint: Point
	private val rocks = inputLines.toGridNotNull { point, c ->
		if (c == 'S') startingPoint = point
		c.takeIf { it == '#' }
	}.keys
	private val bounds = inputSize.toArea()

	// 3689
	override fun partOne(): Int {
		val numberOfSteps = if (isControlSet) 6 else 64
		return countReachableGardenPlots(numberOfSteps)
	}

	// 610158187362102
	override fun partTwo(): Long {
		if (isControlSet) {
			// Control set has a different layout than the real input, so the calculation below doesn't work for it
			val numberOfSteps = 100
			return countInfinitelyReachableGardenPlots(numberOfSteps).toLong()
		}

		// The total number of steps the elf has to take
		val numberOfSteps = 26501365

		// The number of grids the elf can travel at most (since the input data is square and has no obstacles on the same row/column as the starting point)
		val numberOfGrids = numberOfSteps / inputSize.width

		// The number of remaining steps after reaching the final grid (this is basically half the square size, meaning taking this many steps leads exactly to the border)
		val remainingSteps = numberOfSteps % inputSize.width

		// Calculate the number of reachable points based on the number of grids the elf can travel in total
		var totalReachable = 0L
		(0 until numberOfGrids).forEach { i ->
			totalReachable += (29930 * (i + 1)) - (112 * i)
		}

		// Add the reachable points for the remaining number of steps (which is from the starting point of the last grid)
		totalReachable += countInfinitelyReachableGardenPlots(remainingSteps.toInt())

		// I have no idea why exactly the above formula works, but here's how I got those numbers:
//		val stepsToBorder = inputSize.width / 2
//		val steps = listOf(
//			stepsToBorder + (inputSize.width * 0), // 65
//			stepsToBorder + (inputSize.width * 1), // 65 + 131
//			stepsToBorder + (inputSize.width * 2), // 65 + 262
//			stepsToBorder + (inputSize.width * 3), // 65 + 393
//			stepsToBorder + (inputSize.width * 4), // 65 + 524
//		)
//		val reachable = steps.map { countInfinitelyReachableGardenPlots(it.toInt()) }
//		println(reachable)

		// The above will print the following numbers:
//		val stepsToReachable = mapOf(
//			stepsToBorder + (inputSize.width * 0) to 3802,
//			stepsToBorder + (inputSize.width * 1) to 33732,    // + 29'930 = (29930 * 1) - (112 * 0)
//			stepsToBorder + (inputSize.width * 2) to 93480,    // + 59'748 = (29930 * 2) - (112 * 1)
//			stepsToBorder + (inputSize.width * 3) to 183046,    // + 89'556 = (29930 * 3) - (112 * 2)
//			stepsToBorder + (inputSize.width * 4) to 302430,    // + 119'384 = (29930 * 4) - (112 * 3)
//		)

		// Except for the initial value (which is the value for the remaining steps in the final grid), all of these values increase in the same way:
		// 3802 	-> 	33732 	= +29'930		which is 	(29930 * 1) - (112 * 0)
		// 33732	->	93480	= +59'748		which is 	(29930 * 2) - (112 * 1)
		// 93480	->	183046	= +89'556		which is 	(29930 * 3) - (112 * 2)
		// 183046	->	302430	= +119'384		which is 	(29930 * 4) - (112 * 3)

		return totalReachable
	}

	private fun countReachableGardenPlots(numberOfSteps: Int): Int {
		// Keep track of the set of points that were reachable during each step
		val reachableBySteps = Array(numberOfSteps + 1) { emptySet<Point>() }
		reachableBySteps[0] = setOf(startingPoint)

		(1..numberOfSteps).forEach { step ->
			// For each step, take the reachable points of the previous step and map them to the adjacent points, minus the ones that are rocks or out of bounds
			val previous = reachableBySteps[step - 1]
			reachableBySteps[step] = previous.map { point ->
				point.adjacent { bounds.contains(it) && !rocks.contains(it) }
			}.flatten().toSet()
		}

		return reachableBySteps.last().size
	}

	private fun countInfinitelyReachableGardenPlots(numberOfSteps: Int): Int {
		// Keep track of the set of points that were reachable during each step
		val reachableBySteps = Array(numberOfSteps + 1) { emptySet<Point>() }
		reachableBySteps[0] = setOf(startingPoint)

		(1..numberOfSteps).forEach { step ->
			// For each step, take the reachable points of the previous step and map them to the adjacent points, minus the ones that are rocks (even the ones that are out of bounds, since we just wrap around)
			val previous = reachableBySteps[step - 1]
			reachableBySteps[step] = previous.map { point ->
				point.adjacent { !rocks.contains(it.wrapAround(bounds)) }
			}.flatten().toSet()
		}

		return reachableBySteps.last().size
	}

}