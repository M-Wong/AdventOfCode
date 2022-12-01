package ch.mikewong.adventofcode.common.challenges

import ch.mikewong.adventofcode.common.util.InputUtil

abstract class Day<O, T>(val year:Int, val index: Int, val title: String) {

	protected val inputLines: List<String> by lazy { InputUtil.readInputLines(index) }
	protected val inputGroups: List<List<String>> by lazy { InputUtil.readInputGroups(index) }

	abstract fun partOne(): O
	abstract fun partTwo(): T

	override fun toString(): String {
		return "Year $year, Day $index: $title"
	}
}