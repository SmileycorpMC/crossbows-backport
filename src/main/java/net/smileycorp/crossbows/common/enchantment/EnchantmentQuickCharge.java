package net.smileycorp.crossbows.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.smileycorp.crossbows.common.Constants;
import net.smileycorp.crossbows.common.CrossbowsContent;

public class EnchantmentQuickCharge extends Enchantment {
    
    public EnchantmentQuickCharge() {
        super(Rarity.UNCOMMON, CrossbowsContent.CROSSBOW_ENCHANTMENTS, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        setRegistryName(Constants.loc("quick_charge"));
        setName(Constants.name("quick_charge"));
    }
    
    @Override
    public int getMinEnchantability(int level) {
        return 12 + (level - 1) * 20;
    }
    
    @Override
    public int getMaxEnchantability(int level) {
        return 50;
    }
    
    @Override
    public int getMaxLevel() {
        return 3;
    }
    
}
