package ch.mikewong.adventofcode.common.models

import kotlin.math.max
import kotlin.math.min

data class Cuboid(
	val x1: Long, val x2: Long,
	val y1: Long, val y2: Long,
	val z1: Long, val z2: Long
) {
	constructor(start: Point3D, end: Point3D): this(
		x1 = min(start.x.toLong(), end.x.toLong()), x2 = max(start.x.toLong(), end.x.toLong()),
		y1 = min(start.y.toLong(), end.y.toLong()), y2 = max(start.y.toLong(), end.y.toLong()),
		z1 = min(start.z.toLong(), end.z.toLong()), z2 = max(start.z.toLong(), end.z.toLong()),
	)

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

	fun points() = (x1..x2).flatMap { x ->
		(y1..y2).flatMap { y ->
			(z1..z2).map { z ->
				Point3D(x.toInt(), y.toInt(), z.toInt())
			}
		}
	}
}