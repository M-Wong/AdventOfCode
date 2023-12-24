package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allPairs
import ch.mikewong.adventofcode.common.extensions.asLongs
import kotlin.math.abs
import kotlin.math.roundToLong


class Day24 : Day<Int, Long>(2023, 24, "Never Tell Me The Odds") {

	private val hailstones = inputLines.map { line ->
		val (position, velocity) = line.split("@").map { it.trim().split(", ").asLongs() }
		Hailstone(
			LongPoint3D(position[0], position[1], position[2]),
			LongPoint3D(velocity[0], velocity[1], velocity[2]),
		)
	}

	override fun partOne(): Int {
		val (min, max) = if (isControlSet) 7L to 27L else 200000000000000 to 400000000000000

		val allPairs = hailstones.allPairs()
		val intersections = allPairs.map { (a, b) ->
			// Map each pair of hailstones to its 2d trajectory intersection
			a.intersects2DTrajectory(b)?.let { (x, y) ->
				// Check if the intersection lies within the bounds
				x > min && x < max && y > min && y < max
			} ?: false
		}

		return intersections.count { it }
	}

	override fun partTwo(): Long {
		if (isControlSet) {
			// The sample input has too few hailstones to properly determine the target velocity, so this brute forces possible velocities
			val rock = bruteForceRock()
			return with(requireNotNull(rock)) { origin.x + origin.y + origin.z }
		} else {
			// For the real input, there are enough hailstones to determine the target velocity based on hailstone pairs with the same x, y or z velocity
			// The solution is based on this: https://www.reddit.com/r/adventofcode/comments/18pnycy/comment/keqf8uq/
			val (rvx, rvy, rvz) = findTargetVelocity()

			val (originA, velocityA) = hailstones[0]
			val (apx, apy, apz) = originA
			val (avx, avy, avz) = velocityA

			val (originB, velocityB) = hailstones[1]
			val (bpx, bpy, _) = originB
			val (bvx, bvy, _) = velocityB

			val ma = (avy - rvy) / (avx - rvx.toDouble())
			val mb = (bvy - rvy) / (bvx - rvx.toDouble())
			val ca = apy - (ma * apx)
			val cb = bpy - (mb * bpx)
			val xPos = (cb - ca) / (ma - mb)
			val yPos = (ma * xPos + ca)
			val time = (xPos - apx) / (avx - rvx)
			val zPos = apz + (avz - rvz) * time

			return xPos.roundToLong() + yPos.roundToLong() + zPos.roundToLong()
		}
	}

	private fun findTargetVelocity(): LongPoint3D {
		var possibleVelocityX = emptySet<Long>()
		var possibleVelocityY = emptySet<Long>()
		var possibleVelocityZ = emptySet<Long>()

		// For all hailstone pairs, check if one of their velocity coordinates match, and if so calculate the possible speeds the rock has to go to hit them both
		// After all pairs are done, there should only be one possible speed left for each coordinate axis
		hailstones.allPairs().forEach { (a, b) ->
			if (a.velocity.x == b.velocity.x) {
				val diff = b.origin.x - a.origin.x
				val newX = (-1000L..1000L).mapNotNull { velocityX ->
					velocityX.takeIf { it != a.velocity.x && diff % (it - a.velocity.x) == 0L }
				}.toSet()

				possibleVelocityX = if (possibleVelocityX.isNotEmpty()) possibleVelocityX.intersect(newX) else newX
			}

			if (a.velocity.y == b.velocity.y) {
				val diff = b.origin.y - a.origin.y
				val newY = (-1000L..1000L).mapNotNull { velocityY ->
					velocityY.takeIf { it != a.velocity.y && diff % (it - a.velocity.y) == 0L }
				}.toSet()

				possibleVelocityY = if (possibleVelocityY.isNotEmpty()) possibleVelocityY.intersect(newY) else newY
			}

			if (a.velocity.z == b.velocity.z) {
				val diff = b.origin.z - a.origin.z
				val newZ = (-1000L..1000L).mapNotNull { velocityZ ->
					velocityZ.takeIf { it != a.velocity.z && diff % (it - a.velocity.z) == 0L }
				}.toSet()

				possibleVelocityZ = if (possibleVelocityZ.isNotEmpty()) possibleVelocityZ.intersect(newZ) else newZ
			}
		}

		return LongPoint3D(
			possibleVelocityX.single(),
			possibleVelocityY.single(),
			possibleVelocityZ.single(),
		)
	}

	private fun bruteForceRock(): Hailstone? {
		val initialVelocity = LongPoint3D(0, 0, 0)

		var rock: Hailstone? = null

		var i = 0L
		val triedVelocities = mutableSetOf<LongPoint3D>()
		while (rock == null && i < 1_000) {
			val adjustments = (-i..i).map { x ->
				(-i..i).map { y ->
					(-i..i).mapNotNull { z ->
						LongPoint3D(x, y, z)
					}
				}.flatten()
			}.flatten().toSet().minus(triedVelocities)

			triedVelocities.addAll(adjustments)

			val adjustedVelocities = adjustments.map { initialVelocity + it }
			for (velocity in adjustedVelocities) {
				val adjustedHailstones = hailstones.map { it.copy(velocity = it.velocity - velocity) }
				val commonPoint = adjustedHailstones.findCommonPoint()

				if (commonPoint != null) {
					rock = Hailstone(commonPoint, velocity)
					break
				}
			}

			i++
		}

		return rock
	}

	private fun List<Hailstone>.findCommonPoint(): LongPoint3D? {
		var commonPoint: LongPoint3D? = null
		this.allPairs().forEach { (a, b) ->
			val intersection = a.toVelocityLine().intersect3D(b.toVelocityLine())
			if (commonPoint == null) commonPoint = intersection
			if (intersection != commonPoint) return null
		}
		return commonPoint
	}

	private data class Hailstone(val origin: LongPoint3D, val velocity: LongPoint3D) {
		fun toLine() = Line(origin, origin + velocity)

		fun toVelocityLine() = Line(origin, velocity)

		fun intersects2DTrajectory(other: Hailstone): Pair<Double, Double>? {
			val intersection = this.toLine().intersect2D(other.toLine()) ?: return null
			val (x, y) = intersection

			// Check if the intersection is in the past for this hailstone
			if (this.isInPast(x, y)) return null

			// Check if the intersection is in the past for the other hailstone
			if (other.isInPast(x, y)) return null

			return intersection
		}

		private fun isInPast(x: Double, y: Double): Boolean {
			when {
				velocity.x > 0 -> if (x < origin.x) return true
				velocity.x < 0 -> if (x > origin.x) return true
				else -> if (x != origin.x.toDouble()) return true
			}

			when {
				velocity.y > 0 -> if (y < origin.y) return true
				velocity.y < 0 -> if (y > origin.y) return true
				else -> if (y != origin.y.toDouble()) return true
			}
			return false
		}
	}

	private data class LongPoint3D(val x: Long, val y: Long, val z: Long) {
		operator fun plus(other: LongPoint3D) = LongPoint3D(this.x + other.x, this.y + other.y, this.z + other.z)
		operator fun minus(other: LongPoint3D) = LongPoint3D(this.x - other.x, this.y - other.y, this.z - other.z)
	}

	private data class Line(val a: LongPoint3D, val b: LongPoint3D) {
		private val m = (b.y - a.y) / (b.x - a.x.toDouble())

		fun intersect2D(other: Line): Pair<Double, Double>? {
			if (this.m == other.m) {
				// Lines are parallel
				return null
			}

			val x = (this.m * this.a.x - other.m * other.a.x + other.a.y - this.a.y) / (this.m - other.m)
			val y = this.m * (x - this.a.x) + this.a.y

			return x to y
		}

		fun intersect3D(otherLine: Line): LongPoint3D? {
			val p1 = this.a
			val v1 = this.b
			val p2 = otherLine.a
			val v2 = otherLine.b

			val w0 = LongPoint3D(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z)

			val a = v1.dot(v1)
			val b = v1.dot(v2)
			val c = v2.dot(v2)
			val d = v1.dot(w0)
			val e = v2.dot(w0)

			val denominator = a * c - b * b

			// Lines are parallel if the denominator is close to zero
			if (abs(denominator) < 0.00001) {
				return null
			}

			val t = (b * e - c * d) / denominator

			return LongPoint3D(
				p1.x + t * v1.x,
				p1.y + t * v1.y,
				p1.z + t * v1.z
			)
		}

		fun LongPoint3D.dot(other: LongPoint3D): Long {
			return this.x * other.x + this.y * other.y + this.z * other.z
		}

	}

}