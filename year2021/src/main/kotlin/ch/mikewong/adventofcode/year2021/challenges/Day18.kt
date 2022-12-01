package ch.mikewong.adventofcode.year2021.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Node
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

class Day18 : Day<Int, Int>(2021, 18, "Snailfish") {

	override fun partOne(): Int {
		val snailfishNumbers = inputLines.map { parseSnailfishNumber(it) }

		// Reduce the first number
		var snailNumber = snailfishNumbers.first().reduce()

		// Then iterate the rest, reducing the next number, combining them into one and reducing the resulting number
		snailfishNumbers.drop(1).forEach {
			val nextNumber = it.reduce()
			val newRoot = combineNodes(snailNumber, nextNumber)
			snailNumber = newRoot.reduce()
		}

		return snailNumber.magnitude()
	}

	override fun partTwo(): Int {
		var maxMagnitude = Int.MIN_VALUE
		val size = inputLines.size

		// Iterate over all possible combinations of lines
		(0 until size).forEach { a ->
			((a + 1) until size).forEach { b ->
				// Calculate the maximum magnitude for each combination of AB and BA
				maxMagnitude = max(
					maxMagnitude,
					max(
						getMagnitudeForLines(a, b),
						getMagnitudeForLines(b, a)
					)
				)
			}
		}

		return maxMagnitude
	}

	private fun getMagnitudeForLines(a: Int, b: Int) : Int {
		// Need to parse it all over again because the reduce function operates in place
		val snailfishNumbers = inputLines.map { parseSnailfishNumber(it) }
		val numberA = snailfishNumbers[a]
		val numberB = snailfishNumbers[b]

		return combineNodes(numberA, numberB).reduce().magnitude()
	}

	private fun parseSnailfishNumber(line: String): Node<Int> {
		var index = 0
		var depth = 0
		var currentNode: Node<Int>? = null
		while (index < line.length) {
			when (val char = line[index]) {
				'[' -> {
					val nextNode = Node(currentNode, depth)
					currentNode = nextNode
					depth++
					index++
				}
				']' -> {
					val parentNode = currentNode?.parent
					if (parentNode?.left == null) {
						parentNode?.left = currentNode
					} else {
						parentNode.right = currentNode
					}

					if (parentNode != null) {
						currentNode = parentNode
					}
					depth--
					index++
				}
				',' -> {
					index++
				}
				else -> {
					if (currentNode?.left == null) {
						currentNode?.left = Node(currentNode, depth).apply { value = char.digitToInt() }
					} else {
						currentNode.right = Node(currentNode, depth).apply { value = char.digitToInt() }
					}
					index++
				}
			}
		}

		return currentNode!!
	}

	/**
	 * Combine two nodes returning the new root (parent of both nodes)
	 */
	private fun combineNodes(left: Node<Int>, right: Node<Int>): Node<Int> {
		val newRoot = Node<Int>(null, 0)
		newRoot.left = left.apply {
			parent = newRoot
			increaseDepth()
		}
		newRoot.right = right.apply {
			parent = newRoot
			increaseDepth()
		}
		return newRoot
	}

	private fun Node<Int>.reduce(): Node<Int> {
		val actionsStack = ArrayDeque<ReduceAction>()

		// Find the initial reduce actions
		this.findNextExplosion()?.let { actionsStack.add(it) }
		this.findNextSplit()?.let { actionsStack.add(it) }

		// While there are any reduce actions, execute the first on the stack
		while (actionsStack.isNotEmpty()) {
			val action = actionsStack.removeFirst()
			action.run()

			// Since the reduce actions operate in place, clear the stack in case there are any outdated actions
			actionsStack.clear()

			// Find the next reduce actions
			this.findNextExplosion()?.let { actionsStack.add(it) }
			this.findNextSplit()?.let { actionsStack.add(it) }
		}

		return this
	}

	private fun Node<Int>.findNextExplosion(): ReduceAction.Explode? {
		return this.findFirst { it.depth >= 4 && it.isLeafPair() }?.let { ReduceAction.Explode(it) }
	}

	private fun Node<Int>.findNextSplit(): ReduceAction.Split? {
		return this.findFirst { (it.value ?: 0) >= 10 }?.let { ReduceAction.Split(it) }
	}

	private fun Node<Int>.magnitude(): Int {
		val leftMagnitude = 3 * ((left?.value ?: left?.magnitude()) ?: 0)
		val rightMagnitude = 2 * ((right?.value ?: right?.magnitude()) ?: 0)

		return leftMagnitude + rightMagnitude
	}

	sealed class ReduceAction {
		abstract fun run()

		data class Explode(val node: Node<Int>) : ReduceAction() {
			override fun run() {
				val left = node.left?.value ?: 0
				val right = node.right?.value ?: 0

				node.findClosestLeafToLeft()?.let { it.value = (it.value ?: 0) + left }
				node.findClosestLeafToRight()?.let { it.value = (it.value ?: 0) + right }

				// Replace this node with a zero-value node
				if (node.isLeft()) {
					node.parent?.left = Node(node.parent, node.depth).apply { value = 0 }
				} else {
					node.parent?.right = Node(node.parent, node.depth).apply { value = 0 }
				}
			}
		}

		data class Split(val node: Node<Int>) : ReduceAction() {
			override fun run() {
				// Replace this literal value node with two new literal value nodes
				node.left = Node(node, node.depth + 1).apply { value = floor(node.value!! / 2f).toInt() }
				node.right = Node(node, node.depth + 1).apply { value = ceil(node.value!! / 2f).toInt() }
				node.value = null
			}
		}
	}

}