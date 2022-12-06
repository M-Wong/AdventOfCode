package ch.mikewong.adventofcode.common.util

import java.lang.System.lineSeparator

object InputUtil {
	fun readInput(index: Int) = readResource("/input/day$index.txt") ?: ""

	fun readInputLines(index: Int) = readResource("/input/day$index.txt")?.split(lineSeparator()) ?: emptyList()

	fun readInputGroups(index: Int) = readResource("/input/day$index.txt")
		?.split(lineSeparator() + lineSeparator())
		?.map { it.split(lineSeparator()) }
		?: emptyList()

	private fun readResource(path: String) = this::class.java.getResource(path)?.readText()
}