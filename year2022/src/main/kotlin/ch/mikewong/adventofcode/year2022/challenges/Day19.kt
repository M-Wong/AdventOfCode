package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.product

class Day19 : Day<Int, Int>(2022, 19, "Not Enough Minerals") {

	private val blueprints by lazy { readInput() }

	override fun partOne(): Int {
		val results = blueprints.associateWith { findMaxGeodeCount(it, 24) }
		return results.map { (blueprint, geodeCount) -> blueprint.id * geodeCount }.sum()
	}

	override fun partTwo(): Int {
		val results = blueprints.take(3).associateWith { findMaxGeodeCount(it, 32) }
		return results.map { (_, geodeCount) -> geodeCount }.product()
	}

	/**
	 * TODO: Algorithm works on Example Pt1, Input Pt2 and Input Pt2 but not Example Pt2
	 */
	private fun findMaxGeodeCount(blueprint: Blueprint, maxTime: Int): Int {
		val initialState = State(oreRobotCount = 1)
		val states = mutableSetOf(initialState)

		repeat(maxTime) {
			val newStates = mutableSetOf<State>()

			for (state in states) {
				// Check which robots can be built with the currently available material
				val canBuildGeodeRobot = state.canBuildGeodeRobot(blueprint)
				val canBuildObsidianRobot = state.canBuildObsidianRobot(blueprint)
				val canBuildClayRobot = state.canBuildClayRobot(blueprint)
				val canBuildOreRobot = state.canBuildOreRobot(blueprint)

				if (canBuildGeodeRobot) {
					// Assume that if we can build a geode robot, always do so and skip other states
					newStates.add(state.mine().buildGeodeRobot(blueprint))
					continue
				}

				if (canBuildObsidianRobot && state.obsidianRobotCount < blueprint.maxObsidianCost) {
					// Assume that if we can build an obsidian robot, and we don't yet produce more than we can use, always do so and skip other states
					newStates.add(state.mine().buildObsidianRobot(blueprint))
					continue
				}

				if (canBuildClayRobot && state.clayRobotCount < blueprint.maxClayCost) {
					// Only build a new clay robot if we don't yet produce more than we can use
					newStates.add(state.mine().buildClayRobot(blueprint))
				}

				if (canBuildOreRobot && state.oreRobotCount < blueprint.maxOreCost) {
					// Only build a new ore robot if we don't yet produce more than we can use
					newStates.add(state.mine().buildOreRobot(blueprint))
				}

				newStates.add(state.mine())
			}

			states.clear()
			states.addAll(newStates)
		}

		return states.maxOf { it.geodeCount }
	}

	private fun readInput(): List<Blueprint> {
		val regex = "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.".toRegex()
		return inputLines.map { line ->
			val matches = regex.matchEntire(line)!!.groupValues
			Blueprint(
				matches[1].toInt(),
				matches[2].toInt(),
				matches[3].toInt(),
				matches[4].toInt(),
				matches[5].toInt(),
				matches[6].toInt(),
				matches[7].toInt(),
			)
		}
	}

	private data class State(
		val oreCount: Int = 0,
		val clayCount: Int = 0,
		val obsidianCount: Int = 0,
		val geodeCount: Int = 0,
		val oreRobotCount: Int = 0,
		val clayRobotCount: Int = 0,
		val obsidianRobotCount: Int = 0,
		val geodeRobotCount: Int = 0,
	) : Comparable<State> {
		override fun compareTo(other: State) = this.geodeCount - other.geodeCount

		// Methods to check if a specific robot can be built with the currently available materials
		fun canBuildOreRobot(blueprint: Blueprint) = oreCount >= blueprint.oreRobotOreCost
		fun canBuildClayRobot(blueprint: Blueprint) = oreCount >= blueprint.clayRobotOreCost
		fun canBuildObsidianRobot(blueprint: Blueprint) = oreCount >= blueprint.obsidianRobotOreCost && clayCount >= blueprint.obsidianRobotClayCost
		fun canBuildGeodeRobot(blueprint: Blueprint) = oreCount >= blueprint.geodeRobotOreCost && obsidianCount >= blueprint.geodeRobotObsidianCost

		// Methods to build a specific robot and mine in the same step
		fun buildOreRobot(blueprint: Blueprint) = this.copy(oreCount = oreCount - blueprint.oreRobotOreCost, oreRobotCount = oreRobotCount + 1)
		fun buildClayRobot(blueprint: Blueprint) = this.copy(oreCount = oreCount - blueprint.clayRobotOreCost, clayRobotCount = clayRobotCount + 1)
		fun buildObsidianRobot(blueprint: Blueprint) = this.copy(oreCount = oreCount - blueprint.obsidianRobotOreCost, clayCount = clayCount - blueprint.obsidianRobotClayCost, obsidianRobotCount = obsidianRobotCount + 1)
		fun buildGeodeRobot(blueprint: Blueprint) = this.copy(oreCount = oreCount - blueprint.geodeRobotOreCost, obsidianCount = obsidianCount - blueprint.geodeRobotObsidianCost, geodeRobotCount = geodeRobotCount + 1)

		// Method to only mine materials
		fun mine() = this.copy(oreCount = oreCount + oreRobotCount, clayCount = clayCount + clayRobotCount, obsidianCount = obsidianCount + obsidianRobotCount, geodeCount = geodeCount + geodeRobotCount)
	}

	private data class Blueprint(
		val id: Int,
		val oreRobotOreCost: Int,
		val clayRobotOreCost: Int,
		val obsidianRobotOreCost: Int,
		val obsidianRobotClayCost: Int,
		val geodeRobotOreCost: Int,
		val geodeRobotObsidianCost: Int,
	) {
		val maxObsidianCost = geodeRobotObsidianCost
		val maxClayCost = obsidianRobotClayCost
		val maxOreCost = maxOf(geodeRobotOreCost, clayRobotOreCost, oreRobotOreCost)
	}
}