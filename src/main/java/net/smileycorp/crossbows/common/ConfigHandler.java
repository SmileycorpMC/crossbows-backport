package net.smileycorp.crossbows.common;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler {
    
    //crossbow
    public static int durability;
    public static float damage;
    public static int drawSpeed;
    public static int enchantability;
    public static boolean bowEnchantments;
    //general
    public static boolean dispenserFireworks;
    
    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        try{
            config.load();
        durability = config.getInt("durability", "crossbow", 265, 0, Integer.MAX_VALUE, "How much durability do crossbows have?");
        drawSpeed = config.getInt("drawSpeed", "crossbow", 25, 0, Integer.MAX_VALUE, "How many ticks does it take to pull back a crossbow?");
        damage = config.getFloat("damage", "crossbow", 1, 0, Integer.MAX_VALUE, "Base damage of crossbow arrows in hearts.");
        enchantability = config.getInt("enchantability", "crossbow", 1, 0, Integer.MAX_VALUE, "How enchantable are crossbows in the enchanting table?");
        bowEnchantments = config.getBoolean("bowEnchantments", "crossbow", false, "Can crossbows be enchanted with bow enchantments?");
        
        dispenserFireworks = config.getBoolean("dispenserFireworks", "general", true, "Should fireworks use their 1.15+ behaviour in dispensers?");
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
    
}
