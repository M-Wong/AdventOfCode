package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.product
import kotlin.math.ceil

class Day19 : Day<Int, Int>(2022, 19, "Not Enough Minerals") {

	private val blueprints by lazy { readInput() }

	override fun partOne(): Int {
		val results = blueprints.associateWith { MiningSimulation(it, 24).getMaxGeodeCount() }
		return results.map { (blueprint, geodeCount) -> blueprint.id * geodeCount }.sum()
	}

	override fun partTwo(): Int {
		val results = blueprints.take(3).associateWith { MiningSimulation(it, 32).getMaxGeodeCount() }
		return results.map { (_, geodeCount) -> geodeCount }.product()
	}

	private fun readInput(): List<Blueprint> {
		val regex = "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.".toRegex()
		return inputLines.map { line ->
			val matches = regex.matchEntire(line)!!.groupValues
			Blueprint(
				matches[1].toInt(),
				Robot.OreRobot(matches[2].toInt()),
				Robot.ClayRobot(matches[3].toInt()),
				Robot.ObsidianRobot(matches[4].toInt(), matches[5].toInt()),
				Robot.GeodeRobot(matches[6].toInt(), matches[7].toInt()),
			)
		}
	}

	private class MiningSimulation(private val blueprint: Blueprint, private val maxTime: Int) {
		private var currentMaxGeodeCount = 0

		// Always start with one ore robot
		private val initialState = State(0, listOf(blueprint.oreRobot))

		fun getMaxGeodeCount(state: State = initialState): Int {
			currentMaxGeodeCount = state.currentMaterials.getValue(Material.GEODE).coerceAtLeast(currentMaxGeodeCount)

			if (state.minute >= maxTime) return state.currentMaterials.getValue(Material.GEODE)

			val production = state.production

			// The maximum potential geode count for this state is the current geode count plus the production of one additional geode robot per minute
			val currentGeodeProduction = production.getOrDefault(Material.GEODE, 0)
			val maxPotentialGeodeCount = state.currentMaterials.getValue(Material.GEODE) + (0 until maxTime - state.minute).sumOf { currentGeodeProduction + it }
			if (maxPotentialGeodeCount <= currentMaxGeodeCount) {
				return 0
			}

			var max = 0

			if (production.getValue(Material.OBSIDIAN) > 0) {
				// If a geode robot can be built, check if the max geode count is higher if one is built this turn
				max = getMaxGeodeCount(state.build(blueprint.geodeRobot)).coerceAtLeast(max)
			}

			if (production.getValue(Material.CLAY) > 0 && production.getValue(Material.OBSIDIAN) < blueprint.maxObsidianCost) {
				// If an obsidian robot can be built and isn't redundant, check if the max geode count is higher if one is built this turn
				max = getMaxGeodeCount(state.build(blueprint.obsidianRobot)).coerceAtLeast(max)
			}

			if (production.getValue(Material.CLAY) < blueprint.maxClayCost) {
				// If a clay robot isn't redundant, check if the max geode count is higher if one is built this turn
				max = getMaxGeodeCount(state.build(blueprint.clayRobot)).coerceAtLeast(max)
			}

			if (production.getValue(Material.ORE) < blueprint.maxOreCost) {
				// If an ore robot isn't redundant, check if the max geode count is higher if one is built this turn
				max = getMaxGeodeCount(state.build(blueprint.oreRobot)).coerceAtLeast(max)
			}

			return max
		}

		/**
		 * Creates the next useful state when trying to build a specific [robot]
		 */
		private fun State.build(robot: Robot): State {
			// Calculate the minutes until the next useful state, which is either when the time is up or the next robot is ready
			val minutesToNextState = robot.canBeBuiltInMinutes(currentMaterials, production).coerceAtMost(maxTime - minute)

			// The next state has the following properties:
			// 1. Time is skipped by the amount of minutes until the next useful state
			// 2. The type of robot to be built is added to the production line
			// 3. The inventory is updated by adding the current production (per minute) and minus the robot production cost
			return State(
				minute + minutesToNextState,
				currentRobots.plus(robot),
				currentMaterials.mapValues { (material, inventory) ->
					inventory + (production.getOrDefault(material, 0) * minutesToNextState) - robot.requirements.getOrDefault(material, 0)
				}
			)
		}
	}

	private data class State(
		val minute: Int,
		val currentRobots: List<Robot>,
		val currentMaterials: Map<Material, Int> = mapOf(Material.ORE to 0, Material.CLAY to 0, Material.OBSIDIAN to 0, Material.GEODE to 0),
	) {
		val production by lazy {
			mapOf(
				Material.ORE to this.currentRobots.count { it is Robot.OreRobot },
				Material.CLAY to this.currentRobots.count { it is Robot.ClayRobot },
				Material.OBSIDIAN to this.currentRobots.count { it is Robot.ObsidianRobot },
				Material.GEODE to this.currentRobots.count { it is Robot.GeodeRobot },
			)
		}
	}

	private data class Blueprint(
		val id: Int,
		val oreRobot: Robot.OreRobot,
		val clayRobot: Robot.ClayRobot,
		val obsidianRobot: Robot.ObsidianRobot,
		val geodeRobot: Robot.GeodeRobot,
	) {
		// Since only one robot can be built per minute, the maximum material cost per robot is the maximum that should be produced
		val maxOreCost = maxOf(
			clayRobot.requirements.getValue(Material.ORE),
			obsidianRobot.requirements.getValue(Material.ORE),
			geodeRobot.requirements.getValue(Material.ORE),
		)
		val maxClayCost = obsidianRobot.requirements.getValue(Material.CLAY)
		val maxObsidianCost = geodeRobot.requirements.getValue(Material.OBSIDIAN)
	}

	private sealed class Robot(val requirements: Map<Material, Int>) {
		/**
		 * @return The number of minutes it takes with the current [materials] and [production] to build another one of this robot
		 */
		fun canBeBuiltInMinutes(
			materials: Map<Material, Int>,
			production: Map<Material, Int>,
		): Int {
			return requirements.maxOf { (material, cost) ->
				val additionalMaterialNeeded = cost - materials.getValue(material)
				if (additionalMaterialNeeded <= 0) {
					1
				} else {
					// Round up the number of minutes needed to produce the additional material + 1 because the robot takes another minute to be built
					ceil(additionalMaterialNeeded.toFloat() / production.getValue(material)).toInt() + 1
				}
			}
		}

		data class OreRobot(private val requiredOre: Int) : Robot(mapOf(Material.ORE to requiredOre))
		data class ClayRobot(private val requiredOre: Int): Robot(mapOf(Material.ORE to requiredOre))
		data class ObsidianRobot(private val requiredOre: Int, private val requiredClay: Int): Robot(mapOf(Material.ORE to requiredOre, Material.CLAY to requiredClay))
		data class GeodeRobot(private val requiredOre: Int, private val requiredObsidian: Int): Robot(mapOf(Material.ORE to requiredOre, Material.OBSIDIAN to requiredObsidian))
	}

	private enum class Material {
		ORE, CLAY, OBSIDIAN, GEODE
	}

}