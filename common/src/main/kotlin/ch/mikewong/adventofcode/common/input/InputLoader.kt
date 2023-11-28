package ch.mikewong.adventofcode.common.input

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import java.util.*

object InputLoader {

	private val client = HttpClient(CIO) {
		install(Logging) {
			logger = Logger.SIMPLE
			level = LogLevel.INFO
		}
	}

	private const val PROPERTIES_FILE_PATH = "local.properties"
	private const val AOC_URL = "https://adventofcode.com/{YEAR}/day/{DAY}/input"

	private const val MODULE_PREFIX = "year"
	private const val TARGET_PATH = "src/main/resources/input"

	private val sessionToken: String? by lazy {
		val localPropertiesFile = File(".", PROPERTIES_FILE_PATH)
		val localProperties = Properties().apply { load(localPropertiesFile.bufferedReader()) }
		localProperties.getProperty("aoc_session_token")
	}

	fun downloadInput(year: Int, day: Int, overwrite: Boolean = false) {
		val module = MODULE_PREFIX + year
		val inputFilePath = "$TARGET_PATH/day$day.txt"
		val targetFile = File(module, inputFilePath)

		if (overwrite || !targetFile.exists()) {
			if (sessionToken == null) {
				throw IllegalStateException("No session token defined for input download")
			}

			val inputFileUrl = AOC_URL.replace("{YEAR}", year.toString()).replace("{DAY}", day.toString())
			runBlocking(Dispatchers.IO) {
				client.use { client ->
					val response = client.get(inputFileUrl) {
						this.header("Cookie", "session=$sessionToken")
					}

					if (response.status.isSuccess()) {
						targetFile.bufferedWriter().use { it.write(response.bodyAsText().trim()) }
					} else {
						throw IOException("Request failed with HTTP ${response.status.value}")
					}
				}
			}
		}
	}

}