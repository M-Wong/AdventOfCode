package ch.mikewong.adventofcode.common.extensions

/**
 * Cartesian product for CharRanges.
 * The input is the range itself repeated n times
 *
 * @param n How often the Range is repeated
 * @return The cartesian prodcut of the range
 */
fun IntProgression.cartProd(n: Int): Sequence<List<Int>> {
	val ranges = repeat(n).toList().toTypedArray()
	return cartProd(*(ranges))
}

/**
 * Make a [Sequence] that returns object over and over again.
 * Runs indefinitely unless the [times] argument is specified.
 *
 * @param times How often the object is repeated. null means its repeated indefinitely
 */
fun <T : Any> T.repeat(times: Int? = null): Sequence<T> = sequence {
	var count = 0
	while (times == null || count++ < times) yield(this@repeat)
}

/**
 * Cartesian product of input [Iterable]. (https://en.wikipedia.org/wiki/Cartesian_product)
 * Roughly equivalent to nested for-loops in a generator expression.
 * For example, product(A, B) returns the same as ((x,y) for x in A for y in B).
 * The nested loops cycle like an odometer with the rightmost element advancing on every iteration.
 * This pattern creates a lexicographic ordering so that if the input’s iterables are sorted,
 * the product tuples are emitted in sorted order.
 *
 * @param items The input for which the cartesian product is calucalted
 *
 * @return A [Sequence] of [List] which contains the cartesian product
 */
fun <T : Any> cartProd(vararg items: Iterable<T>): Sequence<List<T>> = sequence {
	if (items.all { it.iterator().hasNext() }) {
		val itemsIter = items.map { it.iterator() }.filter { it.hasNext() }.toMutableList()
		val currElement: MutableList<T> = itemsIter.map { it.next() }.toMutableList()
		loop@ while (true) {
			yield(currElement.toList())
			for (pos in itemsIter.count() - 1 downTo 0) {
				if (!itemsIter[pos].hasNext()) {
					if (pos == 0) break@loop
					itemsIter[pos] = items[pos].iterator()
					currElement[pos] = itemsIter[pos].next()
				} else {
					currElement[pos] = itemsIter[pos].next()
					break
				}
			}
		}
	}
}