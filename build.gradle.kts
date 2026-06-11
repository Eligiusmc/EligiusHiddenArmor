plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.6"
    id("com.modrinth.minotaur") version "2.8.7"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
}

group = "com.makrozai"
version = (property("pluginVersion") as String)
description = "EligiusHiddenArmor"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")

    // Apache Commons
    implementation("org.apache.commons:commons-lang3:3.14.0")

    // bStats
    implementation("org.bstats:bstats-bukkit:3.2.1")

    // Redis
    implementation("redis.clients:jedis:5.1.0")

    // HikariCP
    implementation("com.zaxxer:HikariCP:5.1.0")

    // PacketEvents
    implementation("com.github.retrooper:packetevents-spigot:2.12.2")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.21:3.102.0")

    // Kyori Adventure
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
        options.compilerArgs.add("-XDstringConcat=inline")
    }

    jar {
        archiveClassifier.set("original")
    }

    shadowJar {
        archiveClassifier.set("")
    }

    processResources {
        filteringCharset = "UTF-8"
        val props = mapOf(
            "version" to project.version
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    test {
        useJUnitPlatform()
    }
}

val versionString: String = "${version}"

// --- Modrinth Publishing Configuration ---
modrinth {
    token.set(System.getenv("MODRINTH_API_TOKEN"))
    projectId.set("eligiushiddenarmor")
    versionNumber.set(versionString)
    
    val channelEnv = System.getenv("CHANNEL") ?: "Release"
    versionType.set(channelEnv.lowercase())
    
    uploadFile.set(tasks.named("shadowJar"))
    gameVersions.addAll("1.21", "1.21.1", "1.21.3", "1.21.4", "26.1.1", "26.1.2")
    loaders.addAll("bukkit", "spigot", "paper", "purpur", "folia")
    syncBodyFrom.set(rootProject.file("README.md").readText()) // Using README temporarily for Modrinth
    
    val changelogEnv = System.getenv("CHANGELOG")
    if (!changelogEnv.isNullOrBlank()) {
        changelog.set(changelogEnv)
    }
}

// --- Hangar Publishing Configuration ---
hangarPublish {
    publications.register("plugin") {
        version.set(versionString)
        id.set("EligiusHiddenArmor")
        val channelEnv = System.getenv("CHANNEL") ?: "Release"
        channel.set(channelEnv.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
        
        val changelogEnv = System.getenv("CHANGELOG")
        if (!changelogEnv.isNullOrBlank()) {
            changelog.set(changelogEnv)
        } else {
            val changelogFile = rootProject.file("CHANGELOG.md")
            if (changelogFile.exists()) {
                changelog.set(changelogFile.readText())
            } else {
                changelog.set("New Release")
            }
        }
        
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        
        platforms {
            paper {
                jar.set(tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").flatMap { it.archiveFile })
                platformVersions.set(listOf("1.21", "1.21.1", "1.21.3", "1.21.4", "26.1.1", "26.1.2"))
            }
        }
    }
}
