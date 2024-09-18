package net.smileycorp.crossbows.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.smileycorp.crossbows.common.item.FireworksDispenseBehaviour;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.syncConfig(event);
		if (ConfigHandler.dispenserFireworks) BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.FIREWORKS, new FireworksDispenseBehaviour());
	}

	public void init(FMLInitializationEvent event) {
		CriteriaTriggers.register(CrossbowsContent.SHOT_CROSSBOW);
		CriteriaTriggers.register(CrossbowsContent.PIERCED_ENTITIES);
	}
	
	public void postInit(FMLPostInitializationEvent event) {}
	
}
