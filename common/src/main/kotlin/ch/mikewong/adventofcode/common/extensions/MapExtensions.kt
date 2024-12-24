package ch.mikewong.adventofcode.common.extensions

/**
 * Increment the value of [key] in a map with value type long by [amount]
 */
fun <T> MutableMap<T, Long>.increment(key: T, amount: Long = 1L): MutableMap<T, Long> {
	this[key] = this.getOrDefault(key, 0L) + amount
	return this
}

/**
 * Sum up the values of two maps with a long value type
 */
fun <T> Map<T, Long>.addTogether(other: Map<T, Long>): Map<T, Long> {
	val result = this.toMutableMap()
	other.forEach { (k, v) ->
		if (result.containsKey(k)) {
			result[k] = result[k]!! + v
		} else {
			result[k] = v
		}
	}
	return result
}

/**
 * Pair the keys of two maps that intersect by their value
 */
fun <K, V> Map<K, V>.keysIntersectingByValue(other: Map<K, V>): List<Pair<K, K>> {
	val result = mutableListOf<Pair<K, K>>()
	val intersection = this.values.intersect(other.values)
	this.filterValues { intersection.contains(it) }.forEach { (k, v) ->
		result += k to (other.filterValues { it == v }.keys.single())
	}
	return result
}

/**
 * Return the range of values in a map with value type long
 */
fun <T> Map<T, Long>.range(): Long {
	return maxOf { it.value } - minOf { it.value }
}

/**
 * Return a new map with the [key] set to [value]
 */
fun <K, V> Map<K, V>.set(key: K, value: V): Map<K, V> {
	val newMap = this.toMutableMap()
	newMap[key] = value
	return newMap
}

/**
 * Swaps the values of [from] and [to]
 */
fun <K, V> MutableMap<K, V>.swap(from: K, to: K) {
	val fromTile = this.getValue(from)
	val toTile = this.getValue(to)
	this[from] = toTile
	this[to] = fromTile
}