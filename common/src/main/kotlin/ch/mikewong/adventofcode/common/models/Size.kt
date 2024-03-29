package ch.mikewong.adventofcode.common.models

data class Size(val width: Long, val height: Long) {
	/** The range for row indices */
	fun rowRange() = 0 until height

	/** The range for column indices */
	fun colRange() = 0 until width

	/**
	 * @return An area equal to this size
	 */
	fun toArea() = Area(
		topLeft = Point(0, 0),
		bottomRight = Point(height - 1, width - 1)
	)
}
