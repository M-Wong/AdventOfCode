import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    kotlin("jvm") version "1.7.22"
}

allprojects {
    group = "ch.mikewong.adventofcode"
    version = "2022"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("test"))
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }
}

repositories {
    mavenCentral()
}

//dependencies {
//    implementation(kotlin("stdlib"))
//    implementation(kotlin("test"))
//    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
//    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
//}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
