// NeoForge build script, evaluated once per `versions/{mcversion}-neoforge` node.

plugins {
    id("net.neoforged.moddev") version "2.0.148"
    // Serialises Minecraft (re)compilation between NeoForge nodes.
    id("neoforge-mutex")
}

val modId = property("mod.id") as String
val modName = property("mod.name") as String
val modVersion = property("mod.version") as String
val minecraftVersion = sc.current.version
val neoVersion = property("deps.neo_loader") as String
val minecraftCompat = property("mod.mc_compat") as String
val modLicense = property("mod.license") as String
val modHomepage = property("mod.homepage") as String
val modAuthors = property("mod.authors") as String
val modDescription = property("mod.description") as String

version = "$modVersion+$minecraftVersion"
base.archivesName = "$modId-neoforge"

repositories {
    mavenCentral()
}

sourceSets.main {
    // Data generators write here; the folder is shared by every node.
    resources.srcDir(rootProject.file("src/generated/resources"))
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

neoForge {
    version = neoVersion

    runs {
        register("client") {
            client()
            gameDirectory = rootProject.file("run")
        }

        register("server") {
            server()
            programArgument("--nogui")
            gameDirectory = rootProject.file("run")
        }

        register("data") {
            clientData()
            gameDirectory = rootProject.file("run/data")
            programArguments.addAll(
                "--mod", modId, "--all",
                "--output", rootProject.file("src/generated/resources").absolutePath,
                "--existing", rootProject.file("src/main/resources").absolutePath,
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
    options.encoding = "UTF-8"
}

tasks.processResources {
    val props = mapOf(
        "mod_license" to modLicense,
        "mod_id" to modId,
        "mod_version" to project.version.toString(),
        "mod_name" to modName,
        "mod_homepage" to modHomepage,
        "mod_authors" to modAuthors,
        "mod_description" to modDescription,
        "neo_version" to neoVersion,
        "minecraft_version_range" to minecraftCompat,
    )
    inputs.properties(props)

    filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
    exclude("fabric.mod.json")
}

tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

// Collects the finished jar into the root `build/libs/{mod version}/` directory,
// so all four nodes end up in one place.
val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    description = "Copies the NeoForge mod jar into the root build/libs directory."

    inputs.property("version", modVersion)
    from(tasks.jar.flatMap { it.archiveFile })
    into(rootProject.layout.buildDirectory.dir("libs/$modVersion"))
}

tasks.named("build") {
    dependsOn(buildAndCollect)
}
