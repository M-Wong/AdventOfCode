import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    alias(libs.plugins.kotlin.jvm)
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin(rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    }

    dependencies {
        implementation(rootProject.libs.kotlin.stdlib)
        implementation(rootProject.libs.kotlin.test)

        testImplementation(rootProject.libs.junit.jupiter.api)
        testImplementation(rootProject.libs.junit.jupiter.params)
        testRuntimeOnly(rootProject.libs.junit.jupiter.engine)
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}
