plugins {}

dependencies {
	implementation("io.ktor:ktor-client-logging-jvm:2.3.6")
	val ktorVersion = "2.3.6"
	implementation("io.ktor:ktor-client-core:$ktorVersion")
	implementation("io.ktor:ktor-client-cio:$ktorVersion")
	implementation("io.ktor:ktor-client-logging:$ktorVersion")
	implementation("ch.qos.logback:logback-classic:1.4.11")

}