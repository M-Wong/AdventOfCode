package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.indexOfFirst
import ch.mikewong.adventofcode.common.extensions.indexOfLast

class Day9 : Day<Long, Long>(2024, 9, "Disk Fragmenter") {

	// 6283404590840
	override fun partOne(): Long {
		val (disk, _) = parseInput()
		return disk.defragmentBlocks()
			.mapIndexedNotNull { idx, id -> id?.let { idx.toLong() * it } }
			.sum()
	}

	// 6304576012713
	override fun partTwo(): Long {
		val (disk, fileLengths) = parseInput()

		return disk.defragmentFiles(fileLengths)
			.mapIndexedNotNull { idx, id -> id?.let { idx.toLong() * it } }
			.sum()
	}

	/**
	 * Parses the input into two data structures
	 * The left side of the pair is the disk array, where each index is either a file ID or null
	 * The right side of the pair is a map of file IDs and their lengths
	 */
	private fun parseInput(): Pair<Array<Int?>, Map<Int, Int>> {
		val inputLengths = input.map { it.digitToInt() }
		val totalSize = inputLengths.sum()
		val disk = Array<Int?>(totalSize) { null }
		val fileLengths = mutableMapOf<Int, Int>()

		var isFile = true
		var diskIndex = 0
		inputLengths.forEachIndexed { idx, length ->
			// If this is currently a file, take the file ID (which is half the index, since we alternate between file and empty space) and store the length
			val fileId = if (isFile) {
				(idx / 2).also { fileLengths[it] = length }
			} else null

			// Fill the disk with the file ID for the given length
			repeat(length) {
				disk[diskIndex] = fileId
				diskIndex++
			}
			isFile = !isFile
		}

		return disk to fileLengths
	}

	/**
	 * Defragments the disk by moving single blocks all the way to the first free disk index, splitting up files if necessary
	 * This results in a single continuous block of files, followed by a single continuous block of free space
	 */
	private fun Array<Int?>.defragmentBlocks(): Array<Int?> {
		var firstFreeIndex = this.indexOfFirst { it == null }
		var lastFileIndex = this.indexOfLast { it != null }

		// Move from the end of the disk to the front, stopping once those indices cross each other
		while (firstFreeIndex < lastFileIndex) {
			this[firstFreeIndex] = this[lastFileIndex]
			this[lastFileIndex] = null

			// Find the next free index
			firstFreeIndex++
			while (firstFreeIndex < lastFileIndex && this[firstFreeIndex] != null) {
				firstFreeIndex++
			}

			// Find the previous file index
			lastFileIndex--
			while (lastFileIndex > firstFreeIndex && this[lastFileIndex] == null) {
				lastFileIndex--
			}
		}

		return this
	}

	/**
	 * Defragments the disk by moving entire files to the first available free space with enough room
	 * This results in a disk with gaps between files
	 */
	private fun Array<Int?>.defragmentFiles(fileLengths: Map<Int, Int>): Array<Int?> {
		var currentIndex = this.lastIndex

		// Move from the end of the disk to the front, stopping once we reach the beginning
		while (currentIndex > 0) {
			val fileId = this[currentIndex]
			if (fileId != null) {
				val fileLength = fileLengths.getValue(fileId)

				// Find the first free index that has enough space for the file
				var firstFreeIndex = this.indexOfFirst { it == null }
				var nextNonFreeIndex = this.indexOfFirst(firstFreeIndex) { it != null }
				while (firstFreeIndex != -1 && nextNonFreeIndex != -1 && nextNonFreeIndex - firstFreeIndex < fileLength) {
					firstFreeIndex = this.indexOfFirst(nextNonFreeIndex) { it == null }
					nextNonFreeIndex = this.indexOfFirst(firstFreeIndex) { it != null }
				}

				if (firstFreeIndex >= currentIndex) {
					// File cannot move to the left, so we skip it
					currentIndex -= fileLength
					continue
				} else {
					// File can move to the left, so we move it
					repeat(fileLength) { i ->
						this[firstFreeIndex + i] = fileId
						this[currentIndex - i] = null
					}

					currentIndex -= fileLength
				}
			} else {
				currentIndex--
			}
		}

		return this
	}

}