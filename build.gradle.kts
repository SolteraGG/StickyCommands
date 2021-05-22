import kr.entree.spigradle.kotlin.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.dokka") version "1.4.32"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("maven-publish")
    id("io.freefair.lombok") version "5.3.0"
    id("kr.entree.spigradle") version "2.2.3"
}

group = "com.dumbdogdiner"
version = "3.3.2"
val mcApi = "1.16.5"
val apiVer = mcApi + "-R0.1-SNAPSHOT"
val useLocal = false;
val withClosedSource = false;

//java.sourceSets.create("build/")

// best fix i have would be to run kotlin on the delomboked java. then compile that java, and throw out the errors that happen during delombok
// its hacky, shitty, etc. but its the only thing i got that could work

// So all i gotta do is do the necessary gradle fuckery

repositories {
    mavenCentral()

    // spigot repository
    codemc()
    papermc()
    jitpack()
    spigotmc()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven(url = "https://maven.pkg.github.com/DumbDogDiner/StickyAPI") {
        credentials {
            username = property("ghUser") as String
            password = property("ghPass") as String
        }
    }
    if (withClosedSource) {
        maven(url = "https://maven.pkg.github.com/DumbDogDiner/closedsource-package-mirror") {
            credentials {
                username = property("ghUser") as String
                password = property("ghPass") as String
            }
        }
    }

    if (useLocal) {
        flatDir { dirs = setOf(file("libs")) }
        mavenLocal()
    }
}

dependencies {
    // *sigh* kotlin bullshit
    implementation(kotlin("stdlib"))

    // java deps
    compileOnly("org.projectlombok:lombok:1.18.16")
    annotationProcessor("org.projectlombok:lombok:1.18.12")
    implementation("org.jetbrains:annotations:20.1.0")
    kapt("org.projectlombok:lombok:1.18.12")

    // spigot, paper
    compileOnly(paper(apiVer))

    compileOnly("dev.jorel.CommandAPI:commandapi-core:5.12")
    compileOnly("dev.jorel.CommandAPI:commandapi-annotations:5.12")
    annotationProcessor("dev.jorel.CommandAPI:commandapi-annotations:5.12")


    // Database
    implementation("org.jetbrains.exposed", "exposed-core", "0.28.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.28.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.28.1")
    implementation("org.jetbrains.exposed", "exposed-java-time", "0.28.1")
    implementation("org.postgresql", "postgresql", "42.2.18")
    implementation("pw.forst", "exposed-upsert", "1.0")
    implementation("com.zaxxer", "HikariCP", "3.4.5")

    // plugin-specific deps
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("me.clip:placeholderapi:2.10.6")
    implementation("com.dumbdogdiner:stickyapi:1.5.0")
    compileOnly("net.luckperms:api:5.2")

    if (withClosedSource) {
        compileOnly("com.dumbdogdiner.closedsource-package-mirror:stafffacilities:4.8.5")
    } else {
        compileOnly("com.github.xtomyserrax:StaffFacilities:5.0.6.0")
    }
    if (useLocal) {
        implementation(fileTree("libs") { include("*.jar") })
    }
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
}

tasks.withType<JavaCompile> {
    targetCompatibility = JavaVersion.VERSION_11.toString()
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    options.compilerArgs.addAll(arrayOf("-parameters", "-Xlint:all"))
}

//tasks.withType(JavaCompile) {
//    options.compilerArgs() << "-Xlint:unchecked" << "-Xlint:deprecation"
//}


tasks {
    build {
        finalizedBy(shadowJar)
    }

    shadowJar {
        //archiveClassifier.set("")
        val pkg = "com.dumbdogdiner.stickycommands.libs."
        relocate("com.zaxxer", "${pkg}com.zaxxer")
        relocate("org.postgresql", "${pkg}org.postgresql")
    }

    spigot {
        apiVersion = mcApi
        version = this.version
    }

    register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    // have to disable java's javadoc so kotlin's doka can do things because sadness
    javadoc {
        enabled = false
    }

    jar {
        enabled = false
    }
}


//
//tasks.publish.dependsOn build, sources
//
//
//publishing {
//    repositories {
//        maven {
//            name = "GitHubPackages"
//            url = uri("https://maven.pkg.github.com/DumbDogDiner/StickyCommands")
//            credentials {
//                username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
//                password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
//            }
//        }
//    }
//    publications {
//        gpr(MavenPublication) {
//            from(components.java)
//            artifact sources // Publish the output of the sources task
//        }
//    }
//}
