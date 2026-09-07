# Technical Ledger

> **Rated figures:** Unless a row says otherwise, values are defaults from the pinned 1.10.2 source, not promises made by a particular modpack configuration.

[Handbook index](README.md) | [Commissioning](commissioning-and-fault-finding.md) | [Inspector's register](possible-legacy-bugs.md)

## Time, units, and distribution

| Quantity | Value and meaning |
| --- | --- |
| Minecraft game rate | Nominally 20 game ticks/second. |
| Power/network update | Every 8 game ticks per powered tile, spatially staggered; nominally 0.4 seconds. |
| Machine tick | Many work loops run each game tick against a local buffer. |
| Default maximum power flux | 1,000,000 units per network update unless a machine overrides it. |
| Default maximum fluid flux | 1,000,000 mB per network update unless overridden. |
| Steam local decay | 0.0625 steam per game tick where `energyDecay()` is called, nominally 1.25/second. |
| Pipe-distance loss | None in maintained 1.10.2 code. |

Power requests sort first by descending byte priority and, at equal priority, by descending amount:

| Name | Value |
| --- | ---: |
| `FIRST_PRIORITY` | 127 |
| `HIGH_PRIORITY` | 100 |
| `MEDIUM_PRIORITY` | 50 |
| `LOW_PRIORITY` | 0 |
| `BACKUP_PRIORITY` | -50 |
| `LAST_PRIORITY` | -127 |

A source scans its connected typed network and satisfies sorted requests until supply or eligibility is exhausted. A storage block normally asks at backup priority and refuses to feed another backup-level request, preventing batteries/tanks from merely passing stored power among themselves.

## Typed conduits

| Type name | Ordinary carriers/users |
| --- | --- |
| `fluid` / general-fluid representation | Power Advantage fluid pipe and fluid machines; a carried fluid can also be represented as its specific fluid conduit type internally. |
| `steam` | Steam pipes, boilers, steam tank, steam plant, creative source, converters. |
| `electricity` | Electric conduits, generators, batteries, electric plant, LEDs, creative source, converters. |
| `greenhouse` | Growth controller output and growth chambers. |
| `quantum` | Creative source and optional RF converter; no ordinary production chain in these three mods. |

Machine-to-machine conduction follows compatible conduit types. A visible conduit is not required between touching compatible machines.

## Power Advantage identities

### Blocks and fluids

| Registry name | Status |
| --- | --- |
| `poweradvantage:fluid_drain` | Player fluid machine |
| `poweradvantage:fluid_discharge` | Player fluid machine |
| `poweradvantage:fluid_storage_tank` | Player portable 4,000 mB tank |
| `poweradvantage:fluid_metal_tank` | Player fixed 10,000 mB tank |
| `poweradvantage:still` | Player solid-fuel distillation furnace |
| `poweradvantage:fluid_pipe` | Player general-fluid conduit; ore name `pipe` |
| `poweradvantage:fluid_pipe_terminal` | Hidden automatic Forge-fluid adapter |
| `poweradvantage:fluid_switch` | Player redstone cut-out |
| `poweradvantage:item_conveyor` | Player item conveyor |
| `poweradvantage:item_filter_block` | Player category filter |
| `poweradvantage:item_filter_food` | Player category filter |
| `poweradvantage:item_filter_fuel` | Player category filter |
| `poweradvantage:item_filter_inventory` | Player category filter |
| `poweradvantage:item_filter_ore` | Player category filter |
| `poweradvantage:item_filter_plant` | Player category filter |
| `poweradvantage:item_filter_smelt` | Player category filter |
| `poweradvantage:item_filter_overflow` | Player overflow filter |
| `poweradvantage:steel_frame` | Player structure; ore name `frameSteel` |
| `poweradvantage:infinite_steam` | Creative test source |
| `poweradvantage:infinite_electricity` | Creative test source |
| `poweradvantage:infinite_quantum` | Creative test source |
| `poweradvantage:crude_oil` | Fluid and placeable block; registry fluid `crude_oil` |
| `poweradvantage:refined_oil` | Fluid and placeable block; registry fluid `refined_oil` |

Optional registered blocks: `converter_rf_steam`, `converter_rf_electricity`, `converter_rf_quantum`, and `converter_tr_electricity` in the `poweradvantage` domain.

Fluid properties: crude oil has density 850, viscosity 6,000, temperature 300 K, and luminosity 0; refined oil has density 720, viscosity 1,000, temperature 300 K, and luminosity 0. With `use_other_fluids=true`, an existing fluid under the same registry name can replace these locally created definitions.

### Items

| Registry name | Ore names |
| --- | --- |
| `poweradvantage:starch` | `starch` |
| `poweradvantage:bioplastic_ingot` | `plastic`, `ingotPlastic`; optionally `rubber`, `ingotRubber` |
| `poweradvantage:sprocket` | `sprocket`, `gear`, `sprocketSteel`, `gearSteel` |
| `poweradvantage:rotator_tool` | none |

### Tile entity IDs

`poweradvantage.tileentity.pipe_terminal`, `poweradvantage.tileentity.fluid_drain`, `poweradvantage.tileentity.fluid_discharge`, `poweradvantage.tileentity.fluid_storage_tank`, `poweradvantage.tileentity.fluid_metal_tank`, `poweradvantage.tileentity.still`, `poweradvantage.item_conveyor`, all eight matching `poweradvantage.item_filter_*` IDs, and `poweradvantage.infinite_energy_source`.

Optional converter IDs are `poweradvantage.rf_steam_converter_tileentity`, `poweradvantage.rf_electricity_converter_tileentity`, `poweradvantage.rf_quantum_converter_tileentity`, and `poweradvantage.tr_electricity_converter_tileentity`.

## Power Advantage recipes

Pattern notation lists rows top to bottom, separated by `/`. Names such as `plateSteel` are ore-dictionary keys.

### Common recipes

| Output | Pattern or process |
| --- | --- |
| Starch x1 | Crusher: `potato -> starch`; both potato and poisonous potato are registered as `potato`. |
| Bioplastic x1 | Furnace: starch x1, 0.1 XP. |
| Rotation tool x1 | `xx/x*/ x`, where `x=ingotIron`, `*=sprocket`. |
| Conveyor x5 | `xxx/ghg/xxx`, `x=plateSteel`, `g=sprocket`, `h=hopper`. |
| Block/Food/Fuel/Inventory/Ore/Plant/Smelt filters | `x/y/z`, `x=conveyor`, `z=wooden pressure plate`, with `y` respectively `stone`, `bread`, `coal`, `chest`, `ingotGold`, `treeSapling`, `furnace`. |
| Overflow filter | `x/z`, `x=conveyor`, `z=wooden pressure plate`. |
| Steel frame | `xxx/x x/xxx`, `x=barsSteel`. |
| Portable tank | `xxx/xpx/xxx`, `x=ingotPlastic`, `p=pipe`. |
| Metal tank | `xxx/xpx/xxx`, `x=ingotSteel`, `p=pipe`. |
| Drain | ` x /w#w/ppp`, `x=bars`, `w=plateSteel`, `#=frameSteel`, `p=pipe`. |
| Discharge | `ppp/w#w/ x ` with the same keys. |
| Distillation furnace | `bpb/ f `, `b=bucket`, `p=pipe`, `f=furnace`. |
| Fluid switch | ` l /pfp`, `l=lever`, `p=pipe`, `f=frameSteel`. |

### Mode-specific recipes

| Mode | Registrations |
| --- | --- |
| `NORMAL` | Sprocket x4: ` x /x/x/ x ` with `x=ingotSteel`, centre `stickWood` or `rod`; fluid pipe x6: `xxx/   /xxx` from iron or copper ingots; an iron version of the metal tank. |
| `TECH_PROGRESSION` | Sprocket x4 with steel and `rod`, or bronze nugget fallback if ore key `rod` is empty; iron fluid pipe x6; Base Metals strong-hammer option is disabled through compatibility reflection. |
| `APOCALYPTIC` | Fluid pipe x3 from iron, copper, or lead in the six-ingot pipe pattern; crusher salvage of one steel plate from each conveyor/filter when `plateSteel` exists; portable tank -> one pipe; drain/discharge -> two pipes. |

### Optional converter recipes

- RF steam: shapeless-style row `xyz`, `x=governor`, `y=frameSteel`, `z=blockRedstone`.
- RF electricity: `x=PSU`, `y=frameSteel`, `z=blockRedstone`.
- RF quantum: `x=ender pearl`, `y=frameSteel`, `z=blockRedstone`.
- Tech Reborn electricity: `XXX/XZX/XXX`, `X=ingotRefinedIron`, `Z=PSU`.

## Steam Advantage identities

### Blocks

| Registry name | Status |
| --- | --- |
| `steamadvantage:steam_pipe` | Player conduit; ore name `conduitSteam` |
| `steamadvantage:steam_track` | Player drill track/service conduit |
| `steamadvantage:steam_boiler_coal` | Player boiler |
| `steamadvantage:steam_boiler_electric` | Player boiler |
| `steamadvantage:steam_boiler_geothermal` | Player boiler |
| `steamadvantage:steam_boiler_oil` | Player boiler |
| `steamadvantage:steam_tank` | Player storage |
| `steamadvantage:steam_furnace` | Player blast furnace |
| `steamadvantage:steam_crusher` | Player crusher |
| `steamadvantage:steam_drill` | Player drill |
| `steamadvantage:steam_elevator` | Player elevator controller |
| `steamadvantage:steam_switch` | Player redstone cut-out |
| `steamadvantage:steam_still` | Player distiller |
| `steamadvantage:steam_pump` | Player pump |
| `steamadvantage:drillbit` | Managed drill extension helper |
| `steamadvantage:platform` | Managed elevator platform helper |
| `steamadvantage:pump_pipe_steam` | Managed pump shaft helper |

### Items and enchantments

| Identity | Details |
| --- | --- |
| `steamadvantage:steam_governor` | Ore names `governor`, `governorBrass` |
| `steamadvantage:steam_drill_bit` | Drill construction item |
| `steamadvantage:blackpowder_cartridge` | Ore name `ammoBlackpowder` |
| `steamadvantage:musket` | Ore name `gun`; item NBT carries loading state |
| `steamadvantage:high_explosive` | Enchantment levels I-V |
| `steamadvantage:powderless` | Enchantment level I |
| `steamadvantage:rapid_reload` | Enchantment level I |
| `steamadvantage:recoil` | Enchantment levels I-V |

### Tile entity IDs

`steamadvantage.steam_boiler_coal`, `steamadvantage.steam_boiler_electric`, `steamadvantage.steam_boiler_geothermal`, `steamadvantage.steam_tank`, `steamadvantage.steam_furnace`, `steamadvantage.steam_crusher`, `steamadvantage.steam_drill`, `steamadvantage.steam_elevator`, `steamadvantage.drillbit`, `steamadvantage.steam_still`, `steamadvantage.steam_pump`, and `steamadvantage.steam_boiler_oil`.

## Steam rated plant

| Plant | Local capacities | Consumption/output |
| --- | --- | --- |
| Coal boiler | steam 1,000; fluid 1,000; water tank 4,000 mB | Fuel-dependent; lava path requests 100 mB and grants 16 burn ticks/mB. |
| Electric boiler | steam 100; electricity 160; fluid 1,000; water 2,000 mB | 16 electricity/tick -> 0.5 steam/tick. |
| Geothermal boiler | steam 100; fluid 1,000; water 2,000 mB | Heat fluid `(T-372)*0.5`; steam/tick `0.001*T`; 24 heat/steam; max T=2,000. |
| Oil boiler | steam/fluid 1,000; water/fuel tanks 4,000 mB each | Burns configured fuel in 100 mB portions. |
| Steam tank | steam 10,000 | Backup-priority store; redstone disables output; local decay. |
| Blast furnace | steam 200; 1 fuel + 3 input + 3 output | 0.5 steam/active tick; heat +2.5 or +15; progress `3.5e-6*T`; T 100..2,000. |
| Rock crusher | steam 50; 1 input + 5 output | 1.5 steam/progress tick; 125 progress ticks. |
| Steam drill | steam 50; 6 output | 2.5 extension; 1/progress tick; 10/move; range 64; hardness factor 32. |
| Elevator | steam 64 | 32/move; maximum range 16. |
| Pump | steam 300; fluid 1,000; tank 1,000 mB | 5/pipe + 1/lift + 32 base; scan 125; intervals 32/11 ticks. |
| Steam still | steam 100; fluid 1,000; tanks 1,000 mB each | 2 recipe units and 1.5 steam per active tick, plus local steam decay. |

## Steam Advantage recipes

Generic one-component steam machine pattern: `gXg/pmp`, where `g=governor`, `p=plateIron`, `m=frameSteel`, and `X` is the listed component. Two-component pattern: ` Y /gXg/pmp`.

| Output | Component(s) or exact pattern |
| --- | --- |
| Steam pipe x6 | `xxx/   /xxx`, `x=ingotBrass`. |
| Steam crusher | piston + blockSteel. |
| Blast furnace | furnace. |
| Coal boiler | conduitSteam. |
| Steam drill | steam drill bit; `NORMAL` also diamond pickaxe. |
| Elevator | piston + sprocket. |
| Electric boiler | wire + bucket. |
| Geothermal boiler | conduitSteam + blockObsidian. |
| Oil boiler | furnace + bucket. |
| Pump | piston + bucket. |
| Steam still | bucket + bucket. |
| Steam tank | `xgx/xpx/xxx`, `x=plateCopper`, `p=conduitSteam`, `g=governor`. |
| Steam track | Shapeless: steam pipe + steel frame. |
| Steam switch | ` L /pfp`, `L=lever`, `p=conduitSteam`, `f=frameSteel`. |
| Steam drill bit | ` g / i /did`, `g=sprocket`, `i=ingotSteel`, `d=gemDiamond`. |
| Cartridge | `L/g/p`, `L=nuggetLead`, `g=gunpowder`, `p=paper`. |

Mode rules:

- `NORMAL` governor: ` t /srs/btb`, `t=nuggetIron`, `s/r=stick`, `b=ingotBrass`.
- `TECH_PROGRESSION` and `APOCALYPTIC` governor: same pattern with `t=sprocket`, `s/r=rod`.
- Musket, when enabled in `NORMAL`/`TECH_PROGRESSION`: mirrored `fss/w  ` or `ssf/  w`, `f=flint and steel`, `s=ingotSteel`, `w=plankWood`.
- `APOCALYPTIC` crusher salvage: governor -> two sprockets; crusher, furnace, coal/electric/geothermal boilers, and drill -> two governors; elevator -> three; tank -> one.

## Electric Advantage identities

### Blocks

| Registry name | Status |
| --- | --- |
| `electricadvantage:electric_conduit` | Player conductor |
| `electricadvantage:li_ore` | Player/world ore |
| `electricadvantage:sulfur_ore` | Player/world ore |
| `electricadvantage:electric_track` | Player drill track/conductor |
| `electricadvantage:laser_turret` | Player defence |
| `electricadvantage:laser_turret_evil` | Hidden hostile/test variant |
| `electricadvantage:led_bar` | Player conductor/light |
| `electricadvantage:photovoltaic_generator` | Player generator |
| `electricadvantage:hydroelectric_generator` | Player generator/controller |
| `electricadvantage:steam_powered_generator` | Player converter/generator |
| `electricadvantage:electric_furnace` | Player arc furnace |
| `electricadvantage:electric_battery_array` | Player storage |
| `electricadvantage:electric_crusher` | Player crusher |
| `electricadvantage:electric_drill` | Player drill |
| `electricadvantage:electric_fabricator` | Player automated assembler |
| `electricadvantage:growth_chamber` | Player agriculture machine |
| `electricadvantage:growth_chamber_controller` | Player agriculture controller |
| `electricadvantage:electric_oven` | Player food oven |
| `electricadvantage:electric_switch` | Player redstone cut-out |
| `electricadvantage:electric_pump` | Player pump |
| `electricadvantage:electric_still` | Player distiller |
| `electricadvantage:plastic_refinery` | Player refinery |
| `electricadvantage:pump_pipe_electric` | Managed pump shaft helper |

### Items

Registered item paths are `blank_circuit_board`, `control_circuit`, `integrated_circuit`, `psu`, `silicon_ingot`, `silicon_mix`, `solder_mix`, `solder`, `li_ingot`, `li_powder`, `li_nugget`, `sulfur_powder`, `petrolplastic_ingot`, `lead_acid_battery`, `nickel_hydride_battery`, `alkaline_battery`, and `lithium_battery`, all under `electricadvantage`.

### Tile and entity IDs

Generated tile IDs are `electricadvantage.electric_furnace`, `.electric_crusher`, `.electric_drill`, `.electric_fabricator`, `.growth_chamber`, `.growth_chamber_controller`, `.electric_oven`, `.laser_turret`, `.l_e_d`, `.hydroelectric_generator`, `.photovoltaic_generator`, `.steam_powered_electric_generator`, `.electric_battery_array`, `.electric_pump`, `.electric_still`, and `.plastic_refinery`.

The registered hydroturbine entity is the mod entity derived from `HydroturbineEntity`, numeric mod-local ID 0, tracking range 64, update frequency 1, velocity updates enabled.

There are no Electric Advantage enchantments. PCB Printer and Geothermal Generator strings in the language file do not correspond to registered blocks.

## Electric rated plant

| Plant | Capacity/inventory | Consumption/output |
| --- | --- | --- |
| Standard electric machine | electricity 500 | Per-machine values below. |
| Photovoltaic generator | environmental source | Approx. 12,500/day target; rain x0.35, thunder x0.2, blocked-sky block-light x0.1. |
| Hydroelectric generator | turbine entity below | 4 electricity/tick; turbine damage 5 every 8 ticks; environment check 101 ticks. |
| Steam generator | electricity 1,000; steam 32 | 31.25 electricity/steam; up to 32 steam/network update; 0.5 steam decay/update. |
| Battery array | 8 battery slots | Backup-priority request/store; redstone disables output. |
| Arc furnace | 4 input, 6 output | 8 electricity/tick; 200 ticks. |
| Electric crusher | 3 input, 6 output | 12 electricity/tick; per-slot crusher results. |
| Electric oven | 1 input, 1 output | 4 electricity/tick; 100 ticks; food recipes only. |
| Assembler | 12 input, 2 output, 1 reference | 16 electricity/tick; 200 progress ticks; recursion default 5. |
| Electric drill | electricity 500; 6 output | 24/progress tick; 250/move; range 64; hardness factor 32. |
| Electric pump | electricity 9,192; fluid/tank 1,000 mB | 100/pipe + 32/lift + 1,000 base; 1,000 mB/action; scan 125. |
| Electric still | electricity 500; fluid 1,000; tanks 1,000 mB each | 1 recipe unit and 16 electricity per active tick. |
| Plastic refinery | electricity 500; fluid/tank 2,000 mB | 12/tick; 100 ticks; 100 mB/ingot. |
| Growth controller | greenhouse 1,000; electricity 100; fluid 2,000; tank 2,000 mB | 8 electricity + 2 mB water + 1 soil -> 1 greenhouse. |
| Growth chamber | 3 input, 6 output | 1 greenhouse/active slot/tick; 100 ticks/stage. |
| LED bar | electricity 0.5 | 0.125/tick; high-priority request. |
| Laser turret | electricity 2,000 | 1/tick active + 1,000/shot; target 16; ray 32; damage 7. |

Battery capacities are 1,000 lead-acid, 2,000 nickel-hydride, 4,000 alkaline, and 8,000 lithium. Steam/electric conversion constants are 31.25 and 0.032 respectively.

## Electric Advantage recipes

Generic one-component machine pattern: `uX /pmp`, `u=PSU`, `p=plateSteel`, `m=frameSteel`. Two-component form: `uXY/pmp`.

### Materials and conductors

| Output | Pattern/process |
| --- | --- |
| Lithium ingot | Smelt lithium powder or lithium ore, 0.5 XP. |
| Crusher integration | `oreLithium -> 2 li_powder`; `ingotLithium -> 1 li_powder`; `oreSulfur -> 4 sulfur_powder`. |
| Conduit x6 | `xxx/ccc/xxx`, `x=plastic` or `rubber`, `c=ingotCopper`. |
| Conduit x1 | `xx/cc`, `x=plastic` or `rubber`, `c=rodCopper`. |
| Blank boards x2 | Shapeless `plastic + plateCopper`; `NORMAL` also `plastic + ingotCopper`. |
| Control circuit x1 | Shapeless blank board + `microchip` + `solder`. |
| Silicon mix x1 | Shapeless sand + `dustCarbon`; smelt to silicon ingot, 0.5 XP. |
| Solder mix x3 | Shapeless two `dustTin` + `dustLead` or `dustSilver`; smelt to solder, 0.5 XP. |
| LED bars x3 | `ggg/xxx/ccc`, `g=paneGlass`, `x=microchip`, `c=wire`. |
| Electric track | Shapeless electric conduit + steel frame. |

Battery pattern is `pXp/pYp/pZp`, `p=plastic`: lead/sulfur-or-dustSulfur/water; nickel/dustRedstone/water; iron/gunpowder/zinc; lithium/dustRedstone/dustCarbon.

### Mode-dependent electronics

| Mode | Registrations |
| --- | --- |
| `NORMAL` | Integrated circuits x3: `sss/ccc`, `s=ingotSilicon`, `c=nuggetCopper`, `nuggetTin`, or `nuggetGold`; PSU: `wcw/ s ` with `w=wire`, `c=circuitBoard`, `s=ingotSteel` or `ingotIron`; lead/tin/silver ingot copies register as solder-compatible. |
| `TECH_PROGRESSION` | Integrated circuits x3: `prp/sss/ccc`, `p=plastic`, `r=dustRedstone`, `s=ingotSilicon`, `c` one of copper/tin/gold nuggets; PSU uses wire, circuit board, and `plateSteel`. |
| `APOCALYPTIC` | PSU recipe uses wire, circuit board, and `plateSteel`; crusher salvage: steam generator, arc furnace, or photovoltaic -> PSU; PSU -> control circuit; control circuit -> integrated circuit. |

Photovoltaic recipe outside `APOCALYPTIC`: `ggg/sss/wuw`, `g=paneGlass`, `s=ingotSilicon`, `w=wire`, `u=PSU`.

### Machine recipes

| Output | Component(s) or exact pattern |
| --- | --- |
| Steam-powered generator | `conduitSteam` + `governor`. |
| Arc furnace | `bbb/bub/bbb`, `b=blockBrick`, `u=PSU`. |
| Hydroelectric generator | sprocket + sprocket. |
| Battery array | chest. |
| Electric crusher | sprocket + gemDiamond. |
| Laser turret | gemDiamond + gemEmerald, with reversed alternative. |
| Electric drill | blockDiamond. |
| Automated assembler | crafting table. |
| Growth chamber | flower pot + microchip. |
| Growth controller | flower pot + circuitBoard. |
| Electric oven | paneGlass + PSU. |
| Electric switch | ` L /pfp`, `L=lever`, `p=wire`, `f=frameSteel`. |
| Electric still | bucket + bucket. |
| Electric pump | piston + bucket. |
| Plastic refinery | piston + sprocket. |

## Configuration ledger

### Power Advantage

| Category/key | Default/range |
| --- | --- |
| `options.recipe_mode` | `NORMAL`; allowed `NORMAL`, `TECH_PROGRESSION`, `APOCALYPTIC` |
| `options.treasure_chest_loot_factor` | 0.5, range 0..1000 |
| `options.plastic_equals_rubber` | true |
| `recipes.distiller_recipes` | `2*crude_oil->1*refined_oil` in maintained naming |
| `Other Power Mods.use_other_fluids` | false |
| `Other Power Mods.RF_conversions` | `steam=8;electricity=0.25;quantum=8` RF per PA unit |
| `Other Power Mods.TechReborn_conversions` | `electricity=0.25` EU per PA unit |

### Steam Advantage

| Category/key | Default/range |
| --- | --- |
| `options.musket_damage` | 20, range 0..100 |
| `options.musket_reload_time` | 100 ticks, range 20..300 |
| `options.musket_allowed` | true |
| `options.fluid_fuel_values` | `oil=5000;fuel=25000` |

### Electric Advantage

| Category/key | Default/range |
| --- | --- |
| `fabricator.recursion_limit` | 5, range 1..255 |
| `options.laser_sound` | `block.note.bass` |
| `options.petrolplastic_fluids` | `refined_oil;oil` |
| Generated OreSpawn config | `config/orespawn/electricadvantage.json` |

## Persistence ledger

All tile entities also inherit vanilla position/identity data. Shared power machines write typed buffers as `Energy` and inventory as `Items` with per-entry `Slot`; custom names use `CustomName`.

| State | Principal NBT keys/expectation |
| --- | --- |
| Batteries | Item key `energy`. |
| Power fluid tanks | Serialized Forge tank compounds; portable tank also copies contents to dropped item. |
| Still/distillers | `TankIn`, `TankOut`, inventory/progress/burn data as applicable. |
| Coal boiler | `Tank`, `BurnTime`, `BurnTimeTotal`, `Energy`, `Items`. |
| Electric boiler | `Tank`, `Electricity`, steam `Energy`. |
| Geothermal boiler | `Tank`, `Temperature`, `Energy`; `2.2.2.110021` also reads legacy `Tempoerature` for `SA-110-002`. |
| Oil boiler | `WaterTank`, `FuelTank`, `BurnTime`, `BurnTimeTotal`, `Energy`. |
| Steam pump | `Tank`, `NextPump`, `Energy`; `2.2.2.110021` also reads legacy `TankOut` for `SA-110-001`. |
| Electric pump | `Tank`, `NextPump`, `Energy`. |
| Drills | `Energy`, `Items`, `Facing` where used, `progress`, `targetX`, `targetY`, `targetZ`, movement/target reconstruction data. |
| Elevator | `up`, `Energy`, managed platform state. |
| Electric crusher/furnace | Per-slot `smashTime`/`cookTime`, `Energy`, `Items`. |
| Assembler | `progress`, `FSM`, `Energy`, `Items`. |
| Growth controller | `Tank`, `soil`, typed `Energy`, `Items`, request-age synchronization. |
| Growth chamber | `growth`, `prog`, `Energy`, `Items`. |
| Steam generator | `steam`, typed `Energy`. |
| Hydroelectric generator/turbine | `initDone`; turbine `parentPos`. Active-state block changes must preserve the generator tile, or the turbine sees an invalid parent and terminates (`EA-110-004`). |
| Plastic refinery | `Tank`, `progress`, `Energy`, `Items`. |
| Laser turret | `energy`, `lock`, target snapshot, plus persistent `pitch`, `yaw`, `team`, `player`. |
| Musket | Byte loading state: unloaded/loading/loaded. |

## Automation ledger

- Conveyors pull from the inventory behind and offer to the inventory ahead; filters additionally route downward.
- Standard Steam/Electric machine wrappers expose declared input/output slot groups through sided-inventory interfaces. Confirm actual face arrays in a test rig before relying on a compact build.
- Drills retain output internally and offer it to a rear inventory/conveyor path; bit-side world drops are not the intended collection point.
- General-fluid machines implement Forge `IFluidHandler`; terminal fluid pipes bridge adjacent third-party handlers.
- Redstone-powered Power/Steam/Electric switches break their conduit connection. Storage and many machines also use redstone inhibition; each manual entry identifies it where code is clear.
- Comparator support generally reports inventory/tank fill, but the steam crusher formula is under investigation.

## Source map

Paths are relative to each named repository at the pinned commits in the [edition authority](README.md#edition-authority).

| Subject | Defining source |
| --- | --- |
| Tick scheduling and save bridges | Power: `src/main/java/cyano/poweradvantage/api/PoweredEntity.java` |
| Priority and request sorting | Power: `src/main/java/cyano/poweradvantage/api/PowerRequest.java` |
| Shared machine persistence/automation | Power: `src/main/java/cyano/poweradvantage/api/simple/TileEntitySimplePowerMachine.java`, `TileEntitySimpleFluidMachine.java` |
| Conduit discovery/distribution | Power: `src/main/java/com/mcmoddev/poweradvantage/conduitnetwork/ConduitRegistry.java` and adjacent conduit-network classes |
| Power identities and recipes | Power: `init/Blocks.java`, `Items.java`, `Fluids.java`, `Entities.java`, `Recipes.java`, `ModSupport.java` |
| Conveyor and fluid behaviour | Power: `machines/conveyors/*`, `machines/fluidmachines/*` |
| Steam identities/rates | Steam: `init/Blocks.java`, `Items.java`, `Entities.java`, `Power.java`, `Recipes.java`; `machines/*` |
| Musket/enchantments | Steam: `items/MusketItem.java`, `enchantments/*`, `init/Enchantments.java` |
| Electric identities/rates | Electric: `init/Blocks.java`, `Items.java`, `Entities.java`, `Power.java`, `Recipes.java`; `machines/*` |
| Hydroturbine | Electric: `entities/HydroturbineEntity.java`, `machines/HydroelectricGeneratorTileEntity.java` |
| Historical design | DrCyano Wikidot component pages and CurseForge descriptions/galleries linked from the handbook index |
