package net.smileycorp.crossbows.common;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class Constants {
	
	public static final String NAME = "Crossbows";
	public static final String MODID = "crossbows";
	public static final String VERSION = "1.1.0a";
	public static final String DEPENDENCIES = "";
	public static final String PATH = "net.smileycorp.crossbows.";
	public static final String CLIENT = PATH + "client.ClientProxy";
	public static final String SERVER = PATH + "common.CommonProxy";
	
	public static final SoundEvent CROSSBOW_LOADING_END = new SoundEvent(Constants.loc("item.crossbow.loading_end"));
	public static final SoundEvent CROSSBOW_LOADING_MIDDLE = new SoundEvent(Constants.loc("item.crossbow.loading_middle"));
	public static final SoundEvent CROSSBOW_LOADING_START = new SoundEvent(Constants.loc("item.crossbow.loading_start"));
	public static final SoundEvent CROSSBOW_QUICK_CHARGE_1 = new SoundEvent(Constants.loc("item.crossbow.quick_charge_1"));
	public static final SoundEvent CROSSBOW_QUICK_CHARGE_2 = new SoundEvent(Constants.loc("item.crossbow.quick_charge_2"));
	public static final SoundEvent CROSSBOW_QUICK_CHARGE_3 = new SoundEvent(Constants.loc("item.crossbow.quick_charge_3"));
	public static final SoundEvent CROSSBOW_SHOOT = new SoundEvent(Constants.loc("item.crossbow.shoot"));
	
	public static String name(String name) {
		return MODID + "." + name.replace("_", "");
	}
	
	public static ResourceLocation loc(String name) {
		return new ResourceLocation(MODID, name.toLowerCase());
	}
	
}
