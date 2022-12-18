package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Point3D

class Day18 : Day<Int, Int>(2022, 18, "Boiling Boulders") {

	override fun partOne(): Int {
		val lavaDroplets = mutableSetOf<Point3D>()
		var exposedSides = 0

		inputLines.forEach { line ->
			val point = Point3D.fromString(line)

			// Get directly adjacent points. If any of those is already contained in the set of lava droplets, subtract two exposed sides (one from the existing, and one from the new)
			val adjacent = point.adjacent()
			val connectedSides = adjacent.count { lavaDroplets.contains(it) }
			exposedSides -= connectedSides * 2

			// Then add the lava droplet to the set and add it's 6 sides as exposed
			lavaDroplets.add(point)
			exposedSides += 6
		}

		return exposedSides
	}

	override fun partTwo(): Int {
		val lavaDroplets = inputLines.map { Point3D.fromString(it) }
		var fullyExposedSides = 0

		// Keep track of points that we know are not exposed
		val nonExposedPoints = mutableSetOf<Point3D>()

		lavaDroplets.forEach { droplet ->
			val sides = droplet.adjacent()

			// Check each side of the lava droplet if it is exposed
			sides.forEach { side ->
				val pointsToCheck = mutableSetOf(side)

				if (!lavaDroplets.contains(side)) {
					while (pointsToCheck.isNotEmpty()) {
						val point = pointsToCheck.first()
						pointsToCheck.remove(point)

						// If the current point is a lava droplet, remove all its adjacent points from the queue and continue the check
						if (lavaDroplets.contains(point)) {
							pointsToCheck.removeAll(point.adjacent())
							continue
						}

						// Check if this point has direct access to free air (aka there are no droplets in any direction)
						val hasAccessToFreeAir = lavaDroplets.none { it.x > point.x && it.y == point.y && it.z == point.z }
								|| lavaDroplets.none { it.x < point.x && it.y == point.y && it.z == point.z }
								|| lavaDroplets.none { it.x == point.x && it.y > point.y && it.z == point.z }
								|| lavaDroplets.none { it.x == point.x && it.y < point.y && it.z == point.z }
								|| lavaDroplets.none { it.x == point.x && it.y == point.y && it.z > point.z }
								|| lavaDroplets.none { it.x == point.x && it.y == point.y && it.z < point.z }

						if (hasAccessToFreeAir) {
							// If it has direct access to free air, count the side as exposed and break the loop
							fullyExposedSides++
							break
						} else {
							// Otherwise add all its adjacent points (minus those we already know not to be exposed) to the queue
							pointsToCheck.addAll(point.adjacent().minus(nonExposedPoints))
							nonExposedPoints.add(point)
						}
					}
				}
			}
		}

		return fullyExposedSides
	}
}