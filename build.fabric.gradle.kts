// Fabric build script, evaluated once per `versions/{mcversion}-fabric` node.

plugins {
    id("net.fabricmc.fabric-loom") version "1.18.4"
}

val modId = property("mod.id") as String
val modName = property("mod.name") as String
val modVersion = property("mod.version") as String
val minecraftVersion = sc.current.version
val loaderVersion = property("deps.fabric_loader") as String
val minecraftCompat = property("mod.mc_compat") as String

version = "$modVersion+$minecraftVersion"
base.archivesName = "$modId-fabric"

repositories {
    mavenCentral()
}

dependencies {
    // 26.x ships unobfuscated, so a plain `minecraft` dependency (no `mappings`)
    // and regular `implementation` scopes are all that is needed.
    minecraft("com.mojang:minecraft:$minecraftVersion")
    implementation("net.fabricmc:fabric-loader:$loaderVersion")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
}

fabricApi {
    configureDataGeneration {
        client = true
    }
}

loom {
    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }

    runConfigs.configureEach {
        // Dev runs from IntelliJ are delegated to Gradle, so the Stonecutter
        // generated sources are always up to date.
        preferGradleTask = true
        generateRunConfig = true
        // One shared run directory for every node.
        runDirectory = rootProject.file("run")
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
    options.encoding = "UTF-8"
}

tasks.processResources {
    val props = mapOf(
        "id" to modId,
        "name" to modName,
        "version" to project.version.toString(),
        "minecraft" to minecraftCompat,
        "loader" to loaderVersion,
        "java" to "25",
    )
    inputs.properties(props)

    filesMatching("fabric.mod.json") { expand(props) }
    exclude("META-INF/neoforge.mods.toml")
}

tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

// Collects the finished jars into the root `build/libs/{mod version}/` directory,
// so all four nodes end up in one place.
val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    description = "Copies the Fabric mod jar and its sources into the root build/libs directory."

    inputs.property("version", modVersion)
    from(
        tasks.jar.flatMap { it.archiveFile },
        tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile },
    )
    into(rootProject.layout.buildDirectory.dir("libs/$modVersion"))
}

tasks.named("build") {
    dependsOn(buildAndCollect)
}
