package ch.mikewong.adventofcode.common.extensions

import kotlin.math.pow

/**
 * @return [this] to the power of [n]
 */
fun Int.pow(n: Int) = this.toDouble().pow(n)

/**
 * @return The greatest common divisor of two numbers [a] and [b]
 */
fun gcd(a: Int, b: Int): Int {
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
fun lcm(a: Int, b: Int): Int {
	val gcd = gcd(a, b)
	return a * b / gcd
}