package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.models.Point3D
import ch.mikewong.adventofcode.util.addTogether
import ch.mikewong.adventofcode.util.keysIntersectingByValue
import ch.mikewong.adventofcode.util.longCount

class Day19 : Day<Int, Int>(19, "Beacon Scanner") {

	private val zeroPoint = Point3D(0, 0, 0)
	private val scanners by lazy {
		inputGroups.mapIndexed { i, group ->
			Scanner(i, group.drop(1).map { Point3D.fromString(it) })
		}
	}

	override fun partOne(): Int {
		val relativeScannerPositions = Array<Point3D?>(scanners.size) { null }
		relativeScannerPositions[0] = zeroPoint // The first scanner is the relative zero point

		var beaconCount = scanners.sumOf { it.beacons.count() }
		val overlaps = Array(scanners.size) { Array(scanners.size) { emptyList<Pair<Point3D, Point3D>>() } }
		val overlapStack = ArrayDeque<Pair<Int, Int>>()

		scanners.forEach { scannerA ->
			scanners.drop(scannerA.index + 1).forEach { scannerB ->
				// Intersect the beacon distances from scanner A and scanner B
				val pointIntersectionByDistance = scannerA.beaconDistances.keysIntersectingByValue(scannerB.beaconDistances)
				val beaconPointsA = pointIntersectionByDistance.map { listOf(it.first.first, it.first.second) }.flatten().toSet()
				if (beaconPointsA.size >= 12) {
					val correspondingBeaconPoints = beaconPointsA.map { aKey ->
						val bKeys = pointIntersectionByDistance.filter { it.first.first == aKey || it.first.second == aKey }.map { it.second }
						val groupedBKeys = bKeys.longCount { it.first }.addTogether(bKeys.longCount { it.second })
						aKey to groupedBKeys.maxByOrNull { it.value }!!.key
					}



					overlaps[scannerA.index][scannerB.index] = correspondingBeaconPoints
					overlaps[scannerB.index][scannerA.index] = correspondingBeaconPoints.map { it.second to it.first }

					println("Scanner ${scannerA.index} intersects with scanner ${scannerB.index} at these points:")
					correspondingBeaconPoints.forEach {
						println("${it.first} to ${it.second}")
					}
					println()
				}
			}
		}

		val duplicateBeacons = Array<Set<Point3D>>(scanners.size) { emptySet() }
		scanners.forEach { scanner ->
			scanner.beacons.forEach { point ->
				overlaps[scanner.index].forEach { overlap ->
					if (overlap.map { it.first }.contains(point)) {
						beaconCount--
					}
				}



			}
		}


		return scanners.sumOf { it.beacons.count() } - duplicateBeacons.sumOf { it.count() }
	}

	private fun resolveScannerPositionRelativeTo(reference: Point3D, pointMapping: List<Pair<Point3D, Point3D>>): Point3D {
		return pointMapping.map {
			val (pA, pB) = it
			Point3D(
				pA.x + pB.x,
				pA.y - pB.y,
				pA.z + pB.z
			)
		}.distinct().first()
	}

	override fun partTwo(): Int {
		return 0
	}

	private data class Scanner(val index: Int, val beacons: List<Point3D>) {
		val beaconDistances = mutableMapOf<Pair<Point3D, Point3D>, Double>()

		init {
			beacons.forEachIndexed { i, from ->
				beacons.drop(i + 1).forEach { to ->
					beaconDistances[from to to] = from.distanceTo(to)
				}
			}
		}
	}

}