import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21"
}

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

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.vlingo:vlingo-lattice:$vlingoVersion")
    implementation("io.vlingo:vlingo-symbio-jdbc:$vlingoVersion")

    implementation("io.feaggle:feaggle:$feaggleVersion")

    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}