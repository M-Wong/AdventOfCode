package ch.mikewong.adventofcode.util

fun List<String>.asInts(radix: Int = 10) = this.map { it.toInt(radix) }

fun <T> Array<T>.shiftLeft(count: Int = 1, defaultProvider: (Int) -> T) {
	indices.forEach { idx ->
		if (idx < size - count) {
			this[idx] = this[idx + count]
		} else {
			this[idx] = defaultProvider.invoke(idx)
		}
	}
}