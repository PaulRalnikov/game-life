plugins {
    kotlin("jvm") version "1.8.20"
    id("org.jetbrains.compose") version "1.4.0"
    kotlin("plugin.serialization") version "1.8.20"
}


repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}


dependencies {
    implementation(compose.desktop.currentOs)
    val coroutines_version = "1.6.4"
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutines_version")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutines_version")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$coroutines_version")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-debug:$coroutines_version")
    implementation ("ch.qos.logback:logback-classic:1.4.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("com.darkrockstudios:mpfilepicker:1.1.0")
}


compose.desktop {
    application {
        mainClass = "MainKt"
    }
}