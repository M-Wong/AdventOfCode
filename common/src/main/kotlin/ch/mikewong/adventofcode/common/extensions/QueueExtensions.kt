package ch.mikewong.adventofcode.common.extensions

import java.util.*

/**
 * Calls [block] while [this] queue is not empty
 */
inline fun <T> Queue<T>.process(block: (T) -> Unit) {
	while (this.isNotEmpty()) {
		block.invoke(this.remove())
	}
}