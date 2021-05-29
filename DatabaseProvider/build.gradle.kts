import kr.entree.spigradle.kotlin.codemc
import kr.entree.spigradle.kotlin.jitpack
import kr.entree.spigradle.kotlin.paper
import kr.entree.spigradle.kotlin.papermc
import kr.entree.spigradle.kotlin.spigot
import kr.entree.spigradle.kotlin.spigotmc

plugins {
    id("java-library")
    idea
    kotlin("jvm") version "1.4.32"
    //id("org.jetbrains.dokka")
}

group = "com.dumbdogdiner.stickycommands.db"
version = rootProject.version

repositories {
    mavenCentral()

    // spigot repository
    papermc()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":Konstants"))

    compileOnly(paper())

    compileOnly("org.projectlombok:lombok:1.18.16")
    annotationProcessor("org.projectlombok:lombok:1.18.12")

    // Database
    api("org.jetbrains.exposed", "exposed-core", "0.31.1")
    api("org.jetbrains.exposed", "exposed-dao", "0.31.1")
    api("org.jetbrains.exposed", "exposed-jdbc", "0.31.1")
    api("org.jetbrains.exposed", "exposed-java-time", "0.31.1")
    api("org.postgresql", "postgresql", "42.2.18")
    api("pw.forst", "exposed-upsert", "1.1.0")
    api("com.zaxxer", "HikariCP", "3.4.5")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}