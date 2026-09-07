# MMD Material And Fluid Compatibility Specification

## Works standard: Minecraft 1.10.2

> **Stores notice:** One material may have several makers, but the works shall appoint one default mine. Alternative machinery remains on the drawing so the proprietor may put it back into service.

This document is the source-of-truth contract for Power Advantage, Steam Advantage, Electric Advantage, Mineralogy, BaseMinerals, and OreSpawn interoperability. It distinguishes fresh-profile defaults from existing player configuration; it does not rename any registered block, item, fluid, or world data.

## Governing principles

- OreSpawn 4 owns placement. Provider mods declare complete, stable rules through provider schema 4.
- All candidate rules remain visible. A lower-priority candidate is registered with `enabled: false`, not omitted.
- Default ownership is decided only when a rule is first added to a fresh global or world profile.
- `config/orespawn-worldgen.json` supplies installed-pack defaults for new worlds.
- `<world>/serverconfig/orespawn-worldgen.json` is the self-contained authority for an existing world.
- `config/<modid>-orespawn.json` is an authoritative provider override and outranks packaged or API declarations.
- A known disabled or edited rule is not reset when providers are upgraded, added, or removed.
- Retrogen remains disabled for these Advantage rules. Existing chunks do not gain new deposits automatically.

## Fresh-profile ownership

| Material | Priority | Default decision |
| --- | --- | --- |
| Sulfur | Mineralogy, then BaseMinerals, then ElectricAdvantage | Electric sulfur is enabled only when neither `mineralogy` nor `baseminerals` is loaded. |
| Lithium | BaseMinerals, then ElectricAdvantage | Electric lithium is enabled only when `baseminerals` is absent. |
| Ocean crude oil | Mineralogy | Mineralogy's existing deposit remains enabled beneath `OCEAN` biomes. |
| Desert crude oil | PowerAdvantage | Power's complementary deposit is enabled beneath `DESERT` biomes. |

BaseMinerals has not yet been modernized. Until that work is complete, an installation containing both current BaseMinerals and Mineralogy may still generate both mods' sulfur. The later BaseMinerals pass must add its typed provider, preserve its IDs, and implement the priority above.

## Stable provider rules

| Provider rule | Output | Placement |
| --- | --- | --- |
| `poweradvantage:fluid_deposit/desert_crude_oil` | block `poweradvantage:crude_oil`, fluid `crude_oil` | Overworld, Y 0-48, frequency 0.08, radius 5-12, vertical radius 2-5, up to four lobes, two-block cover, one-block shell, `DESERT` biomes. |
| `electricadvantage:ore/sulfur` | `electricadvantage:sulfur_ore` | Overworld, Y 1-31, frequency 1.0, quantity 8-23, default pattern, stone and all OreSpawn geology families. |
| `electricadvantage:ore/lithium` | `electricadvantage:li_ore` | Overworld, Y 1-31, frequency 0.125, quantity 6-9, default pattern, stone and all OreSpawn geology families. |
| `mineralogy:fluid_deposit/crude_oil` | block `mineralogy:crude_oil`, fluid `mineralogy_crude_oil` | Mineralogy's packaged provider; large covered deposits beneath `OCEAN` biomes. |

The Electric quantities and Y range are the exact OreSpawn 4 interpretation of its historical OS1 `size`, `variation`, and exclusive `maxHeight` settings.

## Legacy migration authority

Historical `config/orespawn/poweradvantage.json` and `config/orespawn/electricadvantage.json` files are no longer created for fresh installations. They are deliberately not deleted.

OreSpawn's OS1/OS3 bridge converts an existing file once into `config/<modid>-orespawn.json`. A unique output match keeps the stable provider rule identity; the migrated values remain authoritative and prevent a second active default rule from being added. The source file remains available, and the decision is recorded in `config/orespawn-os3-migration-report.json`.

If an old explicit setting conflicts with the fresh-profile hierarchy, the old setting wins. Change it through OreSpawn's configuration UI or the applicable world/provider profile; do not expect installing or removing a sibling mod to rewrite it.

## Crude-oil category

Power Advantage exposes `cyano.poweradvantage.api.FluidCategoryRegistry` so extensions can match semantic fluid categories without linking to Mineralogy classes.

The built-in `crude_oil` category always contains these fluid registry aliases:

- `crude_oil` - Power Advantage crude oil;
- `mineralogy_crude_oil` - Mineralogy crude oil;
- `oil` - legacy/generic compatibility name.

Additional names may be appended with `compatibility.additional_crude_oil_fluid_aliases` in `poweradvantage.cfg`. Built-ins are never replaced.

Each registered crude alias receives the existing distillation ratio `2 mB crude -> 1 mB refined_oil` unless an explicit distillation recipe already exists for that input. Power's fluid-fuel registry and Steam's oil boiler value every built-in crude alias at 5 ticks per mB, or 5,000 ticks per bucket. Steam's explicit `options.fluid_fuel_values` entry takes precedence.

Electric Advantage's Plastic Refinery remains unchanged. Its accepted inputs are still the configured `options.petrolplastic_fluids` list, historically `refined_oil;oil`; raw crude is not made into plastic by this compatibility pass.

## Ore Dictionary standard

| Material form | Canonical name | Accepted British alias |
| --- | --- | --- |
| Sulfur ore | `oreSulfur` | `oreSulphur` |
| Sulfur dust | `dustSulfur` | `dustSulphur` |
| Sulfur storage block | `blockSulfur` | `blockSulphur` |
| Generic sulfur ingredient | `sulfur` | `sulphur` |
| Lithium ore | `oreLithium` | none |
| Lithium dust | `dustLithium` | none |
| Lithium ingot | `ingotLithium` | none |
| Lithium nugget | `nuggetLithium` | none |
| Lithium storage block | `blockLithium` | none |

Recipes consume canonical names. Compatibility aliases are accepted inputs and never cause registry renames.

## Commissioning matrix

For each row, inspect the global profile before world creation, the saved world profile after creation, and the same profile after a full server restart.

| Installed providers | Fresh sulfur | Fresh lithium | Oil expectation |
| --- | --- | --- | --- |
| Advantage only | Electric enabled | Electric enabled | Power desert oil only |
| Advantage + Mineralogy | Mineralogy enabled; Electric visible disabled | Electric enabled | Mineralogy ocean oil plus Power desert oil |
| Advantage + BaseMinerals | BaseMinerals intended enabled; Electric disabled | BaseMinerals intended enabled; Electric disabled | Power desert oil |
| All providers | Mineralogy intended sulfur; alternatives visible disabled | BaseMinerals intended lithium; Electric disabled | Both biome-specific oils |

Also test an imported legacy Power/Electric file, a manually re-enabled fallback, mod removal/addition, a copied established world, and new chunk generation. Count deposits by biome and dimension; GUI appearance alone is not evidence of generation.

For fluid compatibility, move both crude oils through pipes and tanks, perform a full-process restart, distil exactly 2,000 mB to 1,000 mB refined oil, and burn equal volumes in separate Steam oil boilers. Record all input/output quantities and confirm conservation.