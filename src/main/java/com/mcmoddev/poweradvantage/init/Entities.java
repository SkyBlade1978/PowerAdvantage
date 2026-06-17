package com.mcmoddev.poweradvantage.init;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import com.mcmoddev.poweradvantage.PowerAdvantage;
import com.mcmoddev.poweradvantage.machines.conveyors.*;
import com.mcmoddev.poweradvantage.machines.creative.InfiniteEnergyTileEntity;
import com.mcmoddev.poweradvantage.machines.fluidmachines.*;
import com.mcmoddev.poweradvantage.machines.fluidmachines.modsupport.TerminalFluidPipeTileEntity;

public abstract class Entities {


	private static boolean initDone = false;
	private static boolean dataFixersRegistered = false;

	public static void registerDataFixers() {
		if (dataFixersRegistered) return;
		ModFixs fixes = FMLCommonHandler.instance().getDataFixer().init(PowerAdvantage.MODID, 1);
		fixes.registerFix(FixTypes.BLOCK_ENTITY, new LegacyTileEntityIdFix(PowerAdvantage.MODID, 1));
		dataFixersRegistered = true;
	}

	public static void init() {
		if (initDone) return;

		Blocks.init();

		registerTileEntity(TerminalFluidPipeTileEntity.class, "tileentity.pipe_terminal");
		registerTileEntity(FluidDrainTileEntity.class, "tileentity.fluid_drain");
		registerTileEntity(FluidDischargeTileEntity.class, "tileentity.fluid_discharge");
		registerTileEntity(StorageTankTileEntity.class, "tileentity.fluid_storage_tank");
		registerTileEntity(MetalTankTileEntity.class, "tileentity.fluid_metal_tank");
		registerTileEntity(StillTileEntity.class, "tileentity.still");
		registerTileEntity(TileEntityConveyor.class, "item_conveyor");
		registerTileEntity(TileEntityBlockFilter.class, "item_filter_block");
		registerTileEntity(TileEntityFoodFilter.class, "item_filter_food");
		registerTileEntity(TileEntityFuelFilter.class, "item_filter_fuel");
		registerTileEntity(TileEntityInventoryFilter.class, "item_filter_inventory");
		registerTileEntity(TileEntityOreFilter.class, "item_filter_ore");
		registerTileEntity(TileEntityPlantFilter.class, "item_filter_plant");
		registerTileEntity(TileEntitySmeltableFilter.class, "item_filter_smelt");
		registerTileEntity(TileEntityOverflowFilter.class, "item_filter_overflow");

		registerTileEntity(InfiniteEnergyTileEntity.class, "infinite_energy_source");

		initDone = true;
	}

	public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String path) {
		GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(PowerAdvantage.MODID, path));
	}

	private static final class LegacyTileEntityIdFix implements IFixableData {
		private final String modid;
		private final int version;

		private LegacyTileEntityIdFix(String modid, int version) {
			this.modid = modid;
			this.version = version;
		}

		@Override
		public int getFixVersion() {
			return version;
		}

		@Override
		public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
			if (compound.hasKey("id", 8)) {
				String id = compound.getString("id");
				String legacyPrefix = modid + ".";
				String legacyMinecraftPrefix = "minecraft:" + legacyPrefix;
				if (id.startsWith(legacyMinecraftPrefix)) {
					compound.setString("id", modid + ":" + id.substring(legacyMinecraftPrefix.length()));
				} else if (id.startsWith(legacyPrefix)) {
					compound.setString("id", modid + ":" + id.substring(legacyPrefix.length()));
				}
			}
			return compound;
		}
	}
}
