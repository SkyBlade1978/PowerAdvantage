# PowerAdvantage

Power Advantage provides shared power, fluid, and item transport systems for its Minecraft 1.10.2 expansion mods.

The [Advantage Works 1.10.2 Handbook](docs/1.10/README.md) documents every machine and component across Power, Steam, and Electric Advantage, including commissioning layouts and known investigation points.

The source-controlled [Advantage Works test rig](test-rig/README.md) turns those commissioning cards into a disposable, walkable Forge server world with repeatable save/restart checkpoints. It is a separate test mod and is never packaged with PowerAdvantage.

## Building Minecraft 1.10.2

The `master-1.10.2` build uses ForgeGradle 7.0.34 and Gradle 9.6.1. Run Gradle with Java 17; Gradle resolves the Java 8 toolchain used for compilation.

```text
gradlew.bat clean check build verifyReleaseDependencies verifyReleaseArtifacts writeReleaseChecksums
```

Release jars are written to `build/libs`. The deobfuscated development jar for sibling mod compilation is written to `build/libs-dev` by `deobfJar` or `build`.

For Eclipse, import the repository as an existing Gradle project and run:

```text
gradlew.bat cleanEclipse verifyEclipseProductionClasspath
```

Base Metals and OreSpawn remain required distribution dependencies but are deliberately absent from the compile classpath. Optional RF and RebornCore integrations compile against pinned API inputs and are not bundled.
