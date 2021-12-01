package ch.mikewong.adventofcode.util

import ch.mikewong.adventofcode.Application

object InputUtil {
	fun readInputLines(index: Int) = Application::class.java.getResource("/input/day$index.txt")
		?.readText()
		?.split(System.lineSeparator())
		?: emptyList()
}