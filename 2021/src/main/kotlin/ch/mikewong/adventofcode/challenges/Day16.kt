package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.util.product

class Day16 : Day<Int, Long>(16, "Packet Decoder") {

	private val hexToBinary = mapOf(
		'0' to "0000",
		'1' to "0001",
		'2' to "0010",
		'3' to "0011",
		'4' to "0100",
		'5' to "0101",
		'6' to "0110",
		'7' to "0111",
		'8' to "1000",
		'9' to "1001",
		'A' to "1010",
		'B' to "1011",
		'C' to "1100",
		'D' to "1101",
		'E' to "1110",
		'F' to "1111",
	)

	private val inputTransmission = inputLines.map { line -> line.mapNotNull { hexToBinary[it] }.joinToString("") }.first()
	private val inputPacket by lazy { parsePackets(inputTransmission).single() }

	override fun partOne(): Int {
		return inputPacket.versionSum
	}

	override fun partTwo(): Long {
		return inputPacket.getValue()
	}

	private fun parsePackets(transmission: String): List<Packet> {
		val packets = mutableListOf<Packet>()

		val version = transmission.substring(0, 3).binaryToHex().digitToInt()
		val typeId = transmission.substring(3, 6).binaryToHex()

		if (typeId == '4') {
			// A literal packet
			var index = 6
			val literalValueFragments = mutableListOf<String>()
			var isPacketFinished = true
			while (isPacketFinished) {
				if (transmission[index] == '0') {
					isPacketFinished = false
				}

				index++

				literalValueFragments.add(transmission.substring(index, index + 4))
				index += 4
			}

			val literalValue = literalValueFragments.joinToString("").toLong(2)
			packets.add(Packet.Literal(version, index, literalValue))

			if (index + 7 < transmission.length) {
				packets.addAll(parsePackets(transmission.substring(index)))
			}
		} else {
			// An operator packet
			var packetLength = 7
			val subPackets = when (transmission[6]) {
				'0' -> {
					val length = transmission.substring(7, 22).toInt(2)
					val subPacketTransmission = transmission.substring(22, 22 + length)
					packetLength = 22 + length
					parsePackets(subPacketTransmission)
				}
				'1' -> {
					val subPacketCount = transmission.substring(7, 18).toInt(2)
					val subPacketTransmission = transmission.substring(18)
					val subPackets = parsePackets(subPacketTransmission).take(subPacketCount)
					packetLength = 18 + subPackets.sumOf { it.length }
					subPackets
				}
				else -> emptyList()
			}

			packets.add(Packet.Operator(version, typeId.digitToInt(), packetLength, subPackets))

			if (packetLength + 7 < transmission.length) {
				packets.addAll(parsePackets(transmission.substring(packetLength)))
			}
		}

		return packets
	}

	private fun String.binaryToHex(): Char = hexToBinary.keys.single { hexToBinary[it] == this.padStart(4, '0') }

	private sealed class Packet(open val version: Int, open val typeId: Int, open val length: Int) {
		abstract val versionSum: Int
		abstract fun getValue(): Long

		data class Literal(override val version: Int, override val length: Int, val number: Long) : Packet(version, 4, length) {
			override val versionSum = version

			override fun getValue() = number
		}

		data class Operator(
			override val version: Int,
			override val typeId: Int,
			override val length: Int,
			val subPackets: List<Packet>
		) : Packet(version, typeId, length) {
			override val versionSum = version + subPackets.sumOf { it.versionSum }
			override fun getValue(): Long {
				return when (typeId) {
					0 -> subPackets.sumOf { it.getValue() }
					1 -> subPackets.map { it.getValue() }.product()
					2 -> subPackets.minOf { it.getValue() }
					3 -> subPackets.maxOf { it.getValue() }
					5 -> if (subPackets[0].getValue() > subPackets[1].getValue()) 1 else 0
					6 -> if (subPackets[0].getValue() < subPackets[1].getValue()) 1 else 0
					7 -> if (subPackets[0].getValue() == subPackets[1].getValue()) 1 else 0
					else -> 0L
				}
			}
		}
	}
}