# Advantage Works 1.10.2 Test Rig

> **Commissioning notice:** This harness builds disposable works. It is not a gameplay mod, registers no content, and must never be shipped inside a PowerAdvantage jar.

The rig turns the 20 cards in the [Advantage Works handbook](../docs/1.10/README.md) into one walkable superflat server world. Power Works lies west of the central concourse, Steam Works north, Electric Works east, and the drill, lift, turbine, pumping, musket, and turret proving grounds south.

## Safety interlocks

The `/advworks` command refuses to build or clear unless both conditions hold:

- the level name begins with `AdvantageWorks`;
- the server working directory contains `advantage-works.allow`.

Each station has declared bounds. Reset clears blocks only inside those bounds and removes only entities tagged by that station. Generated saves, staged jars, logs, reports, Forge server files, caches, and local settings are ignored by Git.

## Operator controls

Every bay has clickable start, stop, reset, and check signs plus a wool status lamp. Console operators can use:

```text
/advworks build [all|power|steam|electric]
/advworks reset <station|all>
/advworks start <station>
/advworks stop <station>
/advworks check <station|all>
/advworks status [station]
/advworks checkpoint <station> <name>
```

Before a player uses the clickable controls, grant that tester operator permission with `op <player>` at the server console. The harness intentionally keeps `/advworks` at permission level 2 as a second interlock beyond the marked disposable world.

The definition at `src/main/resources/assets/advantageworkstest/works/works-1.10.2.json` owns station origins, bounds, placements, setup/start/stop actions, assertions, required mods, and manual checks. Station IDs are the handbook card IDs `W-01` through `E-08`.

## Result meanings

| Result | Meaning |
| --- | --- |
| `PASS` | Every automated assertion passed and no manual observation remains. |
| `FAIL` | An ordinary assertion failed. Automated runners stop with failure. |
| `EXPECTED_FAIL` | A declared handbook bug reproduced; the assertion names its bug ID. |
| `UNEXPECTED_PASS` | A declared bug no longer reproduced and needs review before expectations change. |
| `SKIP` | A required mod or integration is absent. |
| `MANUAL_REQUIRED` | Structural assertions passed, but gameplay observations remain on the station card. |

Animation and GUI appearance are never sufficient proof. Use checkpoints and reconcile bounded items, fluids, and energy.

## Build and development run

Run Gradle with Java 17. The harness and all Minecraft code compile to Java 8 bytecode.

```text
gradlew.bat -p test-rig clean test assemble
gradlew.bat -p test-rig verifyWorksDev
gradlew.bat -p test-rig runWorksDev
```

On Linux or macOS use `./gradlew` with the same arguments. `verifyWorksDev` builds all stations, runs each bounded start/stop sequence, saves, stops the server process, starts it again, checkpoints the retained state, and reruns the structural assertions. `runWorksDev` leaves the disposable server open at `localhost:25565` for a human inspector.

Limit an automated run to selected lifecycle bays, or turn one into a 30-minute endurance run, without changing its profile:

```text
gradlew.bat -p test-rig verifyWorksDev -PworksStations=W-04
gradlew.bat -p test-rig verifyWorksPackagedCurrent -PworksStations=W-04,S-02 -PworksRunSeconds=1800
gradlew.bat -p test-rig verifyWorksPackagedCurrent -PworksStations=E-01 -PworksServerPort=25566
```

Every run still builds and checks the complete works; the selection controls which stations are started, stopped, and checkpointed around the full process restart. The configured run duration is also used as a post-restart settle interval before assertions, allowing an eight-tick distribution cycle to complete. Use `worksServerPort` when another local server occupies the profile's default port. The effective station list, duration, and port are recorded in `run-manifest.json`.

The development profile uses current PowerAdvantage source plus the sibling SteamAdvantage and ElectricAdvantage deobf jars. It deliberately omits BaseMetals and OreSpawn so integration-dependent behavior is visibly skipped or left for the packaged profiles.

## Run matrix

| Profile/task | Purpose |
| --- | --- |
| `verifyWorksDev` | Current Power source and sibling deobf extensions, without BaseMetals/OreSpawn |
| `verifyWorksPackagedCurrent` | Reobfuscated Advantage jars with BaseMetals `2.5.0-beta4.238` and OreSpawn `3.2.2.104` |
| `verifyWorksPackagedLegacy` | Reobfuscated Advantage jars with BaseMetals `2.4.0.11` and OreSpawn `1.1.0` |
| `verifyWorksPackagedDecoupled` | Reobfuscated Advantage jars without BaseMetals/OreSpawn |
| `verifyWorksPackagedOptionalPower` | RF API and RebornCore converter discovery |
| `verifyWorksTechProgression` | Registration/build audit under `TECH_PROGRESSION` |
| `verifyWorksApocalyptic` | Registration/build audit under `APOCALYPTIC` |

Profiles pin filenames, mod IDs, versions, and SHA-256 values. Workspace-built harness jars are identified and version-checked but not pinned to a source-dependent hash. The packaged runner downloads only the pinned official Forge installer, verifies it, installs a fresh server into the disposable runtime, and launches Minecraft with Java 8.

Set these only for profiles that need them:

```text
ADVANTAGE_WORKS_CURRENT_MODS=<directory containing the pinned current BaseMetals and OreSpawn jars>
ADVANTAGE_WORKS_REBORNCORE_110=<full path to reborncore-237903-2425028.jar>
ADVANTAGE_WORKS_JAVA8_HOME=<Java 8 home, when it is not in the default Gradle toolchain cache>
ADVANTAGE_WORKS_GRADLE_JAVA_HOME=<Java 17 home, when the runner itself is launched on Java 8>
```

## Station register

| District | Cards |
| --- | --- |
| Power Works | `W-01` fluid persistence; `W-02` drain/discharge reach; `W-03` distillation; `W-04` conveyors and all filters |
| Steam Works | `S-01` boilers; `S-02` topology; `S-03` storage/scarcity; `S-04` furnace/crusher/still |
| South proving grounds | `S-05` drill; `S-06` pump; `S-07` elevator; `S-08` musket; `E-01` generators/turbine; `E-06` drill; `E-07` light/turret |
| Electric Works | `E-02` batteries/distribution; `E-03` processors; `E-04` fluids; `E-05` growth; `E-08` isolated assembler diagnostic |

Ore generation is intentionally excluded from the superflat works. Run that check in a separately generated disposable normal world so terrain generation cannot interfere with machine conservation tests.

## Confirmed-bug regression witnesses

| Station | Automated witness | Manual remainder |
| --- | --- | --- |
| `S-01` | Seeds both `Tempoerature` and `Temperature`, saves canonically, and requires retained heat after a full process restart (`SA-110-002`). | Compare the geothermal GUI gauge with the saved value. |
| `S-04` | An isolated unpowered crusher with one cobblestone must report comparator level `1` before and after restart (`SA-110-003`). | Check intermediate and full stack levels when commissioning redstone automation. |
| `S-06` | An isolated pump must retain exactly 1,000 mB of water after restart (`SA-110-001`). | Confirm the pump GUI agrees with the server NBT. |
| `E-01` | Requires an active hydroelectric generator and charge in its directly connected battery after restart (`EA-110-003`). | Open the hydroelectric GUI and confirm a normal-width active bar. |

## Release isolation

`verifyHarnessIsolation` opens the PowerAdvantage release jar and rejects any `com/mcmoddev/advantageworks` class or `assets/advantageworkstest` resource. The normal PowerAdvantage build never includes the nested `test-rig` source set.
