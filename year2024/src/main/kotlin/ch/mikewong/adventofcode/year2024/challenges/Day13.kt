package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.isInteger

class Day13 : Day<Long, Long>(2024, 13, "Claw Contraption") {

	private val buttonRegex = ".*X\\+(\\d+), Y\\+(\\d+).*".toRegex()
	private val machineRegex = ".*X=(\\d+), Y=(\\d+).*".toRegex()

	private val machines by lazy { parseInput() }

	// 28059
	override fun partOne(): Long {
		val buttonPresses = machines.mapNotNull { machine ->
			solveLinearSystem(
				a1 = machine.buttonA.dx,
				b1 = machine.buttonB.dx,
				c1 = machine.prize.targetX,
				a2 = machine.buttonA.dy,
				b2 = machine.buttonB.dy,
				c2 = machine.prize.targetY
			)
		}.filter { solution ->
			// Filter out solutions that are not integers (not applicable for AoC) and are less than 100 (restriction for part 1)
			solution.x.isInteger() && solution.x < 100 && solution.y.isInteger() && solution.y < 100
		}

		return buttonPresses.sumOf { (x, y) -> (3 * x.toLong()) + y.toLong() }
	}

	// 102255878088512
	override fun partTwo(): Long {
		val buttonPresses = machines.mapNotNull {
			solveLinearSystem(
				a1 = it.buttonA.dx,
				b1 = it.buttonB.dx,
				c1 = it.prize.targetX + 10000000000000,
				a2 = it.buttonA.dy,
				b2 = it.buttonB.dy,
				c2 = it.prize.targetY + 10000000000000
			)
		}.filter { solution ->
			// Filter out solutions that are not integers (not applicable for AoC)
			solution.x.isInteger() && solution.y.isInteger()
		}

		return buttonPresses.sumOf { (x, y) -> (3 * x.toLong()) + y.toLong() }
	}

	private fun parseInput(): List<Machine> {
		return inputGroups.map { group ->
			val (aX, aY) = requireNotNull(buttonRegex.find(group[0])).destructured
			val (bX, bY) = requireNotNull(buttonRegex.find(group[1])).destructured
			val (pX, pY) = requireNotNull(machineRegex.find(group[2])).destructured
			Machine(
				Button(aX.toLong(), aY.toLong()),
				Button(bX.toLong(), bY.toLong()),
				Prize(pX.toLong(), pY.toLong()),
			)
		}
	}

	/**
	 * Solve linear equations using Cramer's Rule for a two variable system
	 * Example linear system:
	 * 		94x + 22y = 8400
	 * 		34x + 67y = 5400
	 * Solving for x and y, this function needs to be called with the following parameters:
	 * a1 = 94, b1 = 22, c1 = 8400, a2 = 34, b2 = 67, c2 = 5400
	 *
	 * @return Null if there is no solution or else [Solution] with x and y values (note, these might NOT be integers but floating point numbers)
	 */
	private fun solveLinearSystem(a1: Long, b1: Long, c1: Long, a2: Long, b2: Long, c2: Long): Solution? {
		val determinant = a1 * b2 - b1 * a2

		if (determinant == 0L) {
			// Linear equations have no solution if the determinant is 0
			return null
		}

		val x = (c1 * b2 - b1 * c2) / determinant.toDouble()
		val y = (a1 * c2 - c1 * a2) / determinant.toDouble()

		return Solution(x, y)
	}

	data class Button(val dx: Long, val dy: Long)

	data class Prize(val targetX: Long, val targetY: Long)

	data class Machine(val buttonA: Button, val buttonB: Button, val prize: Prize)

	data class Solution(val x: Double, val y: Double)

}