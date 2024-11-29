package ch.mikewong.adventofcode.common.challenges

import ch.mikewong.adventofcode.common.models.Size
import ch.mikewong.adventofcode.input.InputLoader
import ch.mikewong.adventofcode.input.InputUtil

abstract class Day<O, T>(
	private val year: Int,
	val day: Int,
	val title: String,
) {

	var isControlSet = false

	/** Get the entire input as a single string, including new line characters */
	protected val input: String by lazy { InputUtil(year).readInput(day) }

	/** Get the input as a list of lines */
	protected val inputLines: List<String> by lazy { InputUtil(year).readInputLines(day) }

	/** Get the input as a list of input groups, where each group is separated by two new lines */
	protected val inputGroups: List<List<String>> by lazy { InputUtil(year).readInputGroups(day) }

	/** Get the size of the input. The width will be equal to the longest row */
	protected val inputSize by lazy { Size(inputLines.maxOf { it.length }.toLong(), inputLines.count().toLong()) }

	abstract fun partOne(): O
	abstract fun partTwo(): T

	open fun initialize() {
		InputLoader.downloadInput(year, day)
	}

	override fun toString(): String {
		return "Year $year, Day $day: $title"
	}
}