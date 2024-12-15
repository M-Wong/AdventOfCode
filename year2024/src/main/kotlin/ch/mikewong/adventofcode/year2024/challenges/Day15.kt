package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toGrid
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day15 : Day<Long, Long>(2024, 15, "Warehouse Woes") {

	private val map by lazy {
		inputGroups.first().toGrid { _, c ->
			TileType.entries.firstOrNull { it.char == c } ?: TileType.EMPTY
		}
	}

	private val instructions by lazy {
		inputGroups.last().flatMap { line ->
			line.mapNotNull { c ->
				when (c) {
					'^' -> Direction.NORTH
					'v' -> Direction.SOUTH
					'>' -> Direction.EAST
					'<' -> Direction.WEST
					else -> null
				}
			}
		}
	}

	// 1492518
	override fun partOne(): Long {
		val currentMap = map.toMutableMap()
		var robotPosition = currentMap.filterValues { it == TileType.ROBOT }.keys.single()

		instructions.forEach { direction ->
			val didRobotMove = currentMap.moveRobot(robotPosition, direction)
			if (didRobotMove) {
				robotPosition = robotPosition.move(direction)
			}
		}

		return currentMap.filterValues { it == TileType.SINGLE_BOX }
			.map { (point, _) -> (100 * point.x) + point.y }
			.sum()
	}

	// 1512860
	override fun partTwo(): Long {
		val currentMap = map.expand().toMutableMap()
		var robotPosition = currentMap.filterValues { it == TileType.ROBOT }.keys.single()

		instructions.forEach { direction ->
			val didRobotMove = currentMap.moveRobot(robotPosition, direction)
			if (didRobotMove) {
				robotPosition = robotPosition.move(direction)
			}
		}

		return currentMap.filterValues { it == TileType.BOX_LEFT }
			.map { (point, _) -> (100 * point.x) + point.y }
			.sum()
	}

	/**
	 * Tries to move the [robot] in a certain [direction], moving all other affected tiles as well.
	 *
	 * @return True if the [robot] did move in the [direction], false otherwise
	 */
	private fun MutableMap<Point, TileType>.moveRobot(robot: Point, direction: Direction): Boolean {
		val affectedTiles = mutableMapOf<Point, Boolean>()
		val canMove = this.canMove(robot, direction, affectedTiles)
		if (canMove) {
			// Move all affected tiles, if the robot can move
			affectedTiles.keys.forEach { point ->
				val next = point.move(direction)
				this.swap(point, next)
			}
		}

		return canMove
	}

	/**
	 * Recursively checks if a [tile] can move in a certain [direction].
	 * The [affectedTiles] map contains tiles that were already checked (e.g. left/right side of boxes) and their results
	 *
	 * @return true if the [tile] can move in [direction], false otherwise
	 */
	private fun MutableMap<Point, TileType>.canMove(tile: Point, direction: Direction, affectedTiles: MutableMap<Point, Boolean>): Boolean {
		return affectedTiles.getOrPut(tile) {
			// Check the next tile that should be moved to
			val next = tile.move(direction)
			val nextTile = this.getValue(next)

			when (nextTile) {
				TileType.ROBOT -> throw IllegalArgumentException("Robot should never be the target for a move")
				TileType.WALL -> false // Next tile is a wall, cannot move there
				TileType.EMPTY -> true // Next tile is empty, can move there
				TileType.SINGLE_BOX -> canMove(next, direction, affectedTiles) // Next tile is a single box, check if that one can move too
				TileType.BOX_LEFT -> {
					when (direction) {
						// Next tile is left side of a box, if we're moving to the left, only check if the left side can move too (since the right side can anyway move)
						Direction.WEST -> canMove(next, direction, affectedTiles)

						// Next tile is left side of a box, and we're not moving to the left, need to check if both sides of the box can move
						else -> canMove(next, direction, affectedTiles) && canMove(next.move(Direction.EAST), direction, affectedTiles)
					}
				}
				TileType.BOX_RIGHT -> {
					when (direction) {
						// Next tile is right side of a box, if we're moving to the right, only check if the right side can move too (since the left side can anyway move)
						Direction.EAST -> canMove(next, direction, affectedTiles)

						// Next tile is right side of a box, and we're not moving to the right, need to check if both sides of the box can move
						else -> canMove(next, direction, affectedTiles) && canMove(next.move(Direction.WEST), direction, affectedTiles)
					}
				}
			}
		}
	}

	/**
	 * Swaps two tiles in the map
	 */
	private fun MutableMap<Point, TileType>.swap(from: Point, to: Point) {
		val fromTile = this.getValue(from)
		val toTile = this.getValue(to)
		this[from] = toTile
		this[to] = fromTile
	}

	/**
	 * Expands the map in width, so that each tile is twice as wide as before.
	 * ROBOT -> [ROBOT EMPTY]
	 * WALL -> [WALL WALL]
	 * EMPTY -> [EMPTY EMPTY]
	 * SINGLE_BOX -> [BOX_LEFT BOX_RIGHT]
	 */
	private fun Map<Point, TileType>.expand(): Map<Point, TileType> {
		return this.flatMap { (point, type) ->
			val left = point.copy(y = point.y * 2)
			val right = left.move(Direction.EAST)
			when (type) {
				TileType.ROBOT -> listOf(
					left to TileType.ROBOT,
					right to TileType.EMPTY,
				)
				TileType.WALL -> listOf(
					left to TileType.WALL,
					right to TileType.WALL,
				)
				TileType.EMPTY -> listOf(
					left to TileType.EMPTY,
					right to TileType.EMPTY,
				)
				TileType.SINGLE_BOX -> listOf(
					left to TileType.BOX_LEFT,
					right to TileType.BOX_RIGHT,
				)
				TileType.BOX_LEFT -> throw IllegalStateException("Map contains BOX_LEFT, so it is already expanded")
				TileType.BOX_RIGHT -> throw IllegalStateException("Map contains BOX_RIGHT, so it is already expanded")
			}
		}.toMap()
	}

	enum class TileType(val char: Char) {
		ROBOT('@'),
		WALL('#'),
		EMPTY('.'),
		SINGLE_BOX('O'),
		BOX_LEFT('['),
		BOX_RIGHT(']'),
	}

}