package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.algorithms.dijkstra
import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toGridNotNull
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.PointWithDirection

class Day16 : Day<Int, Int>(2024, 16, "Reindeer Maze") {

	private val walls by lazy { inputLines.toGridNotNull { _, c -> c.takeIf { it == '#' } } }
	private val start by lazy { inputLines.toGridNotNull { _, c -> c.takeIf { it == 'S' } }.keys.single() }
	private val end by lazy { inputLines.toGridNotNull { _, c -> c.takeIf { it == 'E' } }.keys.single() }

	// 85432
	override fun partOne(): Int {
		return findShortestPath().totalCost
	}

	// 465
	override fun partTwo(): Int {
		return findShortestPath().paths
			.flatMap { path -> path.map { it.point } }
			.distinct()
			.size
	}

	private fun findShortestPath() = dijkstra(
		startingNode = PointWithDirection(start, Direction.EAST),
		isTargetNode = { it.point == end },
		neighbours = { current ->
			// Possible neighbours are all the points in lateral directions, except backwards that are not walls
			Direction.lateral()
				.minus(current.direction.opposite())
				.map { direction -> PointWithDirection(current.point.move(direction), direction) }
				.filter { it.point !in walls }
		},
		costFunction = { current, next ->
			// The cost of a move is 1 if the direction is the same, otherwise 1001 (because turning is 1000 and moving is 1)
			if (current.direction == next.direction) 1 else 1001
		},
		findAllShortestPaths = true
	)

}