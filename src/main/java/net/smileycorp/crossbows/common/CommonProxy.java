package net.smileycorp.crossbows.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.smileycorp.crossbows.common.entities.IFireworksProjectile;
import net.smileycorp.crossbows.common.item.FireworksDispenseBehaviour;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.FIREWORKS, new FireworksDispenseBehaviour());
	}

	public void init(FMLInitializationEvent event) {
		CriteriaTriggers.register(CrossbowsContent.SHOT_CROSSBOW);
		CriteriaTriggers.register(CrossbowsContent.PIERCED_ENTITIES);
	}
	
	public void postInit(FMLPostInitializationEvent event) {}
	
}
