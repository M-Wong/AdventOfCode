plugins {
    application
}

dependencies {
    implementation(project(":common"))
}

application {
    mainClass.set("RunnerKt")
}