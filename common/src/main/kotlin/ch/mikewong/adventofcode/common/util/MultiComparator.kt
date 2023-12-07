package ch.mikewong.adventofcode.common.util

/**
 * Class that combines multiple comparators into a single one. Similar to what [compareBy] does with lambdas
 */
class MultiComparator<T>(private vararg val comparators: Comparator<T>) : Comparator<T> {
	override fun compare(o1: T, o2: T): Int {
		return comparators.firstNotNullOfOrNull { comparator ->
			// Return the comparison value of the first comparator that doesn't return 0
			comparator.compare(o1, o2).takeIf { it != 0 }
		} ?: 0
	}
}