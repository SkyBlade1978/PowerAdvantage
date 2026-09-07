package com.mcmoddev.advantageworks;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
        modid = AdvantageWorksMod.MODID,
        name = "Advantage Works Test Harness",
        version = AdvantageWorksMod.VERSION,
        acceptableRemoteVersions = "*"
)
public final class AdvantageWorksMod {
    public static final String MODID = "advantageworkstest";
    public static final String VERSION = "1.0.0.110020";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        WorksController.getInstance().loadDefinition();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new AdvantageWorksCommand());
    }
}
