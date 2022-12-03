package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.util.getCharactersInCommon

class Day3 : Day<Int, Int>(2022, 3, "Rucksack Reorganization") {

	private val rucksacks = inputLines.map { Rucksack(it) }
	private val itemTypePriorities = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

	override fun partOne(): Int {
		return rucksacks.sumOf { it.getItemTypeInCommon().priority }
	}

	override fun partTwo(): Int {
		return rucksacks.chunked(3).sumOf { it.getBadgeType().priority }
	}

	private val String.priority: Int
		get() = itemTypePriorities.indexOf(this) + 1

	private fun List<Rucksack>.getBadgeType() = this.map { it.input }.getCharactersInCommon().single().toString()

	private data class Rucksack(val input: String) {
		val compartmentA = input.substring(0, input.length / 2)
		val compartmentB = input.substring(input.length / 2)
		fun getItemTypeInCommon(): String {
			return listOf(compartmentA, compartmentB).getCharactersInCommon().single().toString()
		}
	}

}