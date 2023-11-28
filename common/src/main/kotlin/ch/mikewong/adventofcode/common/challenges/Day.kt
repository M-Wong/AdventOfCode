package ch.mikewong.adventofcode.common.challenges

import ch.mikewong.adventofcode.common.input.InputLoader
import ch.mikewong.adventofcode.common.util.InputUtil

abstract class Day<O, T>(
	private val year: Int,
	val day: Int,
	val title: String,
) {

	protected val input: String by lazy { InputUtil.readInput(day) }
	protected val inputLines: List<String> by lazy { InputUtil.readInputLines(day) }
	protected val inputGroups: List<List<String>> by lazy { InputUtil.readInputGroups(day) }

	abstract fun partOne(): O
	abstract fun partTwo(): T

	open fun initialize() {
		InputLoader.downloadInput(year, day)
	}

	override fun toString(): String {
		return "Year $year, Day $day: $title"
	}
}