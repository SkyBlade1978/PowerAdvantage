# Commissioning and Fault Finding

> **Inspector's order:** A machine is not commissioned because it moved once. Commission the empty plant, the loaded plant, the interrupted plant, and the same plant after Minecraft has forgotten it was ever open.

[Handbook index](README.md) | [Technical ledger](technical-ledger.md) | [Inspector's register](possible-legacy-bugs.md)

The source-controlled [Advantage Works test rig](../../test-rig/README.md) generates these cards as bounded stations in one disposable superflat world. The layouts below remain the behavioral authority; a green structural harness check does not replace the manual observations on each card.

## Instruments and record sheet

Prepare a disposable 1.10.2 world with cheats enabled, screenshots, the latest log, and a written coordinate for every machine. Use survival equipment for recipe/throughput tests and clearly mark creative infinite-power blocks when they are used to isolate a network fault.

For every card, record:

| Field | Entry |
| --- | --- |
| Mod jar versions and Git commits | |
| Forge, Base Metals, and OreSpawn versions | |
| Power Advantage recipe mode and changed config | |
| Dimension and block coordinates | |
| Game tick/time interval measured | |
| Initial inventory, tank, energy, facing, and progress | |
| Result after same-session reload | |
| Result after complete Minecraft restart | |
| Relevant log lines and screenshot names | |

Use the evidence labels from the [handbook index](README.md#edition-authority) when promoting an observation into the manuals.

## Acceptance sequence

Run these stages in order. A later pass does not erase an earlier failure.

1. **Cold inspection:** Confirm orientation, connections, inventories, tanks, control signals, and clear working space.
2. **No-load run:** Isolate consumers and prove generation/storage.
3. **Rated-load run:** Connect one known consumer and measure local buffers and completed work.
4. **Scarcity run:** Reduce water, fuel, steam, or electricity until the machine pauses without loss or duplication.
5. **Interruption run:** Break and replace one conduit; then use the proper redstone switch without breaking blocks.
6. **Same-session reload:** Save and return to title, then reopen the world.
7. **Full-process restart:** Save, close Minecraft completely, relaunch, and reopen the world.
8. **Chunk reload:** Travel far enough to unload the installation, wait, and return.
9. **Endurance run:** Operate for 30 minutes with bounded inputs and measurable outputs.

## Card W-01: fluid transfer and persistence

```text
Elevation

  [water source]
       [drain]====[portable tank]====[fluid switch]====[metal tank]
                                                         |
                                                   [discharge]
                                                         v
                                                   [catch basin]

  ==== fluid pipe
```

1. Fit no filter to the metal tank. Start with both tanks empty and the switch unpowered.
2. Confirm the drain removes source blocks and both tanks gain water.
3. Apply redstone to the switch; quantities must stop changing across that cut.
4. Remove redstone; transfer must resume without replacing a tank or pipe.
5. Record both tank quantities, run both reload types, and compare exactly.
6. Break the portable tank and replace it; its item should retain fluid.
7. Break the metal tank only in the disposable world; its item is expected to lose fluid.

**Expected:** No full-restart loss, no manual block replacement needed to wake the network, and no transfer through a redstone-powered switch.

## Card W-02: drain/discharge reach

Build a stepped fluid field whose nearest available source changes after each collection. Include one flowing block and one true source. Confirm only source blocks are consumed and the search does not exceed the documented 32-step traversal. Repeat with an adjacent Forge fluid tank to prove all-face handler exchange.

**Fault clues:** A flowing block that never resolves is not itself a source; a full destination backs up the drain; a non-placeable fluid cannot be discharged into the world.

## Card W-03: distillation

```text
[crude tank]====[distillation furnace]====[refined tank]
                         ^
                      solid fuel
```

Supply exactly 2,000 mB crude oil and enough known furnace fuel. Expect 1,000 mB refined oil under the default recipe. Record input, output, burn time, and progress at mid-cycle, then perform both reload types. Repeat with the steam and electric distillers to prove the shared recipe registry separately from each power system.

## Card W-04: conveyors and filters

```text
Plan

[input chest] -> -> [category filter] -> [forward chest]
                         |
                         v
                    [sorted chest]
```

Feed one stack that matches the selected filter and one that does not. Repeat for block, food, fuel, inventory, ore, plant, and smeltable filters. For overflow, fill the lower chest completely and confirm excess continues forward. Include an upward conveyor section and verify inventory extraction behind and insertion ahead.

**Expected:** No item duplication or silent deletion. Record category edge cases rather than inferring them from an item's appearance.

## Card S-01: boiler calibration

Test each boiler with a short pipe and isolated steam tank:

```text
[water]====[boiler]--steam--[tank]
                ^
        fuel / heat / electricity
```

- Coal: use a known fuel stack and record water, burn ticks, and tank rise.
- Electric: supply stable electricity and verify the 16:0.5 per-tick input/output relationship over a long enough interval.
- Geothermal: test one adjacent fire, then controlled lava. Once the gauge is clearly warm, remove the heat source, record the exact temperature immediately, and perform a full-process restart. Compare both NBT and the GUI before the boiler can reheat.
- Oil: use exactly 100 mB increments of a configured fuel, then a fluid with an unlisted registry name.

**Expected:** An isolated tank rises; under load a low boiler gauge may be normal. The `SA-110-002` regression specifically requires a full-process geothermal restart and must retain a temperature above zero.

## Card S-02: steam topology and gauge synchronization

```text
Plan

                         [drill C]
                            |
[source]--[tank]--[drill A]--[drill B]--[drill D]
                            |
                       [steam switch]
```

1. Use an infinite steam source for topology isolation, then repeat with a real boiler.
2. Open every drill GUI and record its local gauge while all drills work.
3. Save at a known block target; run both reload types without touching the network.
4. Confirm work continues and each GUI updates after at least two network intervals.
5. Apply redstone to the switch, then remove it. Break and replace one pipe only as a separate recovery test.

**Expected:** All connected consumers eventually display/refill according to demand. One machine in the middle can conduct to another even if its own gauge is low. Animation alone is insufficient evidence; count broken blocks and output.

## Card S-03: steam tank priority and scarcity

Connect one boiler, one tank, and two consuming machines. Run the boiler below combined demand. Observe which local buffers fill and whether the backup-priority tank charges only after ordinary demand. Apply redstone to the tank and prove it stops supplying while the remaining network continues.

## Card S-04: blast furnace and crusher

For the blast furnace, load three identical smeltable stacks plus measured solid fuel. Compare completion time without steam and with stable steam. All three output lanes should advance without crossing items.

For the crusher, run known Base Metals hammer recipes and a vanilla fallback candidate. Fill all five output slots before a result completes and prove the input is retained. Check comparator output at empty, one item, half stack, and full stack for `SA-110-003`. Wait at least two redstone ticks after each inventory change, and use a one-item vanilla chest beside the same line as an orientation and signal-strength control.

## Card S-05: steam drill, six facings, and travel

Build one drill for each `EnumFacing` direction in a disposable stone volume. Give every drill a rear chest or conveyor and a stable steam connection.

```text
Horizontal elevation

[output chest]<-[drill]>>> bore direction
                    |
              aligned steam track

Vertical elevation

       [rear output/service]
              [drill]
                 v
             bore shaft
```

1. Record facing, local steam, inventory, target block, and progress.
2. Confirm drops enter the drill/rear output rather than appearing at the bit.
3. Save mid-block and perform both reload types.
4. Confirm the bit restores along the original axis and work resumes.
5. Let the drill enter a track. Confirm old/new blocks, facing, inventory, energy, target, and output all remain coherent.
6. Fill output slots and prove mining pauses without deleting blocks or drops.

**Expected:** No drill vanishes; all six facings survive; horizontal and vertical drills behave equivalently except for the geometry of their bore.

## Card S-06: steam pump

Place a pump above a bounded 3x3 water pool with an output tank. Mark the count of pump-pipe sections and vertical lift. Let the internal tank become non-empty and record its exact contents. Stop the pump, isolate or remove the source water without altering its internal tank, save, and perform a full-process restart. This isolation matters because retained steam can otherwise pump replacement water immediately after loading and hide a persistence failure.

**Expected:** Fluid remains and pumping resumes without replacing the pump or pipe. SteamAdvantage `2.2.2.110021` repairs `SA-110-001`; any recurrence is a release-blocking regression.

## Card S-07: elevator

Build a clear shaft 16 blocks high with solid ceilings at selected heights. Test up/down GUI selection and a redstone pulse. Confirm the platform stops two blocks below the ceiling, consumes 32 steam per move, and neither duplicates nor strands its managed platform after reload.

## Card S-08: musket

In a protected range, measure load interaction, cartridge consumption, 64-block maximum trace, block interactions, configured damage, and durability. Repeat separately with Rapid Reload, Powderless, High Explosive I/V, and Recoil I/V. Keep explosive trials away from the works.

## Card E-01: generators

- **Photovoltaic:** Record output at dawn, noon, dusk, rain, thunder, roofed darkness, and controlled block light.
- **Hydroelectric:** Use a bounded horizontal channel so water crosses the turbine rather than falling beneath it. Prove turbine placement, 4-per-tick output, entity damage, chunk reload, and turbine-parent restoration. Confirm right-click behavior for `EA-110-003`; after repair, compare measured output with a normal-width GUI bar.
- **Steam-powered:** Supply measured steam and consume measured electricity. Confirm `31.25` electricity per steam and distinguish the 32-steam network-update cap from per-game-tick work.

Run every generator isolated into a battery array before connecting machines.

## Card E-02: batteries, switch, and machine chain

Charge one of each battery fully, record item NBT/tooltip charge, save, restart Minecraft, and recheck. Put all four in an eight-slot battery array with known partial charges. Apply redstone and prove output stops; remove it and prove recovery without block replacement.

Then build:

```text
[generator]--[array]--[crusher]--[arc furnace]--[assembler]
                    input/output by conveyors and chests
```

The touching machines intentionally test machine-to-machine conduction. Compare it with an equivalent line containing cable between every machine.

## Card E-03: processing inventory and progress

For crusher, arc furnace, oven, assembler, refinery, and still:

1. Begin valid work and stop at roughly half progress.
2. Record every input/output slot, tank, energy, and GUI progress display.
3. Full-restart the client and confirm exact restoration or documented restart behaviour.
4. Fill outputs and verify no input or fluid is consumed without room for results.
5. Test redstone inhibition and recovery.

For the assembler, first use a one-step vanilla recipe, then a bounded nested recipe. Do not test compressed metal/storage blocks in a valued world; use the isolated `EA-110-001` rig.

## Card E-04: electric pump and distillation fluids

Repeat Card S-06 with the electric pump, measuring 100 per pipe, 32 per lift, and 1,000 base energy against actual geometry. Run crude/refined oil through still and refinery with exact mB accounting. Test a configured alternate fluid name.

## Card E-05: growth chambers

```text
Plan

[water]====[controller]--electric cable
                 |
              [chamber]-[chamber]-[chamber]
                            |
                         [chamber]
```

To characterize the whole-block refill cycle, initially leave electricity disconnected. Load one dirt, confirm from a checkpoint that it becomes 1,000 internal soil, then put a second dirt in the controller's soil slot. It should wait while fewer than 1,000 units fit. Connect measured electricity and verify that active consumption eventually takes soil below 500 and causes the waiting dirt to be accepted. The GUI has no numeric soil display, so use server NBT for threshold measurements. Then run known seed, crop, sapling, and other recognised plant inputs in separate chambers, extend the contiguous chain until one chamber starves, and calculate whether supply explains it. This procedure closed `EA-110-002` as coherent refill hysteresis.

## Card E-06: electric drill

Repeat the six-facing and track procedure from Card S-05. Record the 24-per-progress and 250-per-move energy costs, all six outputs, target coordinates, and post-restart orientation. Include old-world blocks placed before the maintained vertical-facing repair.

## Card E-07: LED and laser turret

For the LED, verify high-priority draw, light level response, interruption, redstone switch control, chunk reload, and full restart.

For the turret, use a creative test enclosure with hostile mobs, an owner, a same-team player, and a non-team test account only if consent and safety permit. Check 16-block acquisition, 32-block ray, charge timing, line penetration, 1,000-per-shot cost, idle/active demand, ownership/team persistence, and safe target reacquisition after restart.

## Card E-08: assembler timeout hypothesis

This is a destructive diagnostic for a copied world only.

1. Set recursion limit to 1 and reference a simple reversible storage-block recipe.
2. Provide exactly bounded ingredients and disconnect all other automation.
3. Capture server tick time and latest log before inserting the reference.
4. Repeat at recursion limits 2 and 5 only if the server remains responsive.
5. Stop immediately on sustained tick stall; preserve the world copy and log.

The source contains an explicit warning that metal blocks can cause a server hang/time-out. Passing one material does not clear the wider recipe graph.

## Thirty-minute endurance schedule

Use bounded inputs so conservation can be checked.

| Minute | Action |
| ---: | --- |
| 0 | Photograph and record every inventory, tank, local buffer, switch, and machine coordinate. |
| 5 | Verify throughput totals and check log for errors. |
| 10 | Redstone-isolate one branch for 30 seconds, then restore it. |
| 15 | Leave the area to unload chunks; return after two minutes. |
| 20 | Save to title and reload. |
| 25 | Create temporary resource scarcity, then restore supply. |
| 30 | Stop inputs, let in-flight work settle, and reconcile all material/fluid totals. |

After the first endurance pass, repeat with a complete Minecraft restart at minute 20. Record item/fluid gains and losses separately from differences in GUI display.

## Fault-finding table

| Symptom | First checks |
| --- | --- |
| Gauge is zero but machine animates | Reopen GUI; wait 16 ticks; count real work; inspect local buffer synchronization and shared-machine conduction. |
| Network wakes only after replacing a pipe | Run full restart test; inspect network-cache/reconnection logs; separate topology from GUI sync. |
| Boiler never gains pressure | Isolate loads; check water and actual fuel/heat/electricity; inspect redstone switch. |
| Pump extends but tank is empty after restart | Compare NBT keys; rerun Steam pump regression `SA-110-001`; prove output tank capacity. |
| Drill destroys blocks but no drops are visible | Inspect internal output and rear chest/conveyor; check output fullness and recipe/drop eligibility. |
| Horizontal or vertical drill changes direction | Record block metadata and `Facing` NBT before/after both reload types. |
| Middle machine reads empty while downstream works | Machine-to-machine conduction can pass through a consumer; distinguish its local demand/display from network continuity. |
| Electric plant flickers under load | Measure generation over eight-tick intervals, demand priorities, battery state, and weather. |
| Growth controller rejects another dirt block | Record soil before insertion; exercise `EA-110-002`. |
| Fluid has the right appearance but recipe/fuel fails | Record exact `Fluid.getName()` registry name and compare configuration. |
The packaged-current profile did not reproduce `EA-110-001` at the default recursion limit of 5 using vanilla iron, BaseMetals copper and starsteel storage blocks, or a deeper Electric Fabricator target. Repeat the matrix against the original release and packaged-legacy profile before closing the historical TODO; a clean current-profile run does not prove every mod-supplied recipe graph safe.
