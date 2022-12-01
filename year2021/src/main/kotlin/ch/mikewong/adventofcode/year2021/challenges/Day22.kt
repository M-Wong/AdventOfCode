package ch.mikewong.adventofcode.year2021.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Cuboid
import ch.mikewong.adventofcode.common.util.asLongs

class Day22 : Day<Long, Long>(2021, 22, "Reactor Reboot") {

	private val inputSteps by lazy {
		inputLines.map {
			val parts = it.split(" ")
			val isOn = parts.first() == "on"
			val coordinates = parts.last().split(",")
			val xRange = coordinates[0].substringAfter("x=").split("..").asLongs()
			val yRange = coordinates[1].substringAfter("y=").split("..").asLongs()
			val zRange = coordinates[2].substringAfter("z=").split("..").asLongs()
			val cuboid = Cuboid(
				xRange.first(), xRange.last(),
				yRange.first(), yRange.last(),
				zRange.first(), zRange.last(),
			)

			RebootStep(cuboid, isOn)
		}
	}

	override fun partOne(): Long {
		val startingCuboid = Cuboid(-50, 50, -50, 50, -50, 50)
		val coercedSteps = coerceInputSteps(startingCuboid)
		return  calculateTurnedOnFieldsForSteps(coercedSteps)
	}

	override fun partTwo() = calculateTurnedOnFieldsForSteps(inputSteps)

	private fun calculateTurnedOnFieldsForSteps(steps: List<RebootStep>): Long {
		val allChanges = mutableListOf<RebootStep>()
		steps.forEach { step ->
			// Add the current reboot step to the list of new changes if it turns fields on
			val newChanges = mutableListOf<RebootStep>()
			if (step.isOn) {
				newChanges.add(step)
			}

			allChanges.forEach { previousChange ->
				// For each previous change, check if there is an intersection
				val intersection = step.cuboid.intersect(previousChange.cuboid)
				intersection?.let {
					// If there is, add it to the list of new changes with the inverted on flag of the previous change
					newChanges.add(RebootStep(it, !previousChange.isOn))
				}
			}
			allChanges.addAll(newChanges)
		}

		// Sum up the values of all changes
		return allChanges.sumOf { it.value }
	}

	/**
	 * Restringt the input reboot steps with a starting cuboid
	 */
	private fun coerceInputSteps(startingCuboid: Cuboid): List<RebootStep> {
		return inputSteps.mapNotNull { step ->
			val intersection = startingCuboid.intersect(step.cuboid)
			intersection?.let { RebootStep(it, step.isOn) }
		}
	}

	private data class RebootStep(val cuboid: Cuboid, val isOn: Boolean) {
		val value = if (isOn) cuboid.volume else -cuboid.volume
	}

}