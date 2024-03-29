package ch.mikewong.adventofcode.common.models

import ch.mikewong.adventofcode.common.extensions.asInts
import ch.mikewong.adventofcode.common.extensions.pow
import kotlin.math.abs
import kotlin.math.sqrt

data class Point3D(val x: Int, val y: Int, val z: Int) {

	companion object {
		fun fromString(input: String): Point3D {
			val parts = input.split(",").asInts()
			return Point3D(parts[0], parts[1], parts[2])
		}
	}

	operator fun plus(other: Point3D) = Point3D(x + other.x, y + other.y, z + other.z)

	operator fun minus(other: Point3D) = Point3D(x - other.x, y - other.y, z - other.z)

	override fun toString(): String {
		return "($x,$y,$z)"
	}

	fun distanceTo(other: Point3D): Double {
		return sqrt((x - other.x).pow(2) + (y - other.y).pow(2) + (z - other.z).pow(2))
	}

	fun manhattanDistanceTo(other: Point3D): Int {
		return abs(other.x - x) + abs(other.y - y) + abs(other.z - z)
	}

	fun adjacent() = setOf(
		Point3D(x + 1, y, z),
		Point3D(x - 1, y, z),
		Point3D(x, y + 1, z),
		Point3D(x, y - 1, z),
		Point3D(x, y, z + 1),
		Point3D(x, y, z - 1),
	)

}
