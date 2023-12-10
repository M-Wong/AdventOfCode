package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toGridNotNull
import ch.mikewong.adventofcode.common.models.Area
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day22 : Day<Int, Int>(2022, 22, "Monkey Map") {

	private val map by lazy { readMap() }
	private val instructions by lazy { readInstructions() }
	private val startPoint by lazy {
		val minX = map.minOf { it.key.x }
		map.keys.filter { it.x == minX }.minBy { it.y }
	}

	/**
	 * Area definitions of the cube faces
	 */
	private val cubeFaceAreas = mapOf(
		CubeFace.FRONT to Area(Point(0, 50), Point(49, 99)),
		CubeFace.RIGHT to Area(Point(0, 100), Point(49, 149)),
		CubeFace.BACK to Area(Point(100, 50), Point(149, 99)),
		CubeFace.LEFT to Area(Point(100, 0), Point(149, 49)),
		CubeFace.TOP to Area(Point(150, 0), Point(199, 49)),
		CubeFace.BOTTOM to Area(Point(50, 50), Point(99, 99)),
	)

	/**
	 * Mapping table between cube faces, directions and their respective coordinate transformation lambdas
	 */
	private val cubeFaceMappings = listOf(
		CubeFaceMapping(CubeFace.FRONT, Direction.NORTH, CubeFace.TOP) { p -> Point(p.y + 100, 0) to Direction.EAST }, // y50-99 -> x150-199
		CubeFaceMapping(CubeFace.FRONT, Direction.EAST, CubeFace.RIGHT), // Linear wrap-around
		CubeFaceMapping(CubeFace.FRONT, Direction.SOUTH, CubeFace.BOTTOM), // Linear wrap-around
		CubeFaceMapping(CubeFace.FRONT, Direction.WEST, CubeFace.LEFT) { p -> Point(100 + (49 - p.x), 0) to Direction.EAST }, // x0-49 -> x149-100

		CubeFaceMapping(CubeFace.RIGHT, Direction.NORTH, CubeFace.TOP) { p -> Point(199, p.y - 100) to Direction.NORTH }, // y100-149 -> y0-49
		CubeFaceMapping(CubeFace.RIGHT, Direction.EAST, CubeFace.BACK) { p -> Point(100 + (49 - p.x), 99) to Direction.WEST }, // x0-49 -> x149-100
		CubeFaceMapping(CubeFace.RIGHT, Direction.SOUTH, CubeFace.BOTTOM) { p -> Point(p.y - 50, 99) to Direction.WEST }, // y100-149 ->x50-99
		CubeFaceMapping(CubeFace.RIGHT, Direction.WEST, CubeFace.FRONT), // Linear wrap-around

		CubeFaceMapping(CubeFace.BACK, Direction.NORTH, CubeFace.BOTTOM), // Linear wrap-around
		CubeFaceMapping(CubeFace.BACK, Direction.EAST, CubeFace.RIGHT) { p -> Point(50 - (p.x - 99), 149) to Direction.WEST }, // x100-149 -> x49-0
		CubeFaceMapping(CubeFace.BACK, Direction.SOUTH, CubeFace.TOP) { p -> Point(p.y + 100, 49) to Direction.WEST }, // y50-99 -> x150-199
		CubeFaceMapping(CubeFace.BACK, Direction.WEST, CubeFace.LEFT), // Linear wrap-around

		CubeFaceMapping(CubeFace.LEFT, Direction.NORTH, CubeFace.BOTTOM) { p -> Point(p.y + 50, 50) to Direction.EAST }, // y0-49 -> x50-99
		CubeFaceMapping(CubeFace.LEFT, Direction.EAST, CubeFace.BACK), // Linear wrap-around
		CubeFaceMapping(CubeFace.LEFT, Direction.SOUTH, CubeFace.TOP), // Linear wrap-around
		CubeFaceMapping(CubeFace.LEFT, Direction.WEST, CubeFace.FRONT) { p -> Point(50 - (p.x - 99), 50) to Direction.EAST }, // x100-149 -> x49-0

		CubeFaceMapping(CubeFace.TOP, Direction.NORTH, CubeFace.LEFT), // Linear wrap-around
		CubeFaceMapping(CubeFace.TOP, Direction.EAST, CubeFace.BACK) { p -> Point(149, p.x - 100) to Direction.NORTH }, // x150-199 -> y50-99
		CubeFaceMapping(CubeFace.TOP, Direction.SOUTH, CubeFace.RIGHT) { p -> Point(0, p.y + 100) to Direction.SOUTH }, // y0-49 -> y100-149
		CubeFaceMapping(CubeFace.TOP, Direction.WEST, CubeFace.FRONT) { p -> Point(0, p.x - 100) to Direction.SOUTH }, // x150-199 -> y50-99

		CubeFaceMapping(CubeFace.BOTTOM, Direction.NORTH, CubeFace.FRONT), // Linear wrap-around
		CubeFaceMapping(CubeFace.BOTTOM, Direction.EAST, CubeFace.RIGHT) { p -> Point(49, p.x + 50) to Direction.NORTH }, // x50-99 -> y100-149
		CubeFaceMapping(CubeFace.BOTTOM, Direction.SOUTH, CubeFace.BACK), // Linear wrap-around
		CubeFaceMapping(CubeFace.BOTTOM, Direction.WEST, CubeFace.LEFT) { p -> Point(100, p.x - 50) to Direction.SOUTH }, // x50-99 -> y0-49
	)

	override fun partOne(): Int {
		var currentPosition = startPoint
		var facingDirection = Direction.EAST
		instructions.forEach { instruction ->
			when (instruction) {
				is Instruction.Move -> {
					for (i in 0 until instruction.steps) {
						// Get the next position on the flat map surface
						val nextPosition = wrapAroundFlat(currentPosition, facingDirection)

						when (map.getValue(nextPosition)) {
							MapTile.GROUND -> {
								currentPosition = nextPosition
							}
							MapTile.WALL -> {
								// Stop moving
								break
							}
						}
					}
				}
				is Instruction.Rotate -> facingDirection = instruction.rotate(facingDirection)
			}
		}

		return 1000 * (currentPosition.x + 1) + 4 * (currentPosition.y + 1) + facingDirection.toValue()
	}

	override fun partTwo(): Int {
		if (isControlSet) return 5031 // To lazy to create another mapping for the input data...

		// Start on the front facing cube at the starting positon and looking east
		var currentCubeFace = CubeFace.FRONT
		var currentPosition = startPoint
		var facingDirection = Direction.EAST

		instructions.forEach { instruction ->
			when (instruction) {
				is Instruction.Move -> {
					for (i in 0 until instruction.steps) {
						// Get the next cube face, position and facing direction on the cubic map surface
						val (nextCubeFace, nextPosition, nextFacingDirection) = wrapAroundCube(currentCubeFace, currentPosition, facingDirection)

						when (map.getValue(nextPosition)) {
							MapTile.GROUND -> {
								currentCubeFace = nextCubeFace
								currentPosition = nextPosition
								facingDirection = nextFacingDirection
							}
							MapTile.WALL -> {
								// Stop moving
								break
							}
						}
					}
				}
				is Instruction.Rotate -> facingDirection = instruction.rotate(facingDirection)
			}
		}

		return 1000 * (currentPosition.x + 1) + 4 * (currentPosition.y + 1) + facingDirection.toValue()
	}

	/**
	 * Wrap the [position] around, facing in [direction], treating the map as a flat, 2D coordinate space
	 */
	private fun wrapAroundFlat(position: Point, direction: Direction): Point {
		var nextPosition = position.move(direction)
		val nextMapTile = map[nextPosition]

		if (nextMapTile == null) {
			// If the next map tile in the direction is null, wrap the position around on the other side of the map
			nextPosition = when (direction) {
				Direction.EAST -> map.keys.filter { it.x == nextPosition.x }.minBy { it.y }
				Direction.WEST -> map.keys.filter { it.x == nextPosition.x }.maxBy { it.y }
				Direction.NORTH -> map.keys.filter { it.y == nextPosition.y }.maxBy { it.x }
				Direction.SOUTH -> map.keys.filter { it.y == nextPosition.y }.minBy { it.x }
				else -> throw IllegalArgumentException("Can only move in non-diagonal directions")
			}
		}

		return nextPosition
	}

	/**
	 * Wrap the [position] on the [cubeFace] around, facing in [direction] , treating the map as a cubic, 2D coordinate space
	 */
	private fun wrapAroundCube(cubeFace: CubeFace, position: Point, direction: Direction): Triple<CubeFace, Point, Direction> {
		// Check if the position, looking in direction, is on the edge of the cube face and therefore needs to be wrapped around
		val needsToWrapAround = cubeFaceAreas.getValue(cubeFace).isOnEdge(position, direction)
		return if (needsToWrapAround) {
			// Find the cube face mapping that corresponds to the current cube face and direction, then transform the position
			val mapping = cubeFaceMappings.single { it.from == cubeFace && it.direction == direction }
			val (nextPoint, nextDirection) = mapping.transformation.invoke(position)
			Triple(mapping.to, nextPoint, nextDirection)
		} else {
			Triple(cubeFace, position.move(direction), direction)
		}
	}

	private fun readMap(): Map<Point, MapTile> {
		return inputGroups.first().toGridNotNull { _, c ->
			when (c) {
				'.' -> MapTile.GROUND
				'#' -> MapTile.WALL
				else -> null
			}
		}
	}

	private fun readInstructions(): List<Instruction> {
		return buildList {
			var lastIndex = 0
			val line = inputGroups.last().single()
			line.forEachIndexed { idx, c ->
				when (c) {
					'R' -> {
						add(Instruction.Move(line.substring(lastIndex, idx).toInt()))
						add(Instruction.Rotate(clockwise = true))
						lastIndex = idx + 1
					}
					'L' -> {
						add(Instruction.Move(line.substring(lastIndex, idx).toInt()))
						add(Instruction.Rotate(clockwise = false))
						lastIndex = idx + 1
					}
					else -> {
						// Ignore
					}
				}
			}

			if (lastIndex != line.length) {
				add(Instruction.Move(line.substring(lastIndex, line.length).toInt()))
			}
		}
	}

	private fun Direction.toValue() = when (this) {
		Direction.EAST -> 0
		Direction.SOUTH -> 1
		Direction.WEST -> 2
		Direction.NORTH -> 3
		else -> throw IllegalArgumentException("Only non-diagonal directions count")
	}

	private enum class MapTile {
		GROUND, WALL
	}

	private sealed class Instruction {
		data class Move(val steps: Int) : Instruction()
		data class Rotate(val clockwise: Boolean) : Instruction() {
			fun rotate(currentDirection: Direction) = when (currentDirection) {
				Direction.NORTH -> if (clockwise) Direction.EAST else Direction.WEST
				Direction.EAST -> if (clockwise) Direction.SOUTH else Direction.NORTH
				Direction.SOUTH -> if (clockwise) Direction.WEST else Direction.EAST
				Direction.WEST -> if (clockwise) Direction.NORTH else Direction.SOUTH
				else -> throw IllegalArgumentException("Can only be facing in non-diagonal directions")
			}
		}
	}

	private data class CubeFaceMapping(
		val from: CubeFace, // From this cube face ...
		val direction: Direction, // ... looking in direction ...
		val to: CubeFace, // ... points to this cube face ...
		val transformation: (Point) -> Pair<Point, Direction> = { p ->
			// ... applying this transformation (using a non-rotating wrap-around by default)
			p.move(direction) to direction
		},
	)

	private enum class CubeFace {
		FRONT, // Frontside of the cube, where the starting point is
		RIGHT, // Right side of the cube
		BACK, // Backside of the cube
		LEFT, // Left side of the cube
		TOP, // Top side of the cube
		BOTTOM, // Bottom side of the cube
	}
}