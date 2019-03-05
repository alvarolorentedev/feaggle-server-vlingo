import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:4.0.3")
    }
}

plugins {
    kotlin("jvm") version "1.3.21"
}

apply(plugin = "com.github.johnrengelman.shadow")

group = "io.feaggle"
version = "0.1.0"

repositories {
    jcenter()
    mavenCentral()
}

val feaggleVersion = "0.1.0"
val vlingoVersion = "0.8.1"
val testContainersVersion = "1.10.6"
val junitVersion = "5.4.0"
val flywayVersion = "5.2.4"
val restAssuredVersion = "3.3.0"
val logbackVersion = "1.2.3"

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.vlingo:vlingo-http:$vlingoVersion")
    implementation("io.vlingo:vlingo-lattice:$vlingoVersion")
    implementation("io.vlingo:vlingo-symbio-jdbc:$vlingoVersion")
    implementation("io.vlingo:vlingo-telemetry:$vlingoVersion")

    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("io.feaggle:feaggle:$feaggleVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("io.rest-assured:json-path:$restAssuredVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
            "Main-Class" to "io.feaggle.server.MainKt"
        ))
    }
}

task("generateVersionFile") {
    File("version.txt").writeText(project.version.toString())
}