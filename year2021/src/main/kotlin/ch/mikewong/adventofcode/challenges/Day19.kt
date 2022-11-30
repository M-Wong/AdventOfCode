package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.models.Point3D
import ch.mikewong.adventofcode.util.keysIntersectingByValue

class Day19 : Day<Int, Int>(19, "Beacon Scanner") {

	private val zeroPoint = Point3D(0, 0, 0)
	private val scanners by lazy {
		inputGroups.mapIndexed { i, group ->
			Scanner(i, group.drop(1).map { Point3D.fromString(it) }, zeroPoint)
		}
	}

	private val resolvedScanners by lazy { resolveScanners() }

	override fun partOne() = resolvedScanners.flatMap { it.beacons }.distinct().count()

	override fun partTwo(): Int {
		val resolvedScannerPositions = resolvedScanners.map { it.position }
		return resolvedScannerPositions.maxOf { a ->
			resolvedScannerPositions.maxOf { b -> a.manhattanDistanceTo(b) }
		}
	}

	private fun resolveScanners(): List<Scanner> {
		// Begin with the first scanner which is the reference (already "solved") scanner
		val scannersToCheck = mutableListOf(scanners[0])
		val resolvedScanners = mutableListOf(scanners[0])

		while (scannersToCheck.isNotEmpty()) {
			val scannerToCheck = scannersToCheck.removeFirst()

			// Get the unresolved scanners
			val unresolvedScanners = scanners.filterNot { s -> s.index in resolvedScanners.map { it.index } }
			unresolvedScanners.forEach { unresolvedScanner ->
				// Try to resolve the current scanner by checking if it intersects with the reference scanner
				val resolvedScanner = unresolvedScanner.resolveWith(scannerToCheck)

				if (resolvedScanner != null) {
					scannersToCheck += resolvedScanner
					resolvedScanners += resolvedScanner
				}
			}
		}

		return resolvedScanners
	}

	private fun Scanner.resolveWith(reference: Scanner): Scanner? {
		rotations.forEach { rotation ->
			// For each possible rotation, adjust the scanner beacons with the rotation transformation
			val adjustedBeacons = this.beacons.map { rotation.invoke(it) }

			// If there is a valid delta between those beacons, return the delta-adjusted scanner
			val delta = findDelta(adjustedBeacons, reference.beacons)
			if (delta != null) {
				return this.copy(beacons = adjustedBeacons.map { it - delta }, position = delta)
			}
		}

		return null
	}

	private fun findDelta(beacons: List<Point3D>, referenceBeacons: List<Point3D>): Point3D? {
		// Iterate each beacon and each reference beacon
		beacons.forEach { beacon ->
			referenceBeacons.forEach { referenceBeacon ->
				// Then calculate the delta between those two beacons
				val delta = beacon - referenceBeacon

				// Check if there are 12 or more beacons that match reference beacons with the given delta
				val count = beacons.count { beaconToAdjust -> beaconToAdjust - delta in referenceBeacons }
				if (count >= 12) {
					return delta
				}
			}
		}
		return null
	}

	private data class Scanner(val index: Int, val beacons: List<Point3D>, val position: Point3D)

	private val rotations = listOf<(Point3D) -> Point3D>(
		{ p -> Point3D(p.x, p.y, p.z) },
		{ p -> Point3D(p.y, p.z, p.x) },
		{ p -> Point3D(p.z, p.x, p.y) },
		{ p -> Point3D(-p.x, p.z, p.y) },
		{ p -> Point3D(p.z, p.y, -p.x) },
		{ p -> Point3D(p.y, -p.x, p.z) },
		{ p -> Point3D(p.x, p.z, -p.y) },
		{ p -> Point3D(p.z, -p.y, p.x) },
		{ p -> Point3D(-p.y, p.x, p.z) },
		{ p -> Point3D(p.x, -p.z, p.y) },
		{ p -> Point3D(-p.z, p.y, p.x) },
		{ p -> Point3D(p.y, p.x, -p.z) },
		{ p -> Point3D(-p.x, -p.y, p.z) },
		{ p -> Point3D(-p.y, p.z, -p.x) },
		{ p -> Point3D(p.z, -p.x, -p.y) },
		{ p -> Point3D(-p.x, p.y, -p.z) },
		{ p -> Point3D(p.y, -p.z, -p.x) },
		{ p -> Point3D(-p.z, -p.x, p.y) },
		{ p -> Point3D(p.x, -p.y, -p.z) },
		{ p -> Point3D(-p.y, -p.z, p.x) },
		{ p -> Point3D(-p.z, p.x, -p.y) },
		{ p -> Point3D(-p.x, -p.z, -p.y) },
		{ p -> Point3D(-p.z, -p.y, -p.x) },
		{ p -> Point3D(-p.y, -p.x, -p.z) },
	)

}