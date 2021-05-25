import kr.entree.spigradle.kotlin.paper

plugins {
    java
    kotlin("jvm")
    id("kr.entree.spigradle")
}

version = "1.0.0"

dependencies {
    // paper
    compileOnly(paper())

    // Annotations
    implementation("org.jetbrains:annotations:21.0.1")
    compileOnly("org.projectlombok:lombok:1.18.18")
    annotationProcessor("org.projectlombok:lombok:1.18.18")
}

spotless {
    java {
        importOrder()
        prettier(
            mapOf(
                "prettier" to "2.0.5",
                "prettier-plugin-java" to "0.8.0"
            )
        ).config(
            mapOf(
                "parser" to "java",
                "tabWidth" to 4
            )
        )
        licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
    }
}

tasks {
    generateSpigotDescription {
        enabled = false
    }
}

