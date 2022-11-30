package ch.mikewong.adventofcode.util

import java.lang.System.lineSeparator

object InputUtil {
	fun readInputLines(index: Int) = readResource("/input/day$index.txt")?.split(lineSeparator()) ?: emptyList()

	fun readInputGroups(index: Int) = readResource("/input/day$index.txt")
		?.split(lineSeparator() + lineSeparator())
		?.map { it.split(lineSeparator()) }
		?: emptyList()

	private fun readResource(path: String) = this::class.java.getResource(path)?.readText()
}