package ch.mikewong.adventofcode.common.extensions

import kotlin.math.pow

/**
 * @return [this] to the power of [n]
 */
fun Int.pow(n: Int) = this.toDouble().pow(n)

/**
 * @return The greatest common divisor of two numbers [a] and [b]
 */
fun gcd(a: Long, b: Long): Long {
	require(a > 0)
	require(b > 0)

	var n1 = a
	var n2 = b

	while (n1 != n2) {
		if (n1 > n2) n1 -= n2 else n2 -= n1
	}

	return n1
}

/**
 * @return The least common multiple of two numbers [a] and [b]
 */
fun lcm(a: Long, b: Long): Long {
	val gcd = gcd(a, b)
	return a * b / gcd
}

fun lcm(input: List<Long>): Long {
	var result = input[0]
	for (i in 1 until input.size) result = lcm(result, input[i])
	return result
}