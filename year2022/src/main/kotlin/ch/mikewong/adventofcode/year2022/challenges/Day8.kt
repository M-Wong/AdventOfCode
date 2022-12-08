package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.product
import ch.mikewong.adventofcode.common.extensions.toIntGrid
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day8 : Day<Int, Int>(2022, 8, "Treetop Tree House") {

	private val directions = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
	private val treeGrid by lazy { inputLines.toIntGrid() }

	override fun partOne(): Int {
		val visibleTrees = mutableSetOf<Point>()

		// Iterate each tree and check if they can see to the edge of the grid in any direction
		treeGrid.forEach { (pos, height) ->
			val canSeeInAnyDirection = directions.any {
				var canSee = true

				// While the next tree height is not null, check if it is taller than the current tree height and if so, stop the
				// check because it cannot see to the edge in this direction
				var nextPos = pos.move(it)
				var nextTreeHeight = treeGrid[nextPos]
				while (nextTreeHeight != null) {
					if (nextTreeHeight >= height) {
						canSee = false
						break
					}
					nextPos = nextPos.move(it)
					nextTreeHeight = treeGrid[nextPos]
				}

				canSee
			}

			if (canSeeInAnyDirection) {
				visibleTrees.add(pos)
			}
		}

		return visibleTrees.size
	}

	override fun partTwo(): Int {
		var maxScenicScore = 0

		// Iterate each tree and calculate its viewing distance in all directions and calculate the scenic score
		treeGrid.forEach { (pos, height) ->
			val viewingDistance = directions.map {
				var count = 0

				// While the next tree height is not null and smaller than the current tree height, increase the count and move to the next tree
				var nextTreePos = pos.move(it)
				var nextTreeHeight = treeGrid[nextTreePos]
				while (nextTreeHeight != null && nextTreeHeight < height) {
					count++
					nextTreePos = nextTreePos.move(it)
					nextTreeHeight = treeGrid[nextTreePos]
				}

				// If the last tree was not null (meaning it wasn't the edge condition that broke the loop) increase the count one more time
				if (nextTreeHeight != null) {
					count++
				}

				count
			}

			val score = viewingDistance.product()
			if (score > maxScenicScore) {
				maxScenicScore = score
			}
		}

		return maxScenicScore
	}

}