package cyano.poweradvantage.api;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FluidCategoryRegistryTest {
    @Test
    public void builtInAndAdditionalCrudeOilAliasesAreAdditive() {
        assertTrue(FluidCategoryRegistry.matches(FluidCategoryRegistry.CRUDE_OIL, "crude_oil"));
        assertTrue(FluidCategoryRegistry.matches(FluidCategoryRegistry.CRUDE_OIL, "mineralogy_crude_oil"));
        assertTrue(FluidCategoryRegistry.matches(FluidCategoryRegistry.CRUDE_OIL, "oil"));
        assertFalse(FluidCategoryRegistry.matches(FluidCategoryRegistry.CRUDE_OIL, "refined_oil"));
        FluidCategoryRegistry.registerAlias(FluidCategoryRegistry.CRUDE_OIL, "example_crude");
        assertTrue(FluidCategoryRegistry.matches(FluidCategoryRegistry.CRUDE_OIL, "example_crude"));
    }
}