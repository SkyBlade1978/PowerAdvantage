package com.mcmoddev.advantageworks;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WorksDefinitionTest {
    @Test
    public void completeWorksCoversEveryRegisteredComponent() {
        WorksDefinition definition = WorksDefinitionLoader.load();
        assertEquals(20, definition.stations.size());

        Set<String> blocks = new HashSet<>();
        Set<String> items = new HashSet<>();
        Set<String> ids = new HashSet<>();
        StringBuilder manual = new StringBuilder();
        for (WorksDefinition.Station station : definition.stations) {
            assertTrue(ids.add(station.id));
            assertTrue(station.id, !station.assertions.isEmpty());
            assertTrue(station.id, !station.manualChecks.isEmpty());
            for (WorksDefinition.Placement placement : station.placements) blocks.add(placement.block);
            collectActions(station.setupActions, blocks, items);
            collectActions(station.startActions, blocks, items);
            collectActions(station.stopActions, blocks, items);
            for (WorksDefinition.Assertion assertion : station.assertions) {
                if (assertion.block != null) blocks.add(assertion.block);
                if (assertion.item != null && !"modLoaded".equals(assertion.type)) items.add(assertion.item);
            }
            for (String check : station.manualChecks) manual.append(check).append('\n');
        }

        assertContainsAll(blocks,
                "poweradvantage:fluid_drain", "poweradvantage:fluid_discharge",
                "poweradvantage:fluid_storage_tank", "poweradvantage:fluid_metal_tank",
                "poweradvantage:still", "poweradvantage:fluid_pipe",
                "poweradvantage:fluid_pipe_terminal", "poweradvantage:fluid_switch",
                "poweradvantage:item_conveyor", "poweradvantage:item_filter_block",
                "poweradvantage:item_filter_food", "poweradvantage:item_filter_fuel",
                "poweradvantage:item_filter_inventory", "poweradvantage:item_filter_ore",
                "poweradvantage:item_filter_plant", "poweradvantage:item_filter_smelt",
                "poweradvantage:item_filter_overflow", "poweradvantage:steel_frame",
                "poweradvantage:infinite_steam", "poweradvantage:infinite_electricity",
                "poweradvantage:infinite_quantum", "poweradvantage:crude_oil",
                "poweradvantage:refined_oil", "poweradvantage:converter_rf_steam",
                "poweradvantage:converter_rf_electricity", "poweradvantage:converter_rf_quantum",
                "poweradvantage:converter_tr_electricity");
        assertContainsAll(blocks,
                "steamadvantage:steam_pipe", "steamadvantage:steam_track",
                "steamadvantage:steam_boiler_coal", "steamadvantage:steam_boiler_electric",
                "steamadvantage:steam_boiler_geothermal", "steamadvantage:steam_boiler_oil",
                "steamadvantage:steam_tank", "steamadvantage:steam_furnace",
                "steamadvantage:steam_crusher", "steamadvantage:steam_drill",
                "steamadvantage:steam_elevator", "steamadvantage:steam_switch",
                "steamadvantage:steam_still", "steamadvantage:steam_pump",
                "steamadvantage:drillbit", "steamadvantage:platform",
                "steamadvantage:pump_pipe_steam");
        assertContainsAll(blocks,
                "electricadvantage:electric_conduit", "electricadvantage:li_ore",
                "electricadvantage:sulfur_ore", "electricadvantage:electric_track",
                "electricadvantage:laser_turret", "electricadvantage:laser_turret_evil",
                "electricadvantage:led_bar", "electricadvantage:photovoltaic_generator",
                "electricadvantage:hydroelectric_generator", "electricadvantage:steam_powered_generator",
                "electricadvantage:electric_furnace", "electricadvantage:electric_battery_array",
                "electricadvantage:electric_crusher", "electricadvantage:electric_drill",
                "electricadvantage:electric_fabricator", "electricadvantage:growth_chamber",
                "electricadvantage:growth_chamber_controller", "electricadvantage:electric_oven",
                "electricadvantage:electric_switch", "electricadvantage:electric_pump",
                "electricadvantage:electric_still", "electricadvantage:plastic_refinery",
                "electricadvantage:pump_pipe_electric");

        assertContainsAll(items,
                "poweradvantage:starch", "poweradvantage:bioplastic_ingot",
                "poweradvantage:sprocket", "poweradvantage:rotator_tool",
                "steamadvantage:steam_governor", "steamadvantage:steam_drill_bit",
                "steamadvantage:blackpowder_cartridge", "steamadvantage:musket",
                "electricadvantage:blank_circuit_board", "electricadvantage:control_circuit",
                "electricadvantage:integrated_circuit", "electricadvantage:psu",
                "electricadvantage:silicon_ingot", "electricadvantage:silicon_mix",
                "electricadvantage:solder_mix", "electricadvantage:solder",
                "electricadvantage:lead_acid_battery", "electricadvantage:nickel_hydride_battery",
                "electricadvantage:alkaline_battery", "electricadvantage:lithium_battery",
                "electricadvantage:li_ingot", "electricadvantage:li_powder",
                "electricadvantage:li_nugget", "electricadvantage:sulfur_powder",
                "electricadvantage:petrolplastic_ingot");

        assertTrue(manual.toString().contains("SA-110-001"));
        assertTrue(manual.toString().contains("SA-110-002"));
        assertTrue(manual.toString().contains("SA-110-003"));
        assertTrue(manual.toString().contains("EA-110-001"));
        assertTrue(manual.toString().contains("EA-110-002"));
        assertTrue(manual.toString().contains("EA-110-003"));

        assertAssertion(definition, "S-01", "geothermal-temperature-persisted", "nbtNumber");
        assertAssertion(definition, "S-04", "crusher-one-item-signal", "comparatorOutput");
        assertAssertion(definition, "S-06", "pump-fluid-persisted", "nbtNumber");
        assertAssertion(definition, "E-01", "hydroelectric-active", "block");
        assertAssertion(definition, "E-01", "hydroelectric-battery-charged", "inventoryNbtNumber");

        Set<String> enclosedStations = new HashSet<>();
        for (WorksDefinition.Station station : definition.stations) {
            if (station.enclosed) enclosedStations.add(station.id);
        }
        assertEquals(new HashSet<>(Arrays.asList(
                "S-05", "S-06", "S-07", "S-08", "E-01", "E-06", "E-07")), enclosedStations);
    }

    private static void collectActions(Iterable<WorksDefinition.Action> actions,
                                       Set<String> blocks, Set<String> items) {
        for (WorksDefinition.Action action : actions) {
            if (action.block != null) blocks.add(action.block);
            if (action.item != null) items.add(action.item);
        }
    }

    private static void assertContainsAll(Set<String> actual, String... expected) {
        Set<String> missing = new HashSet<>(Arrays.asList(expected));
        missing.removeAll(actual);
        assertTrue("Missing component coverage: " + missing, missing.isEmpty());
    }

    private static void assertAssertion(WorksDefinition definition, String stationId,
                                        String assertionId, String assertionType) {
        for (WorksDefinition.Station station : definition.stations) {
            if (!stationId.equals(station.id)) continue;
            for (WorksDefinition.Assertion assertion : station.assertions) {
                if (assertionId.equals(assertion.id)) {
                    assertEquals(assertionType, assertion.type);
                    return;
                }
            }
        }
        throw new AssertionError("Missing assertion " + stationId + "/" + assertionId);
    }
}
