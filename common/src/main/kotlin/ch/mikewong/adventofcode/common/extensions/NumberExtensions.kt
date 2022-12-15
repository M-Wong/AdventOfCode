package ch.mikewong.adventofcode.common.extensions

import kotlin.math.pow

/**
 * @return [this] to the power of [n]
 */
fun Int.pow(n: Int) = this.toDouble().pow(n)