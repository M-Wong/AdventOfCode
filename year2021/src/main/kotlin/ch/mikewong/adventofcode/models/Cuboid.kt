package ch.mikewong.adventofcode.models

import kotlin.math.max
import kotlin.math.min

data class Cuboid(
	val x1: Long, val x2: Long,
	val y1: Long, val y2: Long,
	val z1: Long, val z2: Long
) {
	val volume = (x2 - x1 + 1) * (y2 - y1 + 1) * (z2 - z1 + 1)

	/**
	 * @return True if this cuboid overlaps with [other] cuboid
	 */
	fun overlaps(other: Cuboid): Boolean {
		return (this.x2 >= other.x1) && (this.x1 <= other.x2)
				&& (this.y2 >= other.y1) && (this.y1 <= other.y2)
				&& (this.z2 >= other.z1) && (this.z1 <= other.z2)
	}

	/**
	 * @return Another cuboid that describes the intersection between this and [other] cuboid or null if they don't overlap
	 */
	fun intersect(other: Cuboid): Cuboid? {
		if (!this.overlaps(other)) return null

		return Cuboid(
			x1 = max(this.x1, other.x1), x2 = min(this.x2, other.x2),
			y1 = max(this.y1, other.y1), y2 = min(this.y2, other.y2),
			z1 = max(this.z1, other.z1), z2 = min(this.z2, other.z2)
		)
	}
}