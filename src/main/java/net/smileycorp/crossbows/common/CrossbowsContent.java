package net.smileycorp.crossbows.common;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.smileycorp.crossbows.common.enchantment.EnchantmentMultishot;
import net.smileycorp.crossbows.common.enchantment.EnchantmentPiercing;
import net.smileycorp.crossbows.common.enchantment.EnchantmentQuickCharge;
import net.smileycorp.crossbows.common.item.ItemCrossbow;

@EventBusSubscriber(modid = Constants.MODID)
public class CrossbowsContent {
	
	public static final CrossbowsCriterionTrigger SHOT_CROSSBOW = new CrossbowsCriterionTrigger("shot_crossbow");
	public static final CrossbowsCriterionTrigger PIERCED_ENTITIES = new CrossbowsCriterionTrigger("pierced_entities");
	
	public static Item CROSSBOW;

	public static final EnumEnchantmentType CROSSBOW_ENCHANTMENTS = EnumHelper.addEnchantmentType("crossbow", item -> item instanceof ItemCrossbow);

	public static Enchantment QUICK_CHARGE = new EnchantmentQuickCharge();
	public static Enchantment MULTISHOT = new EnchantmentMultishot();
	public static Enchantment PIERCING = new EnchantmentPiercing();

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		CROSSBOW = new ItemCrossbow(Constants.MODID, "crossbow", ConfigHandler.durability,  ConfigHandler.damage, ConfigHandler.drawSpeed);
		event.getRegistry().register(CROSSBOW);
	}

	@SubscribeEvent
	public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
		IForgeRegistry<Enchantment> registry = event.getRegistry();
		registry.register(QUICK_CHARGE);
		registry.register(MULTISHOT);
		registry.register(PIERCING);
	}
	
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		IForgeRegistry<SoundEvent> registry = event.getRegistry();
		registry.register(Constants.CROSSBOW_LOADING_END);
		registry.register(Constants.CROSSBOW_LOADING_MIDDLE);
		registry.register(Constants.CROSSBOW_LOADING_START);
		registry.register(Constants.CROSSBOW_QUICK_CHARGE_1);
		registry.register(Constants.CROSSBOW_QUICK_CHARGE_2);
		registry.register(Constants.CROSSBOW_QUICK_CHARGE_3);
		registry.register(Constants.CROSSBOW_SHOOT);
	}
}
