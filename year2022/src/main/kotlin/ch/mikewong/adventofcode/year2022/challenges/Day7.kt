package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day7 : Day<Int, Int>(2022, 7, "No Space Left On Device") {

	private val fileStructure by lazy { readFileStructure() }

	override fun partOne(): Int {
		return fileStructure.sizeOfDirsLessThan100K()
	}

	override fun partTwo(): Int {
		val totalDiskSize = 70_000_000
		val requiredAvailableSpace = 30_000_000

		val rootSize = fileStructure.size()
		val availableSize = totalDiskSize - rootSize

		val sizeToFreeUp = requiredAvailableSpace - availableSize

		val dirsAboveSize = fileStructure.findDirsAboveSize(sizeToFreeUp)
		return dirsAboveSize.minOf { it.size() }
	}

	private fun File.sizeOfDirsLessThan100K(): Int {
		if (!isDirectory) return 0

		// Take the size of this directory if it is less than 100K plus the size of all children less than 100K
		val size = this.size().takeIf { it < 100_000 } ?: 0
		return size + this.children.sumOf { it.sizeOfDirsLessThan100K() }
	}

	private fun File.findDirsAboveSize(size: Int): List<File> {
		if (!this.isDirectory) return emptyList()

		return if (this.size() >= size) {
			// If this directory is above the size, recursively find all children that are above the size as well
			val childrenAboveSize = this.children.flatMap { it.findDirsAboveSize(size) }
			childrenAboveSize + this
		} else {
			// If this directory is already less than the size, no child can be above it
			emptyList()
		}
	}

	private fun readFileStructure(): File {
		var rootDir: File? = null
		var currentDir: File? = null
		inputLines.forEach { line ->
			if (line.startsWith("$")) {
				val command = line.removePrefix("$ ")
				if (command.startsWith("cd")) {
					val dirName = command.removePrefix("cd ")

					// Check which directory to move into (parent ".." or child "abc")
					if (dirName == "..") {
						currentDir = currentDir?.parent
					} else {
						val childDir = currentDir?.children?.firstOrNull { it.name == dirName }
							?: File(dirName, parent = currentDir, size = null, isDirectory = true)
						currentDir = childDir

						if (rootDir == null) {
							// In case no root directory has been set yet, the first directory we enter is the root
							rootDir = childDir
						}
					}
				} else if (command == "ls") {
					// Ignore ls
				} else {
					throw IllegalArgumentException("Unknown command: $command")
				}
			} else {
				// Lines not starting with $ are prints of the ls command, so it's either a directory or a file
				val file = if (line.startsWith("dir")) {
					val dirName = line.removePrefix("dir ")
					File(dirName, parent = currentDir, size = null, isDirectory = true)
				} else {
					val parts = line.split(" ")
					File(parts[1], parent = currentDir, size = parts[0].toInt())
				}
				currentDir?.children?.add(file)
			}
		}

		return requireNotNull(rootDir)
	}

	private data class File(
		val name: String,
		val parent: File? = null,
		private val size: Int? = null,
		val isDirectory: Boolean = false,
		val children: MutableList<File> = mutableListOf()
	) {
		fun size(): Int {
			return  if (isDirectory) children.sumOf { it.size() } else requireNotNull(size)
		}

		fun print(indent: Int = 0) {
			val padding = "".padStart(indent)
			if (isDirectory) {
				println("$padding$name (dir)")
				children.forEach { it.print(indent + 1) }
			} else {
				println("$padding$name ($size)")
			}
		}

		override fun toString() = name
	}

}