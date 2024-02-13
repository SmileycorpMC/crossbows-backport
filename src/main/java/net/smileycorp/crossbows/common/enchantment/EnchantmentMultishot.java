package net.smileycorp.crossbows.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.smileycorp.crossbows.common.Constants;
import net.smileycorp.crossbows.common.CrossbowsContent;

public class EnchantmentMultishot extends Enchantment {

    public EnchantmentMultishot() {
        super(Rarity.RARE, CrossbowsContent.CROSSBOW_ENCHANTMENTS, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        setRegistryName(Constants.loc("multishot"));
        setName(Constants.name("multishot"));
    }

    @Override
    public int getMinEnchantability(int level) {
        return 20;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return super.canApplyTogether(enchantment) && enchantment != CrossbowsContent.PIERCING;
    }

}
