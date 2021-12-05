package ch.mikewong.adventofcode.util

fun List<String>.asInts(radix: Int = 10) = this.map { it.toInt(radix) }