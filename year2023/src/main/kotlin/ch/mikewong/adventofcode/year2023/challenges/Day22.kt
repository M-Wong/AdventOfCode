package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.overlapsWith
import ch.mikewong.adventofcode.common.models.Point3D
import kotlin.math.max
import kotlin.math.min

class Day22 : Day<Int, Int>(2023, 22, "Sand Slabs") {

	private val brickSnapshot = inputLines.mapIndexed { idx, line ->
		val (start, end) = line.split("~")
		Brick(idx, Point3D.fromString(start), Point3D.fromString(end))
	}.sortedBy { it.zRange.first }

	private val restingBricks = getRestingBricks()

	override fun partOne(): Int {
		return restingBricks.values.count { (brick, _, supports) ->
			// Count bricks that either don't support any other bricks
			val supportsNoBricks = supports.isEmpty()

			// ... or are not the only support bricks for all the bricks they support
			val isNotOnlySupportBrick = supports.map { restingBricks.getValue(it) }.all { it.supportedBy != listOf(brick.id) }
			supportsNoBricks || isNotOnlySupportBrick
		}
	}

	override fun partTwo(): Int {
		return restingBricks.values.sumOf { (brick, _, supports) ->
			// Keep track of which bricks disintegrating this brick would cause to fall
			val causesBricksToFall = mutableSetOf<Int>()
			val supportingQueue = supports.toMutableList()
			while (supportingQueue.isNotEmpty()) {
				// Check the next brick if the current brick is its only support or if all of its support bricks were already caused to fall
				val nextBrickId = supportingQueue.removeFirst()
				val nextBrick = restingBricks.getValue(nextBrickId)
				if (nextBrick.supportedBy == listOf(brick.id) || nextBrick.supportedBy.all { causesBricksToFall.contains(it) }) {
					causesBricksToFall.add(nextBrick.brick.id)
				}
				supportingQueue.addAll(nextBrick.supports)
			}
			causesBricksToFall.size
		}
	}

	private fun getRestingBricks(): Map<Int, RestingBrick> {
		// Keep track of all bricks that are already resting, as well as which bricks are supported by and support which other bricks
		val restingBricks = mutableListOf<Brick>()
		val brickSupportedBy = brickSnapshot.associate { it.id to mutableListOf<Int>() }
		val brickSupports = brickSnapshot.associate { it.id to mutableListOf<Int>() }

		val fallingBricks = brickSnapshot.toMutableList()
		while (fallingBricks.isNotEmpty()) {
			val fallingBrick = fallingBricks.removeFirst()

			// Find the z level this brick comes to a rest and the bricks that support it
			var restingZ = fallingBrick.zRange.first
			val supportedByBrickIds = mutableListOf<Int>()

			while (restingZ > 1 && supportedByBrickIds.isEmpty()) {
				val nextZ = restingZ - 1
				val supportingBricks = restingBricks.filter { restingBrick ->
					// Filter resting bricks that are on the correct Z level
					restingBrick.zRange.last == nextZ
				}.filter { restingBrick ->
					// Filter resting bricks that have an overlap in the x and y coordinates
					fallingBrick.xRange.overlapsWith(restingBrick.xRange) && fallingBrick.yRange.overlapsWith(restingBrick.yRange)
				}

				if (supportingBricks.isEmpty()) {
					// If there are no supporting bricks found on this z level, move down one level
					restingZ--
				} else {
					// Otherwise add all of them to the support structure
					supportedByBrickIds.addAll(supportingBricks.map { it.id })
					supportingBricks.forEach { brickSupports.getValue(it.id).add(fallingBrick.id) }
				}
			}

			brickSupportedBy.getValue(fallingBrick.id).addAll(supportedByBrickIds)
			restingBricks.add(fallingBrick.moveDown(restingZ))
		}

		return restingBricks.associate {
			it.id to RestingBrick(it, brickSupportedBy.getValue(it.id), brickSupports.getValue(it.id))
		}
	}

	private data class Brick(val id: Int, val start: Point3D, val end: Point3D) {
		val xRange = min(start.x, end.x)..max(start.x, end.x)
		val yRange = min(start.y, end.y)..max(start.y, end.y)
		val zRange = min(start.z, end.z)..max(start.z, end.z)

		fun moveDown(newZ: Int): Brick {
			val delta = this.start.z - newZ
			return Brick(
				id = id,
				start = start.copy(z = newZ),
				end = end.copy(z = end.z - delta),
			)
		}
	}

	private data class RestingBrick(
		/** The actual brick in its final resting position */
		val brick: Brick,

		/** The brick IDs that are supported by this brick (e.g. all bricks in this list rests on top of this brick) */
		val supportedBy: List<Int>,

		/** The brick IDs that support this brick (e.g. this brick rests on top of all bricks in this list) */
		val supports: List<Int>,
	)

}