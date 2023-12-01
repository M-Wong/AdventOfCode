package ch.mikewong.adventofcode.common.challenges

import ch.mikewong.adventofcode.common.input.InputLoader
import ch.mikewong.adventofcode.common.util.InputUtil

abstract class Day<O, T>(
	private val year: Int,
	val day: Int,
	val title: String,
) {

	/** Get the entire input as a single string, including new line characters */
	protected val input: String by lazy { InputUtil.readInput(day) }

	/** Get the input as a list of lines */
	protected val inputLines: List<String> by lazy { InputUtil.readInputLines(day) }

	/** Get the input as a list of input groups, where each group is separated by two new lines */
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