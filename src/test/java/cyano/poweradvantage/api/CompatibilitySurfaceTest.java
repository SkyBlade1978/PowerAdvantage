package cyano.poweradvantage.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class CompatibilitySurfaceTest {
	@Test
	public void legacyGuiDescriptorsRemainAvailable() throws Exception {
		Class<?> point = Class.forName("com.mcmoddev.poweradvantage.math.Integer2D");
		Class<?> guiInterface = Class.forName("com.mcmoddev.poweradvantage.registry.ITileEntityGUI");
		Class<?> simpleGui = Class.forName("cyano.poweradvantage.api.simple.SimpleMachineGUI");
		Class<?> pointArray = java.lang.reflect.Array.newInstance(point, 0).getClass();

		assertNotNull(simpleGui.getConstructor(ResourceLocation.class, pointArray));
		assertNotNull(simpleGui.getConstructor(String.class, pointArray));
		assertTrue(guiInterface.isAssignableFrom(simpleGui));
	}

	@Test
	public void ironAgeFurnitureReflectionFacadeRemainsAvailable() throws Exception {
		Class<?> registry = Class.forName("com.mcmoddev.poweradvantage.conduitnetwork.ConduitRegistry");
		Class<?> fluids = Class.forName("com.mcmoddev.poweradvantage.init.Fluids");

		assertNotNull(registry.getMethod("getInstance"));
		assertNotNull(registry.getMethod("getRequestsForPower", World.class, BlockPos.class,
			ConduitType.class, ConduitType.class));
		assertNotNull(fluids.getMethod("fluidToConduitType", Fluid.class));
		assertNotNull(fluids.getField("fluidConduit_general"));
	}
}
