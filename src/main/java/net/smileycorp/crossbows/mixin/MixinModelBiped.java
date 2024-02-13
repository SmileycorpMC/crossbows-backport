package net.smileycorp.crossbows.mixin;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.smileycorp.crossbows.client.CrossbowAnimator;
import net.smileycorp.crossbows.common.CrossbowsContent;
import net.smileycorp.crossbows.common.item.ItemCrossbow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public class MixinModelBiped {

    @Shadow public ModelRenderer bipedRightArm;
    @Shadow public ModelRenderer bipedLeftArm;

    @Shadow public ModelBiped.ArmPose leftArmPose;

    @Shadow public ModelBiped.ArmPose rightArmPose;

    @Shadow public ModelRenderer bipedHead;

    @Inject(method = "setRotationAngles", at = @At("HEAD"), cancellable = true)
    public void setRotationAngles$HEAD(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
        if (entityIn instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityIn;
            if (player.getActiveItemStack().getItem() == CrossbowsContent.CROSSBOW) {
                leftArmPose = ModelBiped.ArmPose.EMPTY;
                rightArmPose = ModelBiped.ArmPose.EMPTY;
                return;
            }
            if (player.getHeldItemMainhand().getItem() == CrossbowsContent.CROSSBOW && ItemCrossbow.isCharged(player.getHeldItemMainhand())) {
                if (player.getPrimaryHand() == EnumHandSide.RIGHT) rightArmPose = ModelBiped.ArmPose.EMPTY;
                else leftArmPose = ModelBiped.ArmPose.EMPTY;
            }
            if (player.getHeldItemOffhand().getItem() == CrossbowsContent.CROSSBOW && ItemCrossbow.isCharged(player.getHeldItemOffhand())) {
                if (player.getPrimaryHand() == EnumHandSide.LEFT) rightArmPose = ModelBiped.ArmPose.EMPTY;
                else leftArmPose = ModelBiped.ArmPose.EMPTY;
            }
        }
    }

    @Inject(method = "setRotationAngles", at = @At("TAIL"), cancellable = true)
    public void setRotationAngles$TAIL(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
        if (entityIn instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityIn;
            if (player.getActiveItemStack().getItem() == CrossbowsContent.CROSSBOW) {
                CrossbowAnimator.animateCharge(player, bipedRightArm, bipedLeftArm);
                return;
            }
            ItemStack mainhand = player.getHeldItemMainhand();
            ItemStack offhand = player.getHeldItemOffhand();
            if (offhand.getItem() == CrossbowsContent.CROSSBOW && ItemCrossbow.isCharged(offhand)) {
                CrossbowAnimator.animateCrossbowHold(bipedRightArm, bipedLeftArm, bipedHead, player.getPrimaryHand() == EnumHandSide.LEFT);
            }
            if (mainhand.getItem() == CrossbowsContent.CROSSBOW && ItemCrossbow.isCharged(mainhand)) {
                CrossbowAnimator.animateCrossbowHold(bipedRightArm, bipedLeftArm, bipedHead, player.getPrimaryHand() == EnumHandSide.RIGHT);
            }
        }
    }
    
}
