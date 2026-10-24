plugins {
    id("dev.kikugie.stonecutter")
}

// The node whose state is written into the shared `src/` tree.
stonecutter active "26.2-neoforge"

// See https://stonecutter.kikugie.dev/wiki/v2/reference/gradle-api/project-controller
stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)

    // Makes the loader/version sections of `stonecutter.properties.toml` visible
    // through `sc.properties.get("...")` and `property("...")`.
    properties { tags(version, loader) }

    // Enables `//? if fabric {` / `//? if neoforge {` conditions in the sources.
    constants { match(loader, "fabric", "neoforge") }

    // Keeps the human readable mod version in sync with stonecutter.properties.toml.
    swaps["mod_version"] = "\"${node.project.property("mod.version")}\";"
}
