import kr.entree.spigradle.kotlin.paper
import kr.entree.spigradle.kotlin.papermc

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("kr.entree.spigradle")
}

version = "4.0.0"

repositories {
    papermc()
    maven(uri("https://repo.extendedclip.com/content/repositories/placeholderapi/"))
    maven(uri("https://jitpack.io"))

    maven(url = "https://maven.pkg.github.com/DumbDogDiner/StickyAPI") {
        credentials {
            username = property("ghUser") as String
            password = property("ghPass") as String
        }
    }
    maven(url = "https://maven.pkg.github.com/DumbDogDiner/closedsource-package-mirror") {
        credentials {
            username = property("ghUser") as String
            password = property("ghPass") as String
        }
    }
    maven(url = "https://raw.githubusercontent.com/JorelAli/CommandAPI/mvn-repo/")
    maven(url = "https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    // jvm and kotlin dependencies
    implementation(kotlin("stdlib"))
    implementation(project(":StickyCommandsAPI"))

    // server dependencies
    compileOnly(paper())
    implementation("com.dumbdogdiner:stickyapi:2.1.0")

    // plugin depends
    compileOnly("me.clip:placeholderapi:2.10.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("net.luckperms:api:5.2")
    compileOnly("com.dumbdogdiner.closedsource-package-mirror:stafffacilities:4.8.5")

    // Database dependencies
    implementation("org.jetbrains.exposed", "exposed-core", "0.28.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.28.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.28.1")
    implementation("org.jetbrains.exposed", "exposed-java-time", "0.28.1")
    implementation("org.postgresql", "postgresql", "42.2.18")
    implementation("pw.forst", "exposed-upsert", "1.0")
    implementation("com.zaxxer", "HikariCP", "3.4.5")

    implementation("dev.jorel" , "commandapi-shade", "5.8")
}


spotless {
    kotlin {
        ktlint()
        licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
    }
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveClassifier.set("")
        val pkg = "com.dumbdogdiner.stickycommands.libs."
        relocate("com.zaxxer", "${pkg}com.zaxxer")
        relocate("org.postgresql", "${pkg}org.postgresql")
    }

    spigot {
        name = "StickyCommands"
        authors = mutableListOf("ZachyFoxx", "SkyezerFox", "Rodwuff")
        apiVersion = "1.16"
        softDepends = mutableListOf("Vault", "LuckPerms", "StaffFacilities")
        version = "4.0.0"
    }
}
