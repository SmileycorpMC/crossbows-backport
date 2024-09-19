package net.smileycorp.crossbows.mixin;

import net.minecraft.item.Item;
import net.smileycorp.crossbows.common.ConfigHandler;
import net.smileycorp.crossbows.common.item.ItemCrossbow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.enchantment.EnumEnchantmentType$11")
public class MixinEnumEnchantmentType$BOW {
    
    @Inject(method = "canEnchantItem", at = @At(value = "HEAD"), cancellable = true)
    public void onItemUse$spawnEntity(Item itemIn, CallbackInfoReturnable<Boolean> callback) {
        if (ConfigHandler.bowEnchantments && itemIn instanceof ItemCrossbow) callback.setReturnValue(true);
    }
    
}
