package net.smileycorp.crossbows.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {}

	public void init(FMLInitializationEvent event) {
		CriteriaTriggers.register(CrossbowsContent.SHOT_CROSSBOW);
		CriteriaTriggers.register(CrossbowsContent.PIERCED_ENTITIES);
	}
	
	public void postInit(FMLPostInitializationEvent event) {}
	
}
