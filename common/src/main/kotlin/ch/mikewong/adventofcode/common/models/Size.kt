package ch.mikewong.adventofcode.common.models

data class Size(val width: Int, val height: Int) {
	/** The range for row indices */
	fun rowRange() = 0 until height

	/** The range for column indices */
	fun colRange() = 0 until width
}
