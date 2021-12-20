package ch.mikewong.adventofcode.models

import ch.mikewong.adventofcode.util.asInts
import kotlin.math.pow
import kotlin.math.sqrt

data class Point3D(val x: Int, val y: Int, val z: Int) {

	companion object {
		fun fromString(input: String): Point3D {
			val parts = input.split(",").asInts()
			return Point3D(parts[0], parts[1], parts[2])
		}
	}

	val permutations by lazy {
		listOf(
			Point3D(x, y, z),
			Point3D(y, z, x),
			Point3D(z, x, y),
			Point3D(-x, z, y),
			Point3D(z, y, -x),
			Point3D(y, -x, z),
			Point3D(x, z, -y),
			Point3D(z, -y, x),
			Point3D(-y, x, z),
			Point3D(x, -z, y),
			Point3D(-z, y, x),
			Point3D(y, x, -z),
			Point3D(-x, -y, z),
			Point3D(-y, z, -x),
			Point3D(z, -x, -y),
			Point3D(-x, y, -z),
			Point3D(y, -z, -x),
			Point3D(-z, -x, y),
			Point3D(x, -y, -z),
			Point3D(-y, -z, x),
			Point3D(-z, x, -y),
			Point3D(-x, -z, -y),
			Point3D(-z, -y, -x),
			Point3D(-y, -x, -z),
		)
	}

	override fun toString(): String {
		return "($x,$y,$z)"
	}

	fun distanceTo(other: Point3D): Double {
		return sqrt((x - other.x).pow(2) + (y - other.y).pow(2) + (z - other.z).pow(2))
	}

	private fun Int.pow(n: Int) = this.toDouble().pow(n)

}
