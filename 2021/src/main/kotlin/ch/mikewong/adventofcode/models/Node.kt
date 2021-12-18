package ch.mikewong.adventofcode.models

class Node<T>(var parent: Node<T>? = null, var depth: Int) {
	var value: T? = null
	var left: Node<T>? = null
	var right: Node<T>? = null

	override fun toString(): String {
		return value?.toString() ?: "[${left.toString()},${right.toString()}]"
	}

	fun increaseDepth() {
		depth++
		left?.increaseDepth()
		right?.increaseDepth()
	}

	fun findFirst(filter: (Node<T>) -> Boolean): Node<T>? {
		return if (filter.invoke(this)) {
			this
		} else {
			left?.findFirst(filter) ?: right?.findFirst(filter)
		}
	}

	fun findClosestLeafToLeft(): Node<T>? {
		if (isLeft()) {
			var parentNode = parent
			while (parentNode != null) {
				if (parentNode.isRight()) {
					val leftNode = parentNode.parent!!.left
					if (leftNode?.isLeaf() == true) {
						return leftNode
					}

					var rightNode = leftNode?.right
					while (rightNode != null) {
						if (rightNode.isLeaf()) {
							return rightNode
						}
						rightNode = rightNode.right
					}
				}

				parentNode = parentNode.parent
			}
		} else {
			var sibling = parent!!.left
			while (sibling?.isLeaf() != true) {
				sibling = sibling?.right
			}

			return sibling
		}

		return null
	}

	fun findClosestLeafToRight(): Node<T>? {
		if (isRight()) {
			var parentNode = parent
			while (parentNode != null) {
				if (parentNode.isLeft()) {
					val rightNode = parentNode.parent!!.right
					if (rightNode?.isLeaf() == true) {
						return rightNode
					}

					var leftNode = rightNode?.left
					while (leftNode != null) {
						if (leftNode.isLeaf()) {
							return leftNode
						}
						leftNode = leftNode.left
					}
				}

				parentNode = parentNode.parent
			}
		} else {
			var sibling = parent!!.right
			while (sibling?.isLeaf() != true) {
				sibling = sibling?.left
			}

			return sibling
		}

		return null
	}

	fun isLeft() = parent?.left == this

	fun isRight() = parent?.right == this

	fun isLeafPair() = (left?.isLeaf() ?: false) && (right?.isLeaf() ?: false)

	fun isLeaf() = this.value != null

	fun print() {
		val sb = StringBuilder()
		print(sb, "", "")
		println(sb.toString())
	}

	private fun print(sb: StringBuilder, prefix: String, childPrefix: String) {
		sb.append(prefix)
		value?.let { sb.append(it) } ?: sb.append("x")
		sb.append(System.lineSeparator())
		left?.print(sb, "$childPrefix├──", "$childPrefix│  ")
		right?.print(sb, "$childPrefix└──", "$childPrefix   ")
	}
}