pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "FabricMC" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
    }
}

plugins {
    // Multi-version controller: creates one subproject per Minecraft version/loader pair.
    id("dev.kikugie.stonecutter") version "0.9.8"

    // Provisions the Java 25 toolchain automatically when no local JDK 25 is installed.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
    create(rootProject) {
        /**
         * Registers `versions/{project}-{loader}` nodes that share `Minecraft {version}`
         * and are built by `build.{loader}.gradle.kts`.
         */
        fun match(project: String, vararg loaders: String, version: String = project) {
            for (loader in loaders) {
                version("$project-$loader", version).buildscript("build.$loader.gradle.kts")
            }
        }

        match("26.2", "fabric", "neoforge")
        match("26.3", "fabric", "neoforge")

        // The version checked out by default when no other node is active.
        vcsVersion = "26.3-fabric"
    }
}

rootProject.name = "verdant-harvest"
